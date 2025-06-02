package org.Esprit.TripNShip.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Services.AuthService;
import org.Esprit.TripNShip.Utils.UserSession;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FaceRegistrationController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(FaceRegistrationController.class);

    static {
        try {
            nu.pattern.OpenCV.loadLocally();
        } catch (Exception e) {
            System.err.println("Failed to load OpenCV: " + e.getMessage());
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        }
    }

    // FXML Components
    @FXML private ImageView cameraImageView;
    @FXML private Button startCameraBtn;
    @FXML private Button stopCameraBtn;
    @FXML private Label statusLabel;
    @FXML private ProgressBar captureProgress;
    @FXML private Label instructionLabel;
    @FXML private Circle qualityIndicator; // Visual quality indicator

    // OpenCV Components
    private VideoCapture camera;
    private CascadeClassifier faceDetector;
    private ScheduledExecutorService cameraTimer;
    private volatile boolean cameraActive = false;

    // Face Capture Progress System
    private static final int REQUIRED_SAMPLES = 5; // Number of good quality samples needed
    private static final double MIN_FACE_SIZE = 80.0; // Minimum face size in pixels
    private static final double MAX_FACE_SIZE = 300.0; // Maximum face size in pixels
    private static final double MIN_QUALITY_SCORE = 0.7; // Minimum quality threshold

    private List<String> capturedFaces = new ArrayList<>(); // Store good quality faces
    private int consecutiveGoodFrames = 0;
    private long lastQualityCheck = 0;
    private static final long QUALITY_CHECK_INTERVAL = 500; // Check quality every 500ms

    // Quality metrics
    private double currentFaceSize = 0;
    private double currentSharpness = 0;
    private boolean isFaceCentered = false;
    private boolean hasGoodLighting = false;

    private int currentUserId = 75;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeOpenCV();
        initializeUI();
        Platform.runLater(this::startCamera);
    }

    private void initializeUI() {
        captureProgress.setProgress(0.0);
        instructionLabel.setText("Position your face in the center of the camera");
        qualityIndicator.setFill(Color.RED);
    }

    private void initializeOpenCV() {
        camera = new VideoCapture();
        faceDetector = new CascadeClassifier();

        try {
            URL resource = getClass().getResource("/haarcascade_frontalface_alt.xml");
            String classifierPath = new File(resource.toURI()).getAbsolutePath();

            if (!faceDetector.load(classifierPath)) {
                updateStatus("Error: Could not load face detection classifier");
            } else {
                updateStatus("Face detection system initialized");
            }
        } catch (Exception e) {
            System.err.println("Error initializing OpenCV: " + e.getMessage());
        }
    }

    @FXML
    private void startCamera() {
        if (!cameraActive) {
            camera.open(0);
            if (camera.isOpened()) {
                cameraActive = true;
                startCameraBtn.setDisable(true);
                stopCameraBtn.setDisable(false);

                // Reset capture progress
                capturedFaces.clear();
                consecutiveGoodFrames = 0;
                updateProgress();

                cameraTimer = Executors.newSingleThreadScheduledExecutor();
                cameraTimer.scheduleAtFixedRate(this::processFrame, 0, 33, TimeUnit.MILLISECONDS);

                updateStatus("Camera started - Position your face for registration");
            } else {
                updateStatus("Error: Cannot access camera");
            }
        }
    }

    @FXML
    private void stopCamera() {
        if (cameraActive) {
            cameraActive = false;

            if (cameraTimer != null && !cameraTimer.isShutdown()) {
                cameraTimer.shutdown();
            }

            if (camera.isOpened()) {
                camera.release();
            }

            startCameraBtn.setDisable(false);
            stopCameraBtn.setDisable(true);
            Platform.runLater(() -> cameraImageView.setImage(null));
            updateStatus("Camera stopped");
        }
    }

    private void processFrame() {
        if (cameraActive && camera.isOpened()) {
            Mat frame = new Mat();
            if (camera.read(frame)) {
                Mat grayFrame = new Mat();
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

                // Detect faces
                MatOfRect faceDetections = new MatOfRect();
                faceDetector.detectMultiScale(grayFrame, faceDetections, 1.1, 3,
                        0, new Size(30, 30), new Size());

                Rect[] faces = faceDetections.toArray();

                if (faces.length > 0) {
                    // Find the largest face
                    Rect largestFace = faces[0];
                    for (Rect face : faces) {
                        if (face.area() > largestFace.area()) {
                            largestFace = face;
                        }
                    }

                    // Analyze face quality
                    FaceQuality quality = analyzeFaceQuality(frame, grayFrame, largestFace);

                    // Draw face rectangle with quality-based color
                    Scalar rectColor = getQualityColor(quality.overallScore);
                    Imgproc.rectangle(frame, largestFace.tl(), largestFace.br(), rectColor, 3);

                    // Add quality indicators on frame
                    addQualityIndicators(frame, largestFace, quality);

                    // Update UI with quality feedback
                    Platform.runLater(() -> updateQualityFeedback(quality));

                    // Capture face if quality is good enough
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastQualityCheck > QUALITY_CHECK_INTERVAL) {
                        if (quality.overallScore >= MIN_QUALITY_SCORE) {
                            consecutiveGoodFrames++;
                            if (consecutiveGoodFrames >= 3) { // Need 3 consecutive good frames
                                captureFaceData(frame, largestFace, quality);
                                consecutiveGoodFrames = 0; // Reset for next capture
                            }
                        } else {
                            consecutiveGoodFrames = 0; // Reset if quality drops
                        }
                        lastQualityCheck = currentTime;
                    }

                } else {
                    Platform.runLater(() -> {
                        updateStatus("No face detected - Position yourself in front of camera");
                        instructionLabel.setText("Move closer to the camera");
                        qualityIndicator.setFill(Color.RED);
                    });
                    consecutiveGoodFrames = 0;
                }

                // Update camera view
                Platform.runLater(() -> {
                    Image imageToShow = matToImage(frame);
                    cameraImageView.setImage(imageToShow);
                });
            }
        }
    }

    private FaceQuality analyzeFaceQuality(Mat colorFrame, Mat grayFrame, Rect faceRect) {
        FaceQuality quality = new FaceQuality();

        // 1. Face Size Analysis
        double faceArea = faceRect.area();
        currentFaceSize = Math.sqrt(faceArea);

        if (currentFaceSize < MIN_FACE_SIZE) {
            quality.sizeScore = 0.3; // Too small
        } else if (currentFaceSize > MAX_FACE_SIZE) {
            quality.sizeScore = 0.4; // Too large
        } else {
            // Optimal size range
            double optimalSize = (MIN_FACE_SIZE + MAX_FACE_SIZE) / 2;
            quality.sizeScore = 1.0 - Math.abs(currentFaceSize - optimalSize) / optimalSize;
        }

        // 2. Face Position Analysis (centered?)
        int frameWidth = colorFrame.width();
        int frameHeight = colorFrame.height();
        int faceCenterX = faceRect.x + faceRect.width / 2;
        int faceCenterY = faceRect.y + faceRect.height / 2;
        int frameCenterX = frameWidth / 2;
        int frameCenterY = frameHeight / 2;

        double distanceFromCenter = Math.sqrt(
                Math.pow(faceCenterX - frameCenterX, 2) +
                        Math.pow(faceCenterY - frameCenterY, 2)
        );
        double maxDistance = Math.sqrt(Math.pow(frameWidth/2, 2) + Math.pow(frameHeight/2, 2));
        quality.positionScore = 1.0 - (distanceFromCenter / maxDistance);
        isFaceCentered = quality.positionScore > 0.7;

        // 3. Sharpness Analysis (using Laplacian variance)
        Mat faceGray = new Mat(grayFrame, faceRect);
        Mat laplacian = new Mat();
        Imgproc.Laplacian(faceGray, laplacian, CvType.CV_64F);

//        Scalar mean = new Scalar(0);
//        Scalar stddev = new Scalar(0);
//        Core.meanStdDev(laplacian, mean, stddev);
//        currentSharpness = stddev.val[0] * stddev.val[0]; // Variance

        MatOfDouble mean = new MatOfDouble();
        MatOfDouble stddev = new MatOfDouble();
        Core.meanStdDev(laplacian, mean, stddev);
        currentSharpness = Math.pow(stddev.get(0, 0)[0], 2);

        // Normalize sharpness score (typical good values are > 100)
        quality.sharpnessScore = Math.min(currentSharpness / 200.0, 1.0);

        // 4. Lighting Analysis
        Scalar meanBrightness = Core.mean(faceGray);
        double brightness = meanBrightness.val[0];

        // Good lighting is between 50-200 (0-255 scale)
        if (brightness < 50 || brightness > 200) {
            quality.lightingScore = 0.3;
            hasGoodLighting = false;
        } else {
            quality.lightingScore = 1.0 - Math.abs(brightness - 125) / 125.0;
            hasGoodLighting = quality.lightingScore > 0.6;
        }

        // 5. Calculate Overall Score
        quality.overallScore = (quality.sizeScore * 0.3 +
                quality.positionScore * 0.25 +
                quality.sharpnessScore * 0.25 +
                quality.lightingScore * 0.2);

        return quality;
    }

    private void captureFaceData(Mat frame, Rect faceRect, FaceQuality quality) {
        if (capturedFaces.size() >= REQUIRED_SAMPLES) {
            return; // Already captured enough samples
        }

        try {
            Mat detectedFace = new Mat(frame, faceRect);
            String faceDataString = convertFaceToBase64String(detectedFace);

            capturedFaces.add(faceDataString);

            Platform.runLater(() -> {
                updateProgress();
                updateStatus(String.format("Captured %d/%d samples (Quality: %.1f%%)",
                        capturedFaces.size(), REQUIRED_SAMPLES, quality.overallScore * 100));

                if (capturedFaces.size() >= REQUIRED_SAMPLES) {
                    completeFaceRegistration();
                }
            });

            System.out.println("=== FACE SAMPLE CAPTURED ===");
            System.out.println("Sample: " + capturedFaces.size() + "/" + REQUIRED_SAMPLES);
            System.out.println("Quality Score: " + String.format("%.2f", quality.overallScore));
            System.out.println("Face Data: " + faceDataString.substring(0, Math.min(50, faceDataString.length())) + "...");

        } catch (Exception e) {
            System.err.println("Error capturing face data: " + e.getMessage());
        }
    }

    private void completeFaceRegistration() {
        stopCamera();

        // Here you would save all captured faces to database
        // You can use the best quality samples or create an average template

        updateStatus("✓ Face registration completed successfully!");
        instructionLabel.setText("Registration complete! You can now use face login.");
        qualityIndicator.setFill(Color.GREEN);

        System.out.println("=== FACE REGISTRATION COMPLETE ===");
        System.out.println("Total samples captured: " + capturedFaces.size());
        System.out.println("Ready for face recognition login!");
        System.out.println(capturedFaces);

        // TODO: Save capturedFaces list to database
         saveFaceDataToDatabase(capturedFaces);
    }

    private void saveFaceDataToDatabase(List<String> capturedFaces) {
        AuthService authService = new AuthService();
        authService.addFaceRecognitionForUser(currentUserId,capturedFaces);
        Stage stage = (Stage) startCameraBtn.getScene().getWindow();
        stage.close();
    }

    private void updateQualityFeedback(FaceQuality quality) {
        // Update quality indicator color
        if (quality.overallScore >= 0.8) {
            qualityIndicator.setFill(Color.GREEN);
        } else if (quality.overallScore >= 0.6) {
            qualityIndicator.setFill(Color.YELLOW);
        } else {
            qualityIndicator.setFill(Color.RED);
        }

        // Update instruction text based on quality issues
        String instruction = getQualityInstruction(quality);
        instructionLabel.setText(instruction);

        updateStatus(String.format("Quality: %.0f%% | Samples: %d/%d",
                quality.overallScore * 100, capturedFaces.size(), REQUIRED_SAMPLES));
    }

    private String getQualityInstruction(FaceQuality quality) {
        if (quality.sizeScore < 0.5) {
            return currentFaceSize < MIN_FACE_SIZE ? "Move closer to the camera" : "Move further from the camera";
        }
        if (quality.positionScore < 0.6) {
            return "Center your face in the frame";
        }
        if (quality.sharpnessScore < 0.5) {
            return "Hold still - image is blurry";
        }
        if (quality.lightingScore < 0.5) {
            return "Improve lighting - face too dark/bright";
        }
        if (quality.overallScore >= MIN_QUALITY_SCORE) {
            return "Perfect! Hold steady...";
        }
        return "Adjust position for better quality";
    }

    private Scalar getQualityColor(double score) {
        if (score >= 0.8) return new Scalar(0, 255, 0); // Green
        if (score >= 0.6) return new Scalar(0, 255, 255); // Yellow
        return new Scalar(0, 0, 255); // Red
    }

    private void addQualityIndicators(Mat frame, Rect faceRect, FaceQuality quality) {
        // Add quality score text
        String qualityText = String.format("Quality: %.0f%%", quality.overallScore * 100);
        Imgproc.putText(frame, qualityText,
                new Point(faceRect.x, faceRect.y - 10),
                Imgproc.FONT_HERSHEY_SIMPLEX, 0.7,
                getQualityColor(quality.overallScore), 2);

        // Add center guide (crosshair)
        int centerX = frame.width() / 2;
        int centerY = frame.height() / 2;
        Imgproc.line(frame, new Point(centerX - 20, centerY), new Point(centerX + 20, centerY),
                new Scalar(255, 255, 255), 1);
        Imgproc.line(frame, new Point(centerX, centerY - 20), new Point(centerX, centerY + 20),
                new Scalar(255, 255, 255), 1);
    }

    private void updateProgress() {
        double progress = (double) capturedFaces.size() / REQUIRED_SAMPLES;
        captureProgress.setProgress(progress);
    }

    // Helper classes and methods
    private static class FaceQuality {
        double sizeScore = 0.0;
        double positionScore = 0.0;
        double sharpnessScore = 0.0;
        double lightingScore = 0.0;
        double overallScore = 0.0;
    }

    private String convertFaceToBase64String(Mat faceMat) {
        Mat resizedFace = new Mat();
        Size faceSize = new Size(100, 100); // Standard size for all faces
        Imgproc.resize(faceMat, resizedFace, faceSize);

        // Convert to bytes
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", resizedFace, matOfByte);
        byte[] byteArray = matOfByte.toArray();

        // Convert to Base64 string
        return Base64.getEncoder().encodeToString(byteArray);
    }

    private Image matToImage(Mat mat) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", mat, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        return new Image(new ByteArrayInputStream(byteArray));
    }

    private void updateStatus(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }

    public void cleanup() {
        stopCamera();
    }
}