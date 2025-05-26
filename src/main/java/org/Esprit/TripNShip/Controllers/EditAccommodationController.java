package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.TypeAccommodation;
import org.Esprit.TripNShip.Services.AccommodationService;
import org.Esprit.TripNShip.Utils.Shared;

import java.io.File;

public class EditAccommodationController {

    @FXML private ImageView accommodationImageView;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private ChoiceBox<String> typeChoiceBox;
    @FXML private TextField capacityField;

    private Accommodation accommodation;
    private final AccommodationService accommodationService = new AccommodationService();
    private File selectedPhoto;

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;

        if (accommodation != null) {
            nameField.setText(accommodation.getName());
            addressField.setText(accommodation.getAddress());
            typeChoiceBox.setValue(accommodation.getType().name());
            capacityField.setText(String.valueOf(accommodation.getCapacity()));

            String photoPath = accommodation.getPhotosAccommodation();

            if (photoPath != null && !photoPath.isEmpty()) {
                try {
                    Image image;
                    if (photoPath.startsWith("http://") || photoPath.startsWith("https://")) {
                        image = new Image(photoPath, true);
                    } else {
                        File file = new File(photoPath);
                        if (!file.exists()) {
                            System.out.println("Photo file not found locally: " + file.getAbsolutePath());
                            return;
                        }
                        image = new Image(file.toURI().toString());
                    }
                    accommodationImageView.setImage(image);
                    System.out.println("Photo loaded successfully.");
                } catch (Exception e) {
                    System.out.println("Failed to load image: " + e.getMessage());
                }
            } else {
                System.out.println("No photo path provided.");
            }
        }
    }


    @FXML
    private void initialize() {
        typeChoiceBox.setItems(FXCollections.observableArrayList("HOTEL", "AIRBNB", "GUESTHOUSE"));
    }

    @FXML
    private void updateAccommodation() {
        if (accommodation == null) {
            showError("No accommodation selected for editing.");
            return;
        }

        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String typeValue = typeChoiceBox.getValue();
        String capacityText = capacityField.getText().trim();

        if (name.isEmpty() || !name.matches("[a-zA-Z0-9\\s]+")) {
            showError("Please enter a valid name (letters and numbers only).");
            return;
        }

        if (address.isEmpty()) {
            showError("Address cannot be empty.");
            return;
        }

        if (typeValue == null || typeValue.isEmpty()) {
            showError("Please select a type of accommodation.");
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityText);
            if (capacity <= 0) {
                showError("Capacity must be a positive integer.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid capacity.");
            return;
        }

        TypeAccommodation typeEnum;
        try {
            typeEnum = TypeAccommodation.valueOf(typeValue);
        } catch (IllegalArgumentException e) {
            showError("Invalid accommodation type selected.");
            return;
        }

        try {
            accommodation.setName(name);
            accommodation.setAddress(address);
            accommodation.setType(typeEnum);
            accommodation.setCapacity(capacity);

            // ✅ Mise à jour de la photo
            if (selectedPhoto != null) {
                String uploadedPath = Shared.uploadProfilePhoto(selectedPhoto, Shared.ACCOMMODATION_PATH);
                accommodation.setPhotosAccommodation(uploadedPath);
            }

            accommodationService.update(accommodation);

            showInfo("Accommodation updated successfully!");
            closeWindow();

        } catch (Exception e) {
            showError("Error while updating accommodation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void choosePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Accommodation Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) nameField.getScene().getWindow();
        selectedPhoto = fileChooser.showOpenDialog(stage);

        if (selectedPhoto != null) {
            try {
                Image image = new Image(selectedPhoto.toURI().toString());
                accommodationImageView.setImage(image);
            } catch (Exception e) {
                showError("Could not load the selected image.");
            }
        }
    }

    private void closeWindow() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
