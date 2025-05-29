package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.Esprit.TripNShip.Entities.AccommodationBooking;
import org.Esprit.TripNShip.Entities.BookingStatus;
import org.Esprit.TripNShip.Services.AccommodationBookingService;
import org.Esprit.TripNShip.Services.UserService;
import org.Esprit.TripNShip.Utils.UserSession;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class UserBookingsController implements Initializable {

    @FXML private VBox bookingsContainer;
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private Label statusLabel;
    @FXML private Button refreshButton;

    private AccommodationBookingService bookingService;
    private int currentUserId = UserSession.getInstance().getUserId();
    private List<AccommodationBooking> allUserBookings; // Store all bookings in memory

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bookingService = new AccommodationBookingService();
        setupStatusFilter();
        loadUserBookingsFromDatabase();
    }

    private void setupStatusFilter() {
        statusFilterComboBox.getItems().addAll(
                "All Bookings",
                "PENDING",
                "CONFIRMED",
                "CANCELLED"
        );
        statusFilterComboBox.setValue("All Bookings");
    }

    @FXML
    private void handleStatusFilter() {
        String selectedStatus = statusFilterComboBox.getValue();
        List<AccommodationBooking> filteredBookings;

        if ("All Bookings".equals(selectedStatus)) {
            filteredBookings = allUserBookings;
        } else {
            BookingStatus status = BookingStatus.valueOf(selectedStatus);
            filteredBookings = allUserBookings.stream()
                    .filter(booking -> booking.getStatus() == status)
                    .collect(java.util.stream.Collectors.toList());
        }

        displayBookings(filteredBookings);
        statusLabel.setText("Showing " + filteredBookings.size() + " booking(s)");
    }

    @FXML
    private void handleRefresh() {
        loadUserBookingsFromDatabase(); // Refresh from database
    }

    private void loadUserBookingsFromDatabase() {
        allUserBookings = bookingService.getBookingsByUserId(currentUserId);
        handleStatusFilter(); // Apply current filter to newly loaded data
        statusLabel.setText("Loaded " + allUserBookings.size() + " booking(s) from database");
    }

    private void displayBookings(List<AccommodationBooking> bookings) {
        bookingsContainer.getChildren().clear();

        if (bookings.isEmpty()) {
            Label noBookingsLabel = new Label("No bookings found");
            noBookingsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #7f8c8d;");
            VBox.setMargin(noBookingsLabel, new Insets(50, 0, 0, 0));
            bookingsContainer.getChildren().add(noBookingsLabel);
            return;
        }

        for (AccommodationBooking booking : bookings) {
            bookingsContainer.getChildren().add(createBookingCard(booking));
        }
    }

    private VBox createBookingCard(AccommodationBooking booking) {
        VBox card = new VBox(15);
        card.setStyle(getCardStyle(booking.getStatus()));
        card.setPadding(new Insets(20));
        card.setMaxWidth(Double.MAX_VALUE);

        // Header with booking ID and status
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(15);

        Label bookingIdLabel = new Label("Booking #" + booking.getIdBooking());
        bookingIdLabel.setFont(Font.font("System", FontWeight.BOLD, 18));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusBadge = createStatusBadge(booking.getStatus());

        header.getChildren().addAll(bookingIdLabel, spacer, statusBadge);

        // Room information
        VBox roomInfo = new VBox(8);
        Label roomLabel = new Label("Room: " + booking.getRoom().getNameRoom());
        roomLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        Label roomTypeLabel = new Label("Type: " + booking.getRoom().getType().toString());
        roomTypeLabel.setStyle("-fx-text-fill: #7f8c8d;");

        roomInfo.getChildren().addAll(roomLabel, roomTypeLabel);

        // Date information
        HBox dateInfo = new HBox(30);
        dateInfo.setAlignment(Pos.CENTER_LEFT);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

        VBox checkInBox = new VBox(5);
        Label checkInTitleLabel = new Label("Check-in");
        checkInTitleLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        checkInTitleLabel.setStyle("-fx-text-fill: #7f8c8d;");
        Label checkInDateLabel = new Label(dateFormat.format(booking.getStartDate()));
        checkInDateLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        checkInBox.getChildren().addAll(checkInTitleLabel, checkInDateLabel);

        VBox checkOutBox = new VBox(5);
        Label checkOutTitleLabel = new Label("Check-out");
        checkOutTitleLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        checkOutTitleLabel.setStyle("-fx-text-fill: #7f8c8d;");
        Label checkOutDateLabel = new Label(dateFormat.format(booking.getEndDate()));
        checkOutDateLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        checkOutBox.getChildren().addAll(checkOutTitleLabel, checkOutDateLabel);

        dateInfo.getChildren().addAll(checkInBox, checkOutBox);

        // Action buttons
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Button actionButton = createActionButton(booking);
        if (actionButton != null) {
            actionButtons.getChildren().add(actionButton);
        }

        card.getChildren().addAll(header, roomInfo, dateInfo, actionButtons);
        return card;
    }

    private String getCardStyle(BookingStatus status) {
        String baseStyle = "-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2); ";

        switch (status) {
            case PENDING:
                return baseStyle + "-fx-border-color: #f39c12; -fx-border-width: 0 0 0 4; -fx-border-radius: 10;";
            case CONFIRMED:
                return baseStyle + "-fx-border-color: #27ae60; -fx-border-width: 0 0 0 4; -fx-border-radius: 10;";
            case CANCELLED:
                return baseStyle + "-fx-border-color: #e74c3c; -fx-border-width: 0 0 0 4; -fx-border-radius: 10;";
            default:
                return baseStyle;
        }
    }

    private Label createStatusBadge(BookingStatus status) {
        Label badge = new Label(status.name());
        badge.setPadding(new Insets(5, 12, 5, 12));
        badge.setStyle(getStatusBadgeStyle(status));
        badge.setFont(Font.font("System", FontWeight.BOLD, 12));
        return badge;
    }

    private String getStatusBadgeStyle(BookingStatus status) {
        switch (status) {
            case PENDING:
                return "-fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 15;";
            case CONFIRMED:
                return "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 15;";
            case CANCELLED:
                return "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 15;";
            default:
                return "-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 15;";
        }
    }

    private Button createActionButton(AccommodationBooking booking) {
        Button button = new Button();
        button.setPrefWidth(120);
        button.setPrefHeight(35);

        switch (booking.getStatus()) {
            case PENDING:
                button.setText("Pay Now");
                button.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-background-radius: 5;");
                button.setOnAction(e -> handlePayNow(booking));
                break;
            case CONFIRMED:
                button.setText("Cancel");
                button.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-background-radius: 5;");
                button.setOnAction(e -> handleCancel(booking));
                break;
            case CANCELLED:
                button.setText("Delete");
                button.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-background-radius: 5;");
                button.setOnAction(e -> handleDelete(booking));
                break;
            default:
                return null;
        }

        return button;
    }

    private void handlePayNow(AccommodationBooking booking) {
        try{
        // Open the PayPal payment window
        PaymentController.openPaymentWindow(booking);

        // Optional: Show a status message
        statusLabel.setText("Opening PayPal payment window...");

    } catch (Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Payment Error");
        alert.setHeaderText("Failed to open payment window");
        alert.setContentText("An error occurred while opening the PayPal payment window. Please try again.");
        alert.showAndWait();
    }
    }

    private void handleCancel(AccommodationBooking booking) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Cancel Booking");
        confirmAlert.setHeaderText("Cancel This Booking");
        confirmAlert.setContentText("Are you sure you want to cancel this booking? This action cannot be undone.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                booking.setStatus(BookingStatus.CANCELLED);
                bookingService.update(booking);
                // Update the booking in memory as well
                updateBookingInMemory(booking);
                statusLabel.setText("Booking cancelled successfully!");
                handleStatusFilter(); // Refresh the current view
            }
        });
    }

    private void handleDelete(AccommodationBooking booking) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Booking");
        confirmAlert.setHeaderText("Delete This Booking");
        confirmAlert.setContentText("Are you sure you want to permanently delete this booking? This action cannot be undone.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                bookingService.delete(booking);
                // Remove the booking from memory as well
                allUserBookings.removeIf(b -> b.getIdBooking() == booking.getIdBooking());
                statusLabel.setText("Booking deleted successfully!");
                handleStatusFilter(); // Refresh the current view
            }
        });
    }

    // Helper method to update booking in memory after database operations
    private void updateBookingInMemory(AccommodationBooking updatedBooking) {
        for (int i = 0; i < allUserBookings.size(); i++) {
            if (allUserBookings.get(i).getIdBooking() == updatedBooking.getIdBooking()) {
                allUserBookings.set(i, updatedBooking);
                break;
            }
        }
    }
}