package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.Esprit.TripNShip.Entities.CircuitBooking;
import org.Esprit.TripNShip.Entities.StatusBooking;
import org.Esprit.TripNShip.Entities.TourCircuit;
import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Services.CircuitBookingService;
import org.Esprit.TripNShip.Services.TourCircuitService;
import org.Esprit.TripNShip.Services.UserService;

import java.time.LocalDate;

public class UpdateCircuitBookingController {

    @FXML private DatePicker bookingDatePicker;
    @FXML private ComboBox<StatusBooking> statusComboBox;
    @FXML private ComboBox<User> userComboBox;
    @FXML private ComboBox<TourCircuit> tourCircuitComboBox;
    @FXML private Button updateBookingButton;
    @FXML private ImageView closeIcon;

    private final CircuitBookingService bookingService = new CircuitBookingService();
    private final UserService userService = new UserService();
    private final TourCircuitService tourCircuitService = new TourCircuitService();

    private CircuitBooking currentBooking;

    @FXML
    private void initialize() {
        // Remplir les ComboBox avec les données
        statusComboBox.setItems(FXCollections.observableArrayList(StatusBooking.values()));
        userComboBox.setItems(FXCollections.observableArrayList(userService.getAll()));
        tourCircuitComboBox.setItems(FXCollections.observableArrayList(tourCircuitService.getAll()));

        // Custom affichage du User : prénom + nom
        userComboBox.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user != null ? user.getFirstName() + " " + user.getLastName() : "";
            }

            @Override
            public User fromString(String string) {
                return null; // Non utilisé
            }
        });

        // Custom affichage du TourCircuit : nom du circuit
        tourCircuitComboBox.setConverter(new StringConverter<TourCircuit>() {
            @Override
            public String toString(TourCircuit circuit) {
                return circuit != null ? circuit.getNameCircuit(): "";
            }

            @Override
            public TourCircuit fromString(String string) {
                return null; // Non utilisé
            }
        });

        // Action du bouton update
        updateBookingButton.setOnAction(e -> updateBooking());
    }

    public void setBookingData(CircuitBooking booking) {
        this.currentBooking = booking;

        // Pré-remplir les champs
        bookingDatePicker.setValue(booking.getBookingDate().toLocalDate());
        statusComboBox.setValue(booking.getStatusBooking());
        userComboBox.setValue(booking.getUser());
        tourCircuitComboBox.setValue(booking.getTourCircuit());
    }

    private void updateBooking() {
        if (currentBooking == null) return;

        // Récupérer les nouvelles valeurs
        LocalDate selectedDate = bookingDatePicker.getValue();
        StatusBooking selectedStatus = statusComboBox.getValue();
        User selectedUser = userComboBox.getValue();
        TourCircuit selectedCircuit = tourCircuitComboBox.getValue();

        // Vérification de la validité
        if (selectedDate == null || selectedStatus == null || selectedUser == null || selectedCircuit == null) {
            showAlert("Validation Error", "Please fill all the fields.");
            return;
        }

        // Mise à jour des données
        currentBooking.setBookingDate(selectedDate.atStartOfDay());
        currentBooking.setStatusBooking(selectedStatus);
        currentBooking.setUser(selectedUser);
        currentBooking.setTourCircuit(selectedCircuit);

        // Appel service
        bookingService.update(currentBooking);

        // Fermer la fenêtre après update
        Stage stage = (Stage) updateBookingButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCloseForm() {
        Stage stage = (Stage) closeIcon.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
