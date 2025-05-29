package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.Esprit.TripNShip.Entities.CircuitBooking;
import org.Esprit.TripNShip.Entities.StatusBooking;
import org.Esprit.TripNShip.Entities.TourCircuit;
import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Services.CircuitBookingService;
import org.Esprit.TripNShip.Services.TourCircuitService;
import org.Esprit.TripNShip.Services.UserService;

import java.time.LocalDate;

public class AddCircuitBookingController {

    @FXML
    private DatePicker bookingDatePicker;

    @FXML
    private ComboBox<StatusBooking> statusComboBox;

    @FXML
    private ComboBox<User> userComboBox;

    @FXML
    private ComboBox<TourCircuit> tourCircuitComboBox;

    @FXML
    private Button addBookingButton;

    private final CircuitBookingService circuitBookingService = new CircuitBookingService();
    private final UserService userService = new UserService();
    private final TourCircuitService tourCircuitService = new TourCircuitService();

    @FXML
    private void initialize() {
        // Charger les statuts
        statusComboBox.getItems().addAll(StatusBooking.values());

        // Charger les utilisateurs
        userComboBox.getItems().addAll(userService.getAll());
        userComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty || user == null ? null : user.getFirstName() + " " + user.getLastName());
            }
        });
        userComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty || user == null ? null : user.getFirstName() + " " + user.getLastName());
            }
        });

        // Charger les circuits
        tourCircuitComboBox.getItems().addAll(tourCircuitService.getAll());
        tourCircuitComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(TourCircuit circuit, boolean empty) {
                super.updateItem(circuit, empty);
                setText(empty || circuit == null ? null : circuit.getNameCircuit());
            }
        });
        tourCircuitComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(TourCircuit circuit, boolean empty) {
                super.updateItem(circuit, empty);
                setText(empty || circuit == null ? null : circuit.getNameCircuit());
            }
        });

        addBookingButton.setOnAction(event -> addCircuitBooking());
    }

    private void addCircuitBooking() {
        LocalDate selectedDate = bookingDatePicker.getValue();
        StatusBooking status = statusComboBox.getValue();
        User selectedUser = userComboBox.getValue();
        TourCircuit selectedCircuit = tourCircuitComboBox.getValue();

        if (selectedDate == null || status == null || selectedUser == null || selectedCircuit == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        try {
            CircuitBooking booking = new CircuitBooking();
            booking.setBookingDate(selectedDate.atStartOfDay());
            booking.setStatusBooking(status);
            booking.setUser(selectedUser);
            booking.setTourCircuit(selectedCircuit);

            circuitBookingService.add(booking);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Circuit booking added successfully!");

            clearFields();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
        }
    }

    private void clearFields() {
        bookingDatePicker.setValue(null);
        statusComboBox.setValue(null);
        userComboBox.setValue(null);
        tourCircuitComboBox.setValue(null);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
