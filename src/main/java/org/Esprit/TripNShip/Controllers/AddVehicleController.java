package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.Esprit.TripNShip.Entities.RentalAgency;
import org.Esprit.TripNShip.Entities.Type;
import org.Esprit.TripNShip.Entities.Vehicle;
import org.Esprit.TripNShip.Services.VehicleService;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class AddVehicleController {

    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField licensePlateField;
    @FXML private TextField dailyPriceField;
    @FXML private CheckBox availabilityCheckBox;
    @FXML private ComboBox<Type> typeVehicleComboBox;
    @FXML private Button addVehicleButton;
    @FXML private Button chooseImageButton;
    @FXML private Label imageFileNameLabel;

    private final VehicleService vehicleService = new VehicleService();
    private RentalAgency rentalAgency;

    private File selectedImageFile;

    @FXML
    private void initialize() {
        typeVehicleComboBox.getItems().addAll(Type.values());

        // Ajout explicite des handlers
        addVehicleButton.setOnAction(event -> addVehicle());
        chooseImageButton.setOnAction(event -> handleChooseImage());
    }

    public void setRentalAgency(RentalAgency agency) {
        this.rentalAgency = agency;
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Vehicle Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            imageFileNameLabel.setText(file.getName());
        }
    }

    private void addVehicle() {
        String brand = brandField.getText().trim();
        String model = modelField.getText().trim();
        String licensePlate = licensePlateField.getText().trim();
        String dailyPriceText = dailyPriceField.getText().trim();
        boolean availability = availabilityCheckBox.isSelected();
        Type type = typeVehicleComboBox.getValue();

        if (brand.isEmpty() || model.isEmpty() || licensePlate.isEmpty()
                || dailyPriceText.isEmpty() || type == null || selectedImageFile == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields and select an image.");
            return;
        }

        try {
            float dailyPrice = Float.parseFloat(dailyPriceText);

            // Utilisation d'une agence fictive si non injectée
            if (rentalAgency == null) {
                rentalAgency = new RentalAgency();
                rentalAgency.setIdAgency(1); // Assure-toi que l'ID existe dans ta base
                rentalAgency.setNameAgency("Default Agency");
            }

            // Copier l’image dans un dossier local
            String uploadsDir = "src/main/resources/uploads/";
            Files.createDirectories(Paths.get(uploadsDir)); // Crée le dossier si nécessaire

            String fileName = System.currentTimeMillis() + "_" + selectedImageFile.getName();
            Path destination = Paths.get(uploadsDir, fileName);
            Files.copy(selectedImageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

            Vehicle vehicle = new Vehicle();
            vehicle.setBrand(brand);
            vehicle.setModel(model);
            vehicle.setLicensePlate(licensePlate);
            vehicle.setDailyPrice(dailyPrice);
            vehicle.setAvailability(availability);
            vehicle.setType(type);

            // Utiliser URI pour être compatible avec JavaFX Image
            vehicle.setImageURL(destination.toUri().toString());

            vehicle.setRentalAgency(rentalAgency);

            vehicleService.add(vehicle);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle added successfully!");
            clearFields();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Daily price must be a valid number.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "File Error", "Could not save image file: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add vehicle: " + e.getMessage());
        }
    }

    private void clearFields() {
        brandField.clear();
        modelField.clear();
        licensePlateField.clear();
        dailyPriceField.clear();
        availabilityCheckBox.setSelected(false);
        typeVehicleComboBox.getSelectionModel().clearSelection();
        selectedImageFile = null;
        imageFileNameLabel.setText("No file chosen");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
