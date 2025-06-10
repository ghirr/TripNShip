package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Services.UserService;
import org.Esprit.TripNShip.Services.VehicleRentalService;
import org.Esprit.TripNShip.Services.VehicleService;

import java.util.List;

public class UpdateVehicleRentalController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField totalPriceField;
    @FXML private ComboBox<StautCircuit> statusComboBox;
    @FXML private ComboBox<Vehicle> vehicleComboBox;
    @FXML private ComboBox<User> userComboBox;
    @FXML private Button updateButton;
    @FXML private ImageView closeIcon;

    private final VehicleRentalService vehicleRentalService = new VehicleRentalService();
    private final VehicleService vehicleService = new VehicleService();
    private final UserService userService = new UserService();

    private VehicleRental currentRental;

    public void setVehicleRental(VehicleRental rental) {
        this.currentRental = rental;
        populateFields();
    }

    private void populateFields() {
        List<Vehicle> vehicles = vehicleService.getAll();
        vehicleComboBox.getItems().setAll(vehicles);

        List<User> users = userService.getAll();
        userComboBox.getItems().setAll(users);

        statusComboBox.getItems().setAll(StautCircuit.values());

        // Affichage personnalisé basé sur toString() des entités (optionnel car toString est défini)
        // Ici on le fait explicitement pour le cas où tu veux contrôler précisément l'affichage :

        userComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty || user == null ? null : user.getLastName() + " " + user.getFirstName());
            }
        });
        userComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty || user == null ? null : user.getLastName() + " " + user.getFirstName());
            }
        });

        vehicleComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Vehicle vehicle, boolean empty) {
                super.updateItem(vehicle, empty);
                setText(empty || vehicle == null ? null : vehicle.getBrand());
            }
        });
        vehicleComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Vehicle vehicle, boolean empty) {
                super.updateItem(vehicle, empty);
                setText(empty || vehicle == null ? null : vehicle.getBrand());
            }
        });

        if (currentRental != null) {
            startDatePicker.setValue(currentRental.getStartDate().toLocalDate());
            endDatePicker.setValue(currentRental.getEndDate().toLocalDate());
            totalPriceField.setText(String.valueOf(currentRental.getTotalPrice()));
            statusComboBox.setValue(currentRental.getStatusCircuit());
            vehicleComboBox.setValue(currentRental.getVehicle());
            userComboBox.setValue(currentRental.getUser());
        }
    }

    @FXML
    public void initialize() {
        updateButton.setOnAction(event -> {
            if (validateInput()) {
                currentRental.setStartDate(startDatePicker.getValue().atStartOfDay());
                currentRental.setEndDate(endDatePicker.getValue().atStartOfDay());
                currentRental.setTotalPrice(Float.parseFloat(totalPriceField.getText()));
                currentRental.setStatusCircuit(statusComboBox.getValue());
                currentRental.setVehicle(vehicleComboBox.getValue());
                currentRental.setUser(userComboBox.getValue());

                vehicleRentalService.update(currentRental);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("La location a été mise à jour avec succès !");
                alert.showAndWait();

                Stage stage = (Stage) updateButton.getScene().getWindow();
                stage.close();
            }
        });
    }

    @FXML
    private void handleCloseForm() {
        Stage stage = (Stage) closeIcon.getScene().getWindow();
        stage.close();
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (startDatePicker.getValue() == null) errorMessage.append("Date de début requise.\n");
        if (endDatePicker.getValue() == null) errorMessage.append("Date de fin requise.\n");

        if (totalPriceField.getText().isEmpty()) {
            errorMessage.append("Prix total requis.\n");
        } else {
            try {
                Float.parseFloat(totalPriceField.getText());
            } catch (NumberFormatException e) {
                errorMessage.append("Prix total invalide.\n");
            }
        }

        if (statusComboBox.getValue() == null) errorMessage.append("Statut requis.\n");
        if (vehicleComboBox.getValue() == null) errorMessage.append("Véhicule requis.\n");
        if (userComboBox.getValue() == null) errorMessage.append("Utilisateur requis.\n");

        if (!errorMessage.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de validation");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage.toString());
            alert.showAndWait();
            return false;
        }

        return true;
    }
}
