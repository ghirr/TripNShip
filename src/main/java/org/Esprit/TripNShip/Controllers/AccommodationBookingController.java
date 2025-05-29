package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.AccommodationBooking;
import org.Esprit.TripNShip.Entities.BookingStatus;
import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Services.AccommodationBookingService;
import org.Esprit.TripNShip.Utils.UserSession;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class AccommodationBookingController {

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField priceField;

    @FXML
    private Button addAccommodationButton;
    @FXML
    private TextField roomNameField;


    private Room selectedRoom;

    private final AccommodationBookingService bookingService = new AccommodationBookingService();

    public void setRoom(Room room) {
        this.selectedRoom = room;
        System.out.println(">> setRoom called with: " + (room != null ? room.getNameRoom() : "null"));
        if (room != null) {
            roomNameField.setText(room.getNameRoom());
            calculateAndDisplayTotalPrice();
        }
    }



    @FXML
    private void initialize() {
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> calculateAndDisplayTotalPrice());
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> calculateAndDisplayTotalPrice());
        addAccommodationButton.setOnAction(event -> saveBooking());
        priceField.setDisable(true);
    }

    private void calculateAndDisplayTotalPrice() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate != null && endDate != null && selectedRoom != null) {
            if (!endDate.isBefore(startDate)) {
                long nights = ChronoUnit.DAYS.between(startDate, endDate);
                if (nights == 0) nights = 1;

                double pricePerNight = selectedRoom.getPrice();
                double totalPrice = nights * pricePerNight;

                priceField.setText(String.format("%.2f", totalPrice));
            } else {
                priceField.setText("0.00");
            }
        } else {
            priceField.setText("0.00");
        }
    }

    private void saveBooking() {
        try {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (startDate == null || endDate == null) {
                showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs.");
                return;
            }

            if (selectedRoom == null) {
                showAlert(Alert.AlertType.WARNING, "Aucune chambre sélectionnée.");
                return;
            }

            if (endDate.isBefore(startDate)) {
                showAlert(Alert.AlertType.WARNING, "La date de fin doit être après la date de début.");
                return;
            }


            UserSession currentUser = UserSession.getInstance();
            User user = new User(currentUser.getUserId(), currentUser.getUserFirstName(), currentUser.getUserLastName(), currentUser.getUserRole(), currentUser.getUserEmail());


            long nights = ChronoUnit.DAYS.between(startDate, endDate);
            if (nights == 0) nights = 1;
            double pricePerNight = selectedRoom.getPrice();
            double totalPrice = nights * pricePerNight;
            priceField.setText(String.format("%.2f", totalPrice));

            AccommodationBooking booking = new AccommodationBooking(
                    0,
                    user,
                    selectedRoom,
                    java.sql.Date.valueOf(startDate),
                    java.sql.Date.valueOf(endDate),
                    BookingStatus.PENDING
            );

            bookingService.add(booking);
            showAlert(Alert.AlertType.INFORMATION, "Réservation ajoutée avec succès !");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) addAccommodationButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
