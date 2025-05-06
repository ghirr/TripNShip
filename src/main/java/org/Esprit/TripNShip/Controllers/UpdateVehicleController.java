package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import org.Esprit.TripNShip.Entities.RentalAgency;
import org.Esprit.TripNShip.Entities.Type;
import org.Esprit.TripNShip.Entities.Vehicle;
import org.Esprit.TripNShip.Services.VehicleService;

public class UpdateVehicleController {

    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField licensePlateField;
    @FXML private TextField dailyPriceField;
    @FXML private TextField availabilityField;
    @FXML private TextField typeField;
    @FXML private Button updateVehicleButton;

    private final VehicleService vehicleService = new VehicleService();
    private Vehicle vehicleToUpdate;

    @FXML
    public void initialize() {
        // Précharger les informations du véhicule
        if (vehicleToUpdate != null) {
            brandField.setText(vehicleToUpdate.getBrand());
            modelField.setText(vehicleToUpdate.getModel());
            licensePlateField.setText(vehicleToUpdate.getLicensePlate());
            dailyPriceField.setText(String.valueOf(vehicleToUpdate.getDailyPrice()));
            availabilityField.setText(String.valueOf(vehicleToUpdate.isAvailability()));
            typeField.setText(vehicleToUpdate.getType().toString());

        }

        // Configure le bouton de mise à jour
        updateVehicleButton.setOnAction(event -> handleUpdateVehicle());


    }

    // Setter pour passer l'objet Vehicle depuis le contrôleur parent
    public void setVehicleData(Vehicle vehicle) {
        this.vehicleToUpdate = vehicle;
    }

    private void handleUpdateVehicle() {
        // Récupérer les données du formulaire
        String brand = brandField.getText();
        String model = modelField.getText();
        String licensePlate = licensePlateField.getText();
        float dailyPrice = Float.parseFloat(dailyPriceField.getText());
        boolean availability = Boolean.parseBoolean(availabilityField.getText());
        Type type = Type.valueOf(typeField.getText().toUpperCase());


        // Créer un objet Vehicle avec les nouvelles données
        Vehicle updatedVehicle = new Vehicle(vehicleToUpdate.getIdVehicle(), brand, model, licensePlate, dailyPrice, availability, type, new RentalAgency(

        ));

        // Mettre à jour le véhicule
        vehicleService.update(updatedVehicle);

        // Afficher une confirmation
        showConfirmation("Vehicle updated successfully!");

        // Fermer la fenêtre de mise à jour
        closeWindow();
    }

    private void showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        // Fermer la fenêtre actuelle
        updateVehicleButton.getScene().getWindow().hide();
    }
}
