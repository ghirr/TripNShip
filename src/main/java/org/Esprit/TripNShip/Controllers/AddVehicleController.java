package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.Esprit.TripNShip.Entities.Type;
import org.Esprit.TripNShip.Entities.Vehicle;
import org.Esprit.TripNShip.Services.VehicleService;

public class AddVehicleController {

    @FXML
    private TextField brandField;

    @FXML
    private TextField modelField;

    @FXML
    private TextField licensePlateField;

    @FXML
    private TextField dailyPriceField;

    @FXML
    private TextField availabilityField;

    @FXML
    private ComboBox<Type> typeVehicleComboBox;



    @FXML
    private Button addVehicleButton;

    private final VehicleService vehicleService = new VehicleService();


    @FXML
    private void initialize() {
        // Charger types de véhicules
        typeVehicleComboBox.getItems().addAll(Type.values());




        // Action bouton
        addVehicleButton.setOnAction(event -> addVehicle());
    }

    private void addVehicle() {
        String brand = brandField.getText().trim();
        String model = modelField.getText().trim();
        String licensePlate = licensePlateField.getText().trim();
        String dailyPriceText = dailyPriceField.getText().trim();
        String availabilityText = availabilityField.getText().trim();
        Type type = typeVehicleComboBox.getValue();


        if (brand.isEmpty() || model.isEmpty() || licensePlate.isEmpty()
                || dailyPriceText.isEmpty() || availabilityText.isEmpty()
                || type == null ) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        try {
            float dailyPrice = Float.parseFloat(dailyPriceText);
            boolean availability = Boolean.parseBoolean(availabilityText);

            Vehicle vehicle = new Vehicle();
            vehicle.setBrand(brand);
            vehicle.setModel(model);
            vehicle.setLicensePlate(licensePlate);
            vehicle.setDailyPrice(dailyPrice);
            vehicle.setAvailability(availability);
            vehicle.setType(type);


            vehicleService.add(vehicle);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle added successfully!");
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid number/boolean values.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add vehicle: " + e.getMessage());
        }
    }

    private void clearFields() {
        brandField.clear();
        modelField.clear();
        licensePlateField.clear();
        dailyPriceField.clear();
        availabilityField.clear();
        typeVehicleComboBox.getSelectionModel().clearSelection();

    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
