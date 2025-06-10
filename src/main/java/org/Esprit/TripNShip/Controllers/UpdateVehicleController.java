package org.Esprit.TripNShip.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.RentalAgency;
import org.Esprit.TripNShip.Entities.Type;
import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Entities.Vehicle;
import org.Esprit.TripNShip.Services.RentalAgencyService;
import org.Esprit.TripNShip.Services.VehicleService;

import java.io.File;
import java.util.List;

public class UpdateVehicleController {

    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField licensePlateField;
    @FXML private TextField dailyPriceField;

    @FXML private ComboBox<Type> typeVehicleComboBox;
    @FXML private ComboBox<RentalAgency> rentalAgencyComboBox;

    @FXML private Label imageFileNameLabel;
    @FXML private Button chooseImageButton;

    @FXML private CheckBox availabilityCheckBox;
    @FXML private Button updateVehicleButton;
    @FXML private ImageView closeIcon;

    private final VehicleService vehicleService = new VehicleService();
    private final RentalAgencyService rentalAgencyService = new RentalAgencyService();

    private Vehicle vehicleToUpdate;
    private String selectedImagePath;

    @FXML
    public void initialize() {
        typeVehicleComboBox.getItems().setAll(Type.values());

        List<RentalAgency> agencies = rentalAgencyService.getAll();
        rentalAgencyComboBox.getItems().setAll(agencies);
        rentalAgencyComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(RentalAgency user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty || user == null ? null : user.getNameAgency());
            }
        });
    }

    public void setVehicleData(Vehicle vehicle) {
        this.vehicleToUpdate = vehicle;
        if (vehicle != null) {
            brandField.setText(vehicle.getBrand());
            modelField.setText(vehicle.getModel());
            licensePlateField.setText(vehicle.getLicensePlate());
            dailyPriceField.setText(String.valueOf(vehicle.getDailyPrice()));
            availabilityCheckBox.setSelected(vehicle.isAvailability());
            typeVehicleComboBox.setValue(vehicle.getType());
            rentalAgencyComboBox.setValue(vehicle.getRentalAgency());
            selectedImagePath = vehicle.getImageURL();
            imageFileNameLabel.setText(selectedImagePath != null ? selectedImagePath : "No file chosen");
        }
    }

    @FXML
    private void handleUpdateVehicle(ActionEvent event) {
        try {
            String brand = brandField.getText();
            String model = modelField.getText();
            String licensePlate = licensePlateField.getText();
            float dailyPrice = Float.parseFloat(dailyPriceField.getText());
            boolean availability = availabilityCheckBox.isSelected();
            Type type = typeVehicleComboBox.getValue();
            RentalAgency agency = rentalAgencyComboBox.getValue();

            if (type == null || agency == null) {
                showError("Please select both a vehicle type and rental agency.");
                return;
            }

            Vehicle updatedVehicle = new Vehicle(
                    vehicleToUpdate.getIdVehicle(),
                    brand,
                    model,
                    licensePlate,
                    dailyPrice,
                    availability,
                    type,
                    agency,
                    selectedImagePath
            );

            vehicleService.update(updatedVehicle);
            showConfirmation("Vehicle updated successfully!");

        } catch (NumberFormatException e) {
            showError("Daily price must be a valid number.");
        } catch (Exception e) {
            showError("Error updating vehicle: " + e.getMessage());
        }
    }

    @FXML
    private void handleUploadImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Vehicle Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());
        if (file != null) {
            selectedImagePath = file.toURI().toString();
            imageFileNameLabel.setText(file.getName());
        }
    }

    @FXML
    private void handleCloseForm() {
        Stage stage = (Stage) closeIcon.getScene().getWindow();
        stage.close();
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
}
