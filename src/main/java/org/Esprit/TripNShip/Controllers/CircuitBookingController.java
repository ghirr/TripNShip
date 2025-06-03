package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import org.Esprit.TripNShip.Entities.CircuitBooking;
import org.Esprit.TripNShip.Entities.StatusBooking;
import org.Esprit.TripNShip.Entities.TourCircuit;
import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Services.CircuitBookingService;
import org.Esprit.TripNShip.Utils.UserSession;

import java.time.LocalDate;

public class CircuitBookingController {

    @FXML
    private Label circuitNameLabel;

    @FXML
    private DatePicker bookingDatePicker;

    @FXML
    private Button submitBookingButton;

    private TourCircuit selectedCircuit;

    UserSession currentUser = UserSession.getInstance();

    public void initialize() {
        submitBookingButton.setOnAction(e -> {
            if (selectedCircuit == null) {
                showAlert("No circuit selected. Please try again.");
                return;
            }

            LocalDate date = bookingDatePicker.getValue();
            if (date == null) {
                showAlert("Please select a booking date.");
                return;
            }

            // Création d'une réservation
            CircuitBooking booking = new CircuitBooking();

            // Convertir LocalDate en LocalDateTime
            booking.setBookingDate(date.atStartOfDay());

            // Statut à null (sera géré dans le service)
            booking.setStatusBooking(null);

            User user = new User();
            user.setIdUser(currentUser.getUserId());
            booking.setUser(user);

            // Circuit sélectionné
            booking.setTourCircuit(selectedCircuit);

            // Enregistrer avec service
            CircuitBookingService service = new CircuitBookingService();
            service.add(booking);

            System.out.println("Booked " + selectedCircuit.getNameCircuit() + " on " + date);

            showAlert("Circuit '" + selectedCircuit.getNameCircuit() + "' booked for " + date + "!");
        });
    }

    public void setCircuit(TourCircuit circuit) {
        this.selectedCircuit = circuit;
        if (circuit != null) {
            circuitNameLabel.setText("Circuit: " + circuit.getNameCircuit());
        } else {
            circuitNameLabel.setText("No circuit selected.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Booking");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
