package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    @FXML private TextField imageURLField;
    @FXML private Button updateVehicleButton;

    private final VehicleService vehicleService = new VehicleService();
    private Vehicle vehicleToUpdate;

    @FXML
    public void initialize() {
        // Configuration du bouton de mise à jour
        updateVehicleButton.setOnAction(event -> handleUpdateVehicle());
    }

    // Setter pour injecter le véhicule à modifier depuis un autre contrôleur
    public void setVehicleData(Vehicle vehicle) {
        this.vehicleToUpdate = vehicle;
        if (vehicle != null) {
            brandField.setText(vehicle.getBrand());
            modelField.setText(vehicle.getModel());
            licensePlateField.setText(vehicle.getLicensePlate());
            dailyPriceField.setText(String.valueOf(vehicle.getDailyPrice()));
            availabilityField.setText(String.valueOf(vehicle.isAvailability()));
            typeField.setText(vehicle.getType().name());
            imageURLField.setText(vehicle.getImageURL());
        }
    }

    private void handleUpdateVehicle() {
        try {
            // Récupération des données du formulaire
            String brand = brandField.getText();
            String model = modelField.getText();
            String licensePlate = licensePlateField.getText();
            float dailyPrice = Float.parseFloat(dailyPriceField.getText());
            boolean availability = Boolean.parseBoolean(availabilityField.getText());
            Type type = Type.valueOf(typeField.getText().toUpperCase());
            String imageURL = imageURLField.getText();

            // Réutilisation de l'agence existante
            RentalAgency agency = vehicleToUpdate.getRentalAgency();

            // Création du véhicule mis à jour
            Vehicle updatedVehicle = new Vehicle(
                    vehicleToUpdate.getIdVehicle(),
                    brand,
                    model,
                    licensePlate,
                    dailyPrice,
                    availability,
                    type,
                    agency,
                    imageURL            );

            // Mise à jour via le service
            vehicleService.update(updatedVehicle);

            // Confirmation
            showConfirmation("Vehicle updated successfully!");
            closeWindow();

        } catch (Exception e) {
            showError("Error updating vehicle: " + e.getMessage());
        }
    }

    private void showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid input");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        updateVehicleButton.getScene().getWindow().hide();
    }
}
