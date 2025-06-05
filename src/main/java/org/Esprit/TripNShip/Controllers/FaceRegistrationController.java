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
import org.Esprit.TripNShip.Services.FaceAuthService;
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
    @FXML
    private ImageView cameraImageView;
    @FXML
    private Button startCameraBtn;
    @FXML
    private Button stopCameraBtn;
    @FXML
    private Label statusLabel;
    @FXML
    private ProgressBar captureProgress;
    @FXML
    private Label instructionLabel;
    @FXML
    private Circle qualityIndicator;

    // OpenCV Components
    private VideoCapture camera;
    private CascadeClassifier faceDetector;
    private CascadeClassifier eyeDetector;
    private CascadeClassifier mouthDetector;
    private ScheduledExecutorService cameraTimer;
    private volatile boolean cameraActive = false;

    // Face Capture Progress System
    private static final int REQUIRED_SAMPLES = 5;
    private static final double MIN_FACE_SIZE = 80.0;
    private static final double MAX_FACE_SIZE = 300.0;
    private static final double MIN_QUALITY_SCORE = 0.75;

    private List<String> capturedFaces = new ArrayList<>();
    private int consecutiveGoodFrames = 0;
    private long lastQualityCheck = 0;
    private static final long QUALITY_CHECK_INTERVAL = 500;

    // Quality metrics
    private double currentFaceSize = 0;
    private double currentSharpness = 0;
    private boolean isFaceCentered = false;
    private boolean hasGoodLighting = false;
    private boolean eyesDetected = false;
    private boolean mouthDetected = false;

    private int currentUserId = UserSession.getInstance().getUserId();

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
        eyeDetector = new CascadeClassifier();
        mouthDetector = new CascadeClassifier();

        try {
            // Load face detector
            URL faceResource = getClass().getResource("/haarcascade_frontalface_default.xml");
            String faceClassifierPath = new File(faceResource.toURI()).getAbsolutePath();

            // Load eye detector
            URL eyeResource = getClass().getResource("/haarcascade_eye.xml");
            String eyeClassifierPath = new File(eyeResource.toURI()).getAbsolutePath();

            // Load mouth detector
            URL mouthResource = getClass().getResource("/haarcascade_mcs_mouth.xml");
            String mouthClassifierPath = new File(mouthResource.toURI()).getAbsolutePath();

            if (!faceDetector.load(faceClassifierPath)) {
                updateStatus("Error: Could not load face detection classifier");
                return;
            }

            if (!eyeDetector.load(eyeClassifierPath)) {
                updateStatus("Warning: Could not load eye detection classifier");
            }

            if (!mouthDetector.load(mouthClassifierPath)) {
                updateStatus("Warning: Could not load mouth detection classifier");
            }

            updateStatus("Face detection system initialized");

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

                    // Analyze face quality including eyes and mouth
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
                            if (consecutiveGoodFrames >= 3) {
                                captureFaceData(frame, largestFace, quality);
                                consecutiveGoodFrames = 0;
                            }
                        } else {
                            consecutiveGoodFrames = 0;
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
            quality.sizeScore = 0.3;
        } else if (currentFaceSize > MAX_FACE_SIZE) {
            quality.sizeScore = 0.4;
        } else {
            double optimalSize = (MIN_FACE_SIZE + MAX_FACE_SIZE) / 2;
            quality.sizeScore = 1.0 - Math.abs(currentFaceSize - optimalSize) / optimalSize;
        }

        // 2. Face Position Analysis
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
        double maxDistance = Math.sqrt(Math.pow(frameWidth / 2, 2) + Math.pow(frameHeight / 2, 2));
        quality.positionScore = 1.0 - (distanceFromCenter / maxDistance);
        isFaceCentered = quality.positionScore > 0.7;

        // 3. Sharpness Analysis
        Mat faceGray = new Mat(grayFrame, faceRect);
        Mat laplacian = new Mat();
        Imgproc.Laplacian(faceGray, laplacian, CvType.CV_64F);

        MatOfDouble mean = new MatOfDouble();
        MatOfDouble stddev = new MatOfDouble();
        Core.meanStdDev(laplacian, mean, stddev);
        currentSharpness = Math.pow(stddev.get(0, 0)[0], 2);

        quality.sharpnessScore = Math.min(currentSharpness / 200.0, 1.0);

        // 4. Lighting Analysis
        Scalar meanBrightness = Core.mean(faceGray);
        double brightness = meanBrightness.val[0];

        if (brightness < 50 || brightness > 200) {
            quality.lightingScore = 0.3;
            hasGoodLighting = false;
        } else {
            quality.lightingScore = 1.0 - Math.abs(brightness - 125) / 125.0;
            hasGoodLighting = quality.lightingScore > 0.6;
        }

        // 5. Eye Detection Analysis
        quality.eyeScore = detectEyes(grayFrame, faceRect);
        eyesDetected = quality.eyeScore > 0.5;

        // 6. Mouth Detection Analysis
        quality.mouthScore = detectMouth(grayFrame, faceRect);
        mouthDetected = quality.mouthScore > 0.3;

        // 7. Calculate Overall Score with weighted features
        quality.overallScore = (quality.sizeScore * 0.2 +
                quality.positionScore * 0.2 +
                quality.sharpnessScore * 0.2 +
                quality.lightingScore * 0.15 +
                quality.eyeScore * 0.15 +
                quality.mouthScore * 0.1);

        return quality;
    }

    private double detectEyes(Mat grayFrame, Rect faceRect) {
        try {
            // Define eye region (upper half of face)
            Rect eyeRegion = new Rect(
                    faceRect.x,
                    faceRect.y,
                    faceRect.width,
                    faceRect.height / 2
            );

            Mat eyeROI = new Mat(grayFrame, eyeRegion);
            MatOfRect eyeDetections = new MatOfRect();

            eyeDetector.detectMultiScale(eyeROI, eyeDetections, 1.1, 3,
                    0, new Size(10, 10), new Size());

            Rect[] eyes = eyeDetections.toArray();

            // Score based on number of eyes detected (ideally 2)
            if (eyes.length >= 2) {
                return 1.0; // Perfect - both eyes detected
            } else if (eyes.length == 1) {
                return 0.6; // Acceptable - one eye detected
            } else {
                return 0.0; // Poor - no eyes detected
            }

        } catch (Exception e) {
            return 0.0; // Error in detection
        }
    }

    private double detectMouth(Mat grayFrame, Rect faceRect) {
        try {
            // Define mouth region (lower third of face)
            int mouthY = faceRect.y + (int) (faceRect.height * 0.6);
            int mouthHeight = (int) (faceRect.height * 0.4);

            Rect mouthRegion = new Rect(
                    faceRect.x,
                    mouthY,
                    faceRect.width,
                    mouthHeight
            );

            Mat mouthROI = new Mat(grayFrame, mouthRegion);
            MatOfRect mouthDetections = new MatOfRect();

            mouthDetector.detectMultiScale(mouthROI, mouthDetections, 1.1, 3,
                    0, new Size(15, 15), new Size());

            Rect[] mouths = mouthDetections.toArray();

            // Score based on mouth detection
            if (mouths.length >= 1) {
                return 1.0; // Good - mouth detected
            } else {
                return 0.0; // Poor - no mouth detected
            }

        } catch (Exception e) {
            return 0.0; // Error in detection
        }
    }

    private void captureFaceData(Mat frame, Rect faceRect, FaceQuality quality) {
        if (capturedFaces.size() >= REQUIRED_SAMPLES) {
            return;
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

        } catch (Exception e) {
            System.err.println("Error capturing face data: " + e.getMessage());
        }
    }

    private void completeFaceRegistration() {
        stopCamera();

        updateStatus("✓ Face registration completed successfully!");
        instructionLabel.setText("Registration complete! You can now use face login.");
        qualityIndicator.setFill(Color.GREEN);

       saveFaceDataToDatabase(capturedFaces);
    }

    private void saveFaceDataToDatabase(List<String> capturedFaces) {
        FaceAuthService faceAuthService = new FaceAuthService();
        faceAuthService.addFaceRecognitionForUser(currentUserId, capturedFaces);
        Stage stage = (Stage) startCameraBtn.getScene().getWindow();
        stage.close();
    }

    private void updateQualityFeedback(FaceQuality quality) {
        if (quality.overallScore >= 0.8) {
            qualityIndicator.setFill(Color.GREEN);
        } else if (quality.overallScore >= 0.6) {
            qualityIndicator.setFill(Color.YELLOW);
        } else {
            qualityIndicator.setFill(Color.RED);
        }

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
        if (quality.eyeScore < 0.5) {
            return "Look directly at the camera - eyes not detected";
        }
        if (quality.mouthScore < 0.3) {
            return "Position face properly - mouth not visible";
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
        String qualityText = String.format("Quality: %.0f%%", quality.overallScore * 100);
        Imgproc.putText(frame, qualityText,
                new Point(faceRect.x, faceRect.y - 10),
                Imgproc.FONT_HERSHEY_SIMPLEX, 0.7,
                getQualityColor(quality.overallScore), 2);

        // Add feature detection indicators
        if (eyesDetected) {
            Imgproc.putText(frame, "Eyes: OK",
                    new Point(faceRect.x, faceRect.y + faceRect.height + 25),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.5,
                    new Scalar(0, 255, 0), 1);
        }

        if (mouthDetected) {
            Imgproc.putText(frame, "Mouth: OK",
                    new Point(faceRect.x + 80, faceRect.y + faceRect.height + 25),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.5,
                    new Scalar(0, 255, 0), 1);
        }

        // Add center guide
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

    private static class FaceQuality {
        double sizeScore = 0.0;
        double positionScore = 0.0;
        double sharpnessScore = 0.0;
        double lightingScore = 0.0;
        double eyeScore = 0.0;
        double mouthScore = 0.0;
        double overallScore = 0.0;
    }

    private String convertFaceToBase64String(Mat faceMat) {
        Mat resizedFace = new Mat();
        Size faceSize = new Size(100, 100);
        Imgproc.resize(faceMat, resizedFace, faceSize);

        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", resizedFace, matOfByte);
        byte[] byteArray = matOfByte.toArray();

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
