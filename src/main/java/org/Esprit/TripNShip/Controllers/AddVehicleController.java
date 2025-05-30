package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.Esprit.TripNShip.Entities.RentalAgency;
import org.Esprit.TripNShip.Entities.Type;
import org.Esprit.TripNShip.Entities.Vehicle;
import org.Esprit.TripNShip.Services.RentalAgencyService;
import org.Esprit.TripNShip.Services.VehicleService;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

import static org.Esprit.TripNShip.Utils.Shared.UPLOAD_DIR;

public class AddVehicleController {

    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField licensePlateField;
    @FXML private TextField dailyPriceField;
    @FXML private CheckBox availabilityCheckBox;
    @FXML private ComboBox<Type> typeVehicleComboBox;
    @FXML private ComboBox<RentalAgency> rentalAgencyComboBox;
    @FXML private Button addVehicleButton;
    @FXML private Button chooseImageButton;
    @FXML private Label imageFileNameLabel;
    @FXML
    private ImageView closeIcon;

    private final VehicleService vehicleService = new VehicleService();
    private final RentalAgencyService rentalAgencyService = new RentalAgencyService();

    private File selectedImageFile;

    @FXML
    private void initialize() {
        typeVehicleComboBox.getItems().addAll(Type.values());
        loadRentalAgencies();

//        // Afficher seulement nameAgency dans la ComboBox
//        rentalAgencyComboBox.setCellFactory();

        addVehicleButton.setOnAction(event -> addVehicle());
        chooseImageButton.setOnAction(event -> handleChooseImage());


    }

    private void loadRentalAgencies() {
        List<RentalAgency> agencies = rentalAgencyService.getAll();
        rentalAgencyComboBox.getItems().addAll(agencies);
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
        RentalAgency selectedAgency = rentalAgencyComboBox.getValue();
        if (brand.isEmpty() || model.isEmpty() || licensePlate.isEmpty()
                || dailyPriceText.isEmpty() || type == null || selectedImageFile == null || selectedAgency == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields and select an image and agency.");
            return;
        }

        try {
            float dailyPrice = Float.parseFloat(dailyPriceText);

            // Sauvegarder l'image
            String dirName = "vehicles";
            Path uploadsDir = Paths.get(UPLOAD_DIR, dirName);
            Files.createDirectories(uploadsDir);

            String fileExtension = getFileExtension(selectedImageFile.getName());
            String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID() + fileExtension;

            Path destination = uploadsDir.resolve(fileName);
            Files.copy(selectedImageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

            // Créer l'objet Vehicle
            Vehicle vehicle = new Vehicle();
            vehicle.setBrand(brand);
            vehicle.setModel(model);
            vehicle.setLicensePlate(licensePlate);
            vehicle.setDailyPrice(dailyPrice);
            vehicle.setAvailability(availability);
            vehicle.setType(type);
            vehicle.setImageURL(destination.toUri().toString());
            vehicle.setRentalAgency(selectedAgency);

            vehicleService.add(vehicle);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle added successfully!");
            clearFields();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Daily price must be a valid number.");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "File Error", "Could not save image file:\n" + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add vehicle:\n" + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return (lastDot != -1 && lastDot < fileName.length() - 1) ? fileName.substring(lastDot) : "";
    }

    private void clearFields() {
        brandField.clear();
        modelField.clear();
        licensePlateField.clear();
        dailyPriceField.clear();
        availabilityCheckBox.setSelected(false);
        typeVehicleComboBox.getSelectionModel().clearSelection();
        rentalAgencyComboBox.getSelectionModel().clearSelection();
        selectedImageFile = null;
        imageFileNameLabel.setText("No file chosen");
    }

    @FXML
    private void handleCloseForm() {
        Stage stage = (Stage) closeIcon.getScene().getWindow();
        stage.close();
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
