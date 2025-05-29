package org.Esprit.TripNShip.Controllers;

import io.github.cdimascio.dotenv.Dotenv;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {

    @FXML private WebView paypalWebView;
    private Stage paypalstage;
    @FXML private Label bookingInfoLabel;
    @FXML private Label paymentStatusLabel;
    @FXML private Button backButton;
    @FXML private Button refreshButton;
    @FXML private Button cancelPaymentButton;

    private AccommodationBooking currentBooking;
    private AccommodationBookingService bookingService;
    private WebEngine webEngine;
    private Stage currentStage;

    // Payment callback handler
    public class PaymentCallback {
        public void paymentCallback(String status, JSObject details) {
            Platform.runLater(() -> handlePaymentCallback(status, details));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bookingService = new AccommodationBookingService();
        setupWebView();
    }

    public void setBooking(AccommodationBooking booking) {
        this.currentBooking = booking;
        updateBookingDisplay();
        loadPaymentPage();
    }

    public void setCurrentStage(Stage stage) {
        this.currentStage = stage;
    }

    private void setupWebView() {
        webEngine = paypalWebView.getEngine();

        // Enable JavaScript
        webEngine.setJavaScriptEnabled(true);

        // Add popup handler for PayPal authentication windows
        webEngine.setCreatePopupHandler(popupFeatures -> {
            // Create a new WebView for popup windows (PayPal authentication)
            WebView popupWebView = new WebView();
            WebEngine popupEngine = popupWebView.getEngine();
            popupEngine.setJavaScriptEnabled(true);

            // Create a new stage for the popup
            paypalstage = new Stage();
            paypalstage.setTitle("PayPal Authentication");
            paypalstage.setScene(new Scene(popupWebView, 800, 600));
            paypalstage.show();

            // Handle popup window close
//            popupStage.setOnCloseRequest(event -> {
//                // Refresh the main payment page when popup closes
//                Platform.runLater(() -> {
//                    try {
//                        // Small delay to allow PayPal to process
//                        Thread.sleep(1000);
//                        webEngine.reload();
//                    } catch (InterruptedException e) {
//                        Thread.currentThread().interrupt();
//                    }
//                });
//            });

            return popupEngine;
        });

        webEngine.setOnAlert(event -> System.out.println("ALERT: " + event.getData()));
        webEngine.setOnError(event -> System.err.println("WEB ERROR: " + event.getMessage()));
        webEngine.getLoadWorker().exceptionProperty().addListener((obs, oldEx, newEx) -> {
            if (newEx != null) newEx.printStackTrace();
        });

        // Handle page load completion
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                // Inject the JavaFX callback object into the web page
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javafx", new PaymentCallback());

                // Update booking details in the web page
                updateWebPageBookingDetails();
                paymentStatusLabel.setText("Payment page loaded successfully");
            } else if (newState == Worker.State.FAILED) {
                paymentStatusLabel.setText("Failed to load payment page");
                showAlert("Error", "Failed to load PayPal payment page. Please try again.", Alert.AlertType.ERROR);
            }
        });
    }

    private void loadPaymentPage() {
        try {
            Dotenv dotenv = Dotenv.load();
            // Load the HTML file from resources or file system
//            InputStream is = getClass().getResourceAsStream("/templates/paypal.html");
//            if (is == null) {
//                throw new IOException("Template file not found");
//            }
//            String htmlFile = new String(is.readAllBytes(), StandardCharsets.UTF_8);
//
//            String htmlContent = htmlFile.replace("YOUR_PAYPAL_CLIENT_ID",dotenv.get("PAYPAL_CLIENT_ID"));
//            webEngine.loadContent(htmlContent, "text/html");
            String htmlContent = "http://localhost/tripNship/paypal.html";
            webEngine.load(htmlContent);

            paymentStatusLabel.setText("Loading PayPal payment page...");
        } catch (Exception e) {
            paymentStatusLabel.setText("Error loading payment page");
            e.printStackTrace();
        }
    }

    private void updateBookingDisplay() {
        if (currentBooking != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

            bookingInfoLabel.setText("Booking #" + currentBooking.getIdBooking());

            // Calculate total amount (you may need to adjust this based on your pricing logic)
            double totalAmount = calculateTotalAmount();
        }
    }

    private void updateWebPageBookingDetails() {
        if (currentBooking != null && webEngine != null) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                double totalAmount = calculateTotalAmount();

                String script = String.format(
                        "updateBookingDetails({" +
                                "id: '%d', " +
                                "roomName: '%s', " +
                                "checkIn: '%s', " +
                                "checkOut: '%s', " +
                                "amount: '%.2f'" +
                                "});",
                        currentBooking.getIdBooking(),
                        currentBooking.getRoom().getNameRoom().replace("'", "\\'"),
                        dateFormat.format(currentBooking.getStartDate()),
                        dateFormat.format(currentBooking.getEndDate()),
                        totalAmount
                );

                webEngine.executeScript(script);
            } catch (Exception e) {
                System.err.println("Error updating web page booking details: " + e.getMessage());
            }
        }
    }

    private double calculateTotalAmount() {
        if (currentBooking != null && currentBooking.getRoom() != null) {
            // Calculate days between check-in and check-out
            long milliseconds = currentBooking.getEndDate().getTime() - currentBooking.getStartDate().getTime();
            long days = milliseconds / (1000 * 60 * 60 * 24);
            System.out.println(currentBooking.getRoom().getPrice());
            // Ensure at least 1 day is charged
            if (days <= 0) days = 1;

            // Calculate total: room price * number of days
            return currentBooking.getRoom().getPrice() * days;
        }
        return 0.0;
    }

    private void handlePaymentCallback(String status, JSObject details) {
        switch (status.toUpperCase()) {
            case "SUCCESS":
                handlePaymentSuccess(details);
                break;
            case "ERROR":
                handlePaymentError(details);
                break;
            case "CANCELLED":
                handlePaymentCancelled();
                break;
            default:
                paymentStatusLabel.setText("Unknown payment status: " + status);
        }
    }

    private void handlePaymentSuccess(JSObject details) {
        try {
            String transactionId = details.getMember("transactionId").toString();
            paymentStatusLabel.setText("Payment successful! Transaction ID: " + transactionId);

            // Update booking status to CONFIRMED
            currentBooking.setStatus(BookingStatus.CONFIRMED);
            bookingService.update(currentBooking);


            // Close the payment window and return to bookings
            Platform.runLater(() -> {
                try {
                    paypalstage.close();
                    returnToBookings();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            System.err.println("Error handling payment success: " + e.getMessage());
            paymentStatusLabel.setText("Payment completed but there was an error updating the booking.");
        }
    }

    private void handlePaymentError(JSObject details) {
        String errorMsg = "Payment failed. Please try again.";
        try {
            Object error = details.getMember("error");
            if (error != null) {
                errorMsg = "Payment failed: " + error.toString();
            }
        } catch (Exception e) {
            // Use default error message
        }

        paymentStatusLabel.setText(errorMsg);
        showAlert("Payment Failed", errorMsg, Alert.AlertType.ERROR);
    }

    private void handlePaymentCancelled() {
        paymentStatusLabel.setText("Payment was cancelled by user");
        showAlert("Payment Cancelled", "Payment was cancelled. Your booking status remains unchanged.", Alert.AlertType.WARNING);
    }

    @FXML
    private void handleBack() {
        try {
            returnToBookings();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to return to bookings page.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRefreshPayment() {
        paymentStatusLabel.setText("Refreshing payment page...");
        loadPaymentPage();
    }

    @FXML
    private void handleCancelPayment() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Cancel Payment");
        confirmAlert.setHeaderText("Cancel Payment Process");
        confirmAlert.setContentText("Are you sure you want to cancel the payment process?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    returnToBookings();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void returnToBookings() throws Exception {
        // Get the current stage and set the new scene
        Stage stage = currentStage != null ? currentStage : (Stage) backButton.getScene().getWindow();
        stage.close();
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

    // Static method to open payment window for a booking
    public static void openPaymentWindow(AccommodationBooking booking) {
        try {
            FXMLLoader loader = new FXMLLoader(PaymentController.class.getResource("/fxml/AccommodationManagementFXML/PaymentView.fxml"));
            Parent root = loader.load();

            PaymentController controller = loader.getController();
            controller.setBooking(booking);

            Stage stage = new Stage();
            controller.setCurrentStage(stage);
            stage.setTitle("Payment - TripNShip");
            stage.setScene(new Scene(root, 800, 700));
            stage.setResizable(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to open payment window");
            alert.setContentText("An error occurred while opening the payment window: " + e.getMessage());
            alert.showAndWait();
        }
    }
}