package org.Esprit.TripNShip.Controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Services.AuthService;
import org.Esprit.TripNShip.Utils.Shared;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FaceLoginController implements Initializable {

    @FXML
    private StackPane cameraContainer;

    @FXML
    private ImageView cameraPreview;

    @FXML
    private Label cameraStatusLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Button startCameraButton;

    @FXML
    private Button stopCameraButton;

    @FXML
    private Hyperlink regularLoginLink;

    @FXML
    private Hyperlink signUpLink;

    private VideoCapture capture;
    private ScheduledExecutorService timer;
    private ScheduledExecutorService captureTimer;
    private ScheduledFuture<?> timeoutTask;
    private CascadeClassifier faceDetector;
    private AuthService userService;
    private boolean cameraActive = false;
    private boolean autoCapturing = false;
    private long captureStartTime;
    private static final long CAPTURE_TIMEOUT = 60000; // 1 minute in milliseconds
    private static final long CAPTURE_INTERVAL = 2000; // Attempt capture every 2 seconds

    static {
        // Load OpenCV native library
        nu.pattern.OpenCV.loadLocally();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userService = new AuthService();

        try {
            // Initialize face detector
            URL resource = getClass().getResource("/haarcascade_frontalface_alt.xml");
            String classifierPath = new File(resource.toURI()).getAbsolutePath();
            faceDetector = new CascadeClassifier(classifierPath);

            if (faceDetector.empty()) {
                statusLabel.setText("Error: Could not load face detection classifier");
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ef4444; -fx-font-weight: bold;");
            } else {
                // Auto-start camera and capturing when the controller initializes
                Platform.runLater(this::startAutoCapture);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAutoCapture() {
        if (!cameraActive) {
            capture = new VideoCapture(0);

            if (capture.isOpened()) {
                cameraActive = true;
                autoCapturing = true;
                captureStartTime = System.currentTimeMillis();

                // Update UI
                startCameraButton.setVisible(false);
                stopCameraButton.setVisible(true);
                cameraStatusLabel.setVisible(false);
                statusLabel.setText("Auto-capturing started. Please position your face in the frame.");
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #10b981; -fx-font-weight: bold;");

                // Start grabbing frames
                timer = Executors.newSingleThreadScheduledExecutor();
                timer.scheduleAtFixedRate(this::grabFrame, 0, 33, TimeUnit.MILLISECONDS);

                // Start auto-capture attempts
                captureTimer = Executors.newSingleThreadScheduledExecutor();
                captureTimer.scheduleAtFixedRate(this::attemptAutoCapture, 1000, CAPTURE_INTERVAL, TimeUnit.MILLISECONDS);

                // Set timeout to stop capturing after 1 minute
                timeoutTask = captureTimer.schedule(this::onCaptureTimeout, CAPTURE_TIMEOUT, TimeUnit.MILLISECONDS);
            } else {
                statusLabel.setText("Error: Cannot access camera");
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ef4444; -fx-font-weight: bold;");
            }
        }
    }

    private void attemptAutoCapture() {
        if (!autoCapturing || !cameraActive) {
            return;
        }

        // Update remaining time
        long elapsed = System.currentTimeMillis() - captureStartTime;
        long remaining = (CAPTURE_TIMEOUT - elapsed) / 1000;

        Platform.runLater(() -> {
            statusLabel.setText("Auto-capturing... " + remaining + " seconds remaining");
            statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #f59e0b; -fx-font-weight: bold;");
        });

        Mat frame = new Mat();
        if (capture != null && capture.isOpened()) {
            capture.read(frame);

            if (!frame.empty()) {
                // Detect faces
                MatOfRect faceDetections = new MatOfRect();
                faceDetector.detectMultiScale(frame, faceDetections);

                Rect[] faces = faceDetections.toArray();
                if (faces.length > 0) {
                    // Face detected, attempt authentication
                    Platform.runLater(() -> {
                        statusLabel.setText("Face detected! Processing...");
                        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #3b82f6; -fx-font-weight: bold;");
                    });

                    processDetectedFace(frame, faces[0]);
                }
            }
        }
    }

    private void processDetectedFace(Mat frame, Rect faceRect) {
        Task<User> loginTask = new Task<User>() {
            @Override
            protected User call() throws Exception {
                Mat faceROI = new Mat(frame, faceRect);
                String capturedFaceData = matToBase64(faceROI);
                return userService.authenticateWithFace(capturedFaceData);
            }
        };

        loginTask.setOnSucceeded(e -> {
            User authenticatedUser = loginTask.getValue();
            if (authenticatedUser != null) {
                // Success - stop auto-capturing
                autoCapturing = false;
                Platform.runLater(() -> {
                    statusLabel.setText("Login successful! Welcome " + authenticatedUser.getLastName());
                    statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #10b981; -fx-font-weight: bold;");
                });

                // Stop camera and navigate
                stopCamera();
                Platform.runLater(() -> {
                    try {
                        // Store user session or pass user data as needed
                        // UserSession.setCurrentUser(authenticatedUser);
                        // navigateToMainApp();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
            // If authentication fails, continue auto-capturing
        });

        loginTask.setOnFailed(e -> {
            // Continue auto-capturing on failure
            System.err.println("Face authentication failed: " + loginTask.getException().getMessage());
        });

        Thread loginThread = new Thread(loginTask);
        loginThread.setDaemon(true);
        loginThread.start();
    }

    private void onCaptureTimeout() {
        if (autoCapturing) {
            autoCapturing = false;
            Platform.runLater(() -> {
                statusLabel.setText("Auto-capture timeout. No face recognized within 1 minute.");
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ef4444; -fx-font-weight: bold;");
                stopCamera();
            });
        }
    }

    @FXML
    private void startCamera() {
        startAutoCapture();
    }

    @FXML
    private void stopCamera() {
        if (cameraActive) {
            cameraActive = false;
            autoCapturing = false;

            // Cancel timeout task
            if (timeoutTask != null && !timeoutTask.isDone()) {
                timeoutTask.cancel(false);
            }

            // Stop the timers
            if (timer != null && !timer.isShutdown()) {
                timer.shutdown();
                try {
                    timer.awaitTermination(33, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    System.err.println("Exception in stopping the frame capture: " + e);
                }
            }

            if (captureTimer != null && !captureTimer.isShutdown()) {
                captureTimer.shutdown();
                try {
                    captureTimer.awaitTermination(100, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    System.err.println("Exception in stopping the capture timer: " + e);
                }
            }

            // Release the camera
            if (capture != null && capture.isOpened()) {
                capture.release();
            }

            // Update UI
            Platform.runLater(() -> {
                cameraPreview.setImage(null);
                cameraStatusLabel.setVisible(true);
                cameraStatusLabel.setText("Camera stopped");
                startCameraButton.setVisible(true);
                stopCameraButton.setVisible(false);
                statusLabel.setText("Click 'Start Camera' to begin");
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #374151; -fx-font-weight: bold;");
            });
        }
    }

    @FXML
    private void captureAndLogin() {
        // This method is now handled automatically by attemptAutoCapture
        if (autoCapturing) {
            statusLabel.setText("Auto-capture is already running...");
            statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #f59e0b; -fx-font-weight: bold;");
        }
    }

    private void grabFrame() {
        if (cameraActive && capture != null && capture.isOpened()) {
            Mat frame = new Mat();
            capture.read(frame);

            if (!frame.empty()) {
                // Detect faces and draw rectangles
                MatOfRect faceDetections = new MatOfRect();
                faceDetector.detectMultiScale(frame, faceDetections);

                Rect[] faces = faceDetections.toArray();
                for (Rect face : faces) {
                    Imgproc.rectangle(frame, new Point(face.x, face.y),
                            new Point(face.x + face.width, face.y + face.height),
                            new Scalar(0, 255, 0), 2);
                }

                // Convert Mat to Image and display
                Image imageToShow = matToImage(frame);
                Platform.runLater(() -> cameraPreview.setImage(imageToShow));
            }
        }
    }

    private Image matToImage(Mat mat) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", mat, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        return new Image(new ByteArrayInputStream(byteArray));
    }

    private String matToBase64(Mat mat) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", mat, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        return java.util.Base64.getEncoder().encodeToString(byteArray);
    }

    @FXML
    private void switchToRegularLogin(ActionEvent actionEvent) {
        Shared.switchScene(actionEvent,getClass().getResource("/fxml/login.fxml"),"Login");
    }

    private void navigateToMainApp() {
        try {
            Stage stage = (Stage) cameraPreview.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainApp.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cleanup() {
        stopCamera();
    }
}