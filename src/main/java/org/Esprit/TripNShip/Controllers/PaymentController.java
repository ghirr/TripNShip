package org.Esprit.TripNShip.Controllers;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.Esprit.TripNShip.Entities.AccommodationBooking;
import org.Esprit.TripNShip.Entities.BookingStatus;
import org.Esprit.TripNShip.Services.AccommodationBookingService;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PaymentController implements Initializable {

    @FXML private WebView paypalWebView;
    @FXML private Label bookingInfoLabel;
    @FXML private Label paymentStatusLabel;
    @FXML private Button backButton;
    @FXML private Button refreshButton;
    @FXML private Button cancelPaymentButton;

    private WebEngine webEngine;
    private Stage currentStage;
    private Stage popupStage;
    private AccommodationBooking currentBooking;
    private AccommodationBookingService bookingService;
    private boolean bridgeReady = false;
    private boolean paymentProcessing = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bookingService = new AccommodationBookingService();
        setupWebView();
    }

    public void setBooking(AccommodationBooking booking) {
        this.currentBooking = booking;
        updateBookingDisplay();

        // Load payment page with better timing
        Platform.runLater(() -> loadPaymentPage());
    }

    public void setCurrentStage(Stage stage) {
        this.currentStage = stage;
    }

    private void setupWebView() {
        webEngine = paypalWebView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        // Set user agent to avoid PayPal compatibility issues
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            System.out.println("WebEngine state changed: " + oldState + " -> " + newState);

            if (newState == Worker.State.SUCCEEDED) {
                System.out.println("Page loaded successfully, setting up bridge...");
                setupJavaScriptBridge();
            } else if (newState == Worker.State.FAILED) {
                Platform.runLater(() -> {
                    paymentStatusLabel.setText("Failed to load payment page");
                    showAlert("Error", "Failed to load PayPal payment page. Please check your internet connection.", Alert.AlertType.ERROR);
                });
            }
        });

        // Improved popup handler with better lifecycle management
        webEngine.setCreatePopupHandler(popupFeatures -> {
            System.out.println("Creating popup window for PayPal authentication");

            // Close existing popup if it exists
            if (popupStage != null && popupStage.isShowing()) {
                popupStage.close();
            }

            WebView popupWebView = new WebView();
            WebEngine popupEngine = popupWebView.getEngine();
            popupEngine.setJavaScriptEnabled(true);

            // Set the same user agent for popup
            popupEngine.setUserAgent(webEngine.getUserAgent());

            popupStage = new Stage();
            popupStage.setTitle("PayPal Authentication");
            popupStage.setScene(new Scene(popupWebView, 800, 600));

            // Add close event handler to cleanup
            popupStage.setOnCloseRequest(event -> {
                System.out.println("PayPal popup closed by user");
                // You might want to notify the main window about popup closure
            });

            popupStage.show();
            return popupEngine;
        });
    }

    // 2. Add better error handling and retry mechanism
    private void setupJavaScriptBridge() {
        CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
            Platform.runLater(() -> {
                int maxRetries = 3;
                setupBridgeWithRetry(0, maxRetries);
            });
        });
    }

    private void setupBridgeWithRetry(int attempt, int maxRetries) {
        try {
            // Inject JavaScript bridge
            JSObject window = (JSObject) webEngine.executeScript("window");
            window.setMember("javafx", new PaymentCallback());
            System.out.println("JavaScript bridge injected successfully (attempt " + (attempt + 1) + ")");

            // Test the bridge
            Object bridgeTestResult = webEngine.executeScript(
                    "typeof testJavaFXBridge === 'function' ? testJavaFXBridge() : false"
            );
            System.out.println("Bridge test result: " + bridgeTestResult);

            if (Boolean.TRUE.equals(bridgeTestResult)) {
                bridgeReady = true;
                updateWebPageBookingDetails();
                paymentStatusLabel.setText("Payment page ready");
            } else {
                throw new RuntimeException("Bridge test failed");
            }

        } catch (Exception e) {
            System.err.println("Error setting up JavaScript bridge (attempt " + (attempt + 1) + "): " + e.getMessage());

            if (attempt < maxRetries - 1) {
                // Retry after a delay
                CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS).execute(() -> {
                    Platform.runLater(() -> setupBridgeWithRetry(attempt + 1, maxRetries));
                });
            } else {
                // Final attempt failed
                e.printStackTrace();
                paymentStatusLabel.setText("Error: Failed to setup payment bridge");
                showAlert("Setup Error", "Failed to initialize payment system after " + maxRetries + " attempts: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void loadPaymentPage() {
        try {
            String htmlPath = "file://" + System.getProperty("user.dir") + "/src/main/resources/templates/paypal.html";
            System.out.println("Loading payment page from: " + htmlPath);
            webEngine.load(htmlPath);
            paymentStatusLabel.setText("Loading PayPal page...");
            bridgeReady = false;
        } catch (Exception e) {
            System.err.println("Load error: " + e.getMessage());
            e.printStackTrace();
            paymentStatusLabel.setText("Error loading payment page");
            showAlert("Load Error", "Failed to load payment page: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateBookingDisplay() {
        if (currentBooking != null) {
            bookingInfoLabel.setText(String.format("Booking #%d - %s",
                    currentBooking.getIdBooking(),
                    currentBooking.getRoom().getNameRoom()));
            System.out.println("Booking display updated for ID: " + currentBooking.getIdBooking());
        }
    }

    private void updateWebPageBookingDetails() {
        if (currentBooking == null) {
            System.err.println("Current booking is null, cannot update details");
            return;
        }

        if (!bridgeReady) {
            System.err.println("Bridge not ready, skipping booking details update");
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
            double amount = calculateTotalAmount();

            // Properly escape the room name for JavaScript
            String roomName = sanitizeStringForJavaScript(currentBooking.getRoom().getNameRoom());

            String script = String.format(
                    "try {" +
                            "  console.log('Updating booking details from Java...');" +
                            "  if (typeof updateBookingDetails === 'function') {" +
                            "    updateBookingDetails({" +
                            "      id: '%d'," +
                            "      roomName: '%s'," +
                            "      checkIn: '%s'," +
                            "      checkOut: '%s'," +
                            "      amount: '%.2f'" +
                            "    });" +
                            "    console.log('Booking details updated successfully');" +
                            "    true;" +
                            "  } else {" +
                            "    console.error('updateBookingDetails function not found');" +
                            "    false;" +
                            "  }" +
                            "} catch(e) {" +
                            "  console.error('Error updating booking details:', e);" +
                            "  false;" +
                            "}",
                    currentBooking.getIdBooking(),
                    roomName,
                    sdf.format(currentBooking.getStartDate()),
                    sdf.format(currentBooking.getEndDate()),
                    amount
            );

            Object result = webEngine.executeScript(script);
            System.out.println("Booking details update result: " + result);

            if (Boolean.TRUE.equals(result)) {
                paymentStatusLabel.setText("Ready for payment");
            } else {
                paymentStatusLabel.setText("Warning: Booking details may not be updated");
            }

        } catch (Exception e) {
            System.err.println("Error updating web page booking details: " + e.getMessage());
            e.printStackTrace();
            paymentStatusLabel.setText("Error updating booking details");
        }
    }

    private String sanitizeStringForJavaScript(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private double calculateTotalAmount() {
        if (currentBooking != null && currentBooking.getRoom() != null) {
            long diff = currentBooking.getEndDate().getTime() - currentBooking.getStartDate().getTime();
            long days = Math.max(1, diff / (1000 * 60 * 60 * 24));
            return days * currentBooking.getRoom().getPrice();
        }
        return 0.0;
    }

    public class PaymentCallback {
        public void paymentCallback(String status, Object details) {
            System.out.println("=== JAVA CALLBACK TRIGGERED ===");
            System.out.println("Status: " + status);
            System.out.println("Details type: " + (details != null ? details.getClass().getName() : "null"));
            System.out.println("Thread: " + Thread.currentThread().getName());

            Platform.runLater(() -> {
                try {
                    System.out.println("=== PROCESSING ON JAVAFX THREAD ===");

                    // Prevent multiple simultaneous payment processing
                    if ("SUCCESS".equals(status.toUpperCase()) && paymentProcessing) {
                        System.out.println("Payment already being processed, ignoring duplicate callback");
                        return;
                    }

                    if (details instanceof JSObject) {
                        System.out.println("Processing JSObject details");
                        handlePaymentCallback(status, (JSObject) details);
                    } else {
                        System.out.println("Processing simple details: " + details);
                        handleSimpleCallback(status, details);
                    }
                } catch (Exception e) {
                    System.err.println("=== EXCEPTION IN CALLBACK ===");
                    System.err.println("Error in payment callback: " + e.getMessage());
                    e.printStackTrace();
                    paymentStatusLabel.setText("Error processing payment callback");
                }
            });
        }
    }

    private void handlePaymentCallback(String status, JSObject details) {
        System.out.println("aaa");
        switch (status.toUpperCase()) {
            case "SUCCESS":
                handleSuccess(details);
                break;
            case "ERROR":
                handleError(details);
                break;
            case "CANCELLED":
                handleCancellation(details);
                break;
            case "TEST":
                System.out.println("Bridge test successful - callback working properly");
                break;
            default:
                System.out.println("Unknown payment status: " + status);
                paymentStatusLabel.setText("Unknown payment status: " + status);
        }
    }

    private void handleSimpleCallback(String status, Object details) {
        switch (status.toUpperCase()) {
            case "TEST":
                System.out.println("Bridge test successful!");
                break;
            case "CANCELLED":
                showAlert("Payment Cancelled", "Payment was cancelled by user.", Alert.AlertType.WARNING);
                break;
            case "ERROR":
                String errorMsg = details != null ? details.toString() : "Unknown error";
                showAlert("Payment Error", "Payment error: " + errorMsg, Alert.AlertType.ERROR);
                break;
            default:
                System.out.println("Simple callback - Status: " + status + ", Details: " + details);
        }
    }

    private void handleSuccess(JSObject details) {
        if (paymentProcessing) {
            System.out.println("Payment already being processed, ignoring duplicate success callback");
            return;
        }

        // Validate required fields before processing
        String transactionId = tryGetString(details, "transactionId");
        String amount = tryGetString(details, "amount");

        if (transactionId == null || transactionId.trim().isEmpty()) {
            System.err.println("Missing transaction ID in payment success callback");
            showAlert("Payment Error", "Payment completed but transaction ID is missing. Please contact support.", Alert.AlertType.WARNING);
            return;
        }

        paymentProcessing = true;
        System.out.println("=== PROCESSING SUCCESSFUL PAYMENT ===");

        String payerEmail = tryGetString(details, "payerEmail");
        String orderId = tryGetString(details, "orderId");
        String captureId = tryGetString(details, "captureId");

        System.out.println("Transaction ID: " + transactionId);
        System.out.println("Amount: " + amount);
        System.out.println("Payer Email: " + payerEmail);
        System.out.println("Order ID: " + orderId);
        System.out.println("Capture ID: " + captureId);

        paymentStatusLabel.setText("Payment successful! Processing...");

        try {
            // Update booking status
            currentBooking.setStatus(BookingStatus.CONFIRMED);

            // You might want to store payment details in the booking or a separate payment record
            // currentBooking.setTransactionId(transactionId);
            // currentBooking.setPaymentAmount(Double.parseDouble(amount));

            bookingService.update(currentBooking);
            System.out.println("Booking status updated to CONFIRMED");

            String successMessage = String.format(
                    "Payment completed successfully!\n\n" +
                            "Transaction ID: %s\n" +
                            "Amount: $%s\n" +
                            "Booking ID: %d\n" +
                            "Order ID: %s",
                    transactionId,
                    amount != null ? amount : "N/A",
                    currentBooking.getIdBooking(),
                    orderId != null ? orderId : "N/A"
            );

            showAlert("Payment Successful", successMessage, Alert.AlertType.INFORMATION);

            // Close window after a short delay
            CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS).execute(() -> {
                Platform.runLater(() -> {
                    cleanup();
                    closeWindow();
                });
            });

        } catch (Exception e) {
            System.err.println("Error updating booking after successful payment: " + e.getMessage());
            e.printStackTrace();
            showAlert("Warning",
                    "Payment was successful but there was an error updating the booking status.\n" +
                            "Transaction ID: " + transactionId + "\n" +
                            "Please contact support if needed.",
                    Alert.AlertType.WARNING);
        } finally {
            paymentProcessing = false;
        }
    }

    // 5. Add method to handle window closing properly
    @Override
    public void finalize() throws Throwable {
        cleanup();
        super.finalize();
    }

    private void handleError(JSObject details) {
        System.out.println("=== PROCESSING PAYMENT ERROR ===");
        String error = tryGetString(details, "error");
        String errorDetails = tryGetString(details, "errorDetails");

        System.out.println("Error: " + error);
        System.out.println("Error Details: " + errorDetails);

        paymentStatusLabel.setText("Payment failed: " + (error != null ? error : "Unknown error"));

        String errorMessage = error != null ? error : "Unknown payment error occurred";
        if (errorDetails != null && !errorDetails.equals(error)) {
            errorMessage += "\n\nDetails: " + errorDetails;
        }

        showAlert("Payment Failed", errorMessage, Alert.AlertType.ERROR);
    }

    private void handleCancellation(JSObject details) {
        System.out.println("=== PROCESSING PAYMENT CANCELLATION ===");
        String orderId = tryGetString(details, "orderId");
        String reason = tryGetString(details, "reason");

        System.out.println("Order ID: " + orderId);
        System.out.println("Reason: " + reason);

        paymentStatusLabel.setText("Payment cancelled");
        showAlert("Payment Cancelled", "Payment was cancelled by the user.", Alert.AlertType.WARNING);
    }

    private String tryGetString(JSObject obj, String key) {
        try {
            Object value = obj.getMember(key);
            if (value != null && !"undefined".equals(value.toString()) && !"null".equals(value.toString())) {
                return value.toString();
            }
        } catch (Exception e) {
            System.err.println("Error getting property '" + key + "': " + e.getMessage());
        }
        return null;
    }

    private void closeWindow() {
        System.out.println("Closing payment window...");

        if (popupStage != null && popupStage.isShowing()) {
            popupStage.close();
        }

        if (currentStage != null) {
            currentStage.close();
        } else if (backButton != null && backButton.getScene() != null) {
            Stage stage = (Stage) backButton.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @FXML
    private void handleBack() {
        if (!paymentProcessing) {
            cleanup();
            closeWindow();
        } else {
            showAlert("Please Wait", "Payment is being processed. Please wait...", Alert.AlertType.INFORMATION);
        }
    }

    private void cleanup() {
        // Clean up popup if it exists
        if (popupStage != null && popupStage.isShowing()) {
            popupStage.close();
            popupStage = null;
        }

        // Clean up web engine
        if (webEngine != null) {
            try {
                webEngine.load("about:blank");
            } catch (Exception e) {
                System.err.println("Error cleaning up web engine: " + e.getMessage());
            }
        }

        // Reset flags
        bridgeReady = false;
        paymentProcessing = false;
    }

    @FXML
    private void handleRefreshPayment() {
        if (!paymentProcessing) {
            paymentStatusLabel.setText("Refreshing...");
            bridgeReady = false;
            loadPaymentPage();
        } else {
            showAlert("Please Wait", "Payment is being processed. Cannot refresh now.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleCancelPayment() {
        if (paymentProcessing) {
            showAlert("Cannot Cancel", "Payment is being processed and cannot be cancelled now.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Payment");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Do you want to cancel the payment process?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                closeWindow();
            }
        });
    }

    public static void openPaymentWindow(AccommodationBooking booking) {
        try {
            FXMLLoader loader = new FXMLLoader(PaymentController.class.getResource("/fxml/AccommodationManagementFXML/PaymentView.fxml"));
            Parent root = loader.load();
            PaymentController controller = loader.getController();
            controller.setBooking(booking);

            Stage stage = new Stage();
            controller.setCurrentStage(stage);
            stage.setTitle("PayPal Payment - Booking #" + booking.getIdBooking());
            stage.setScene(new Scene(root, 800, 700));
            stage.setResizable(true);
            stage.show();

            System.out.println("Payment window opened for booking ID: " + booking.getIdBooking());
        } catch (Exception e) {
            System.err.println("Error opening payment window: " + e.getMessage());
            e.printStackTrace();

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to open payment window");
                alert.setContentText("Error: " + e.getMessage());
                alert.showAndWait();
            });
        }
    }
}