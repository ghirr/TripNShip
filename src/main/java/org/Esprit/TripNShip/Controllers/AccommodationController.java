package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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
import static org.Esprit.TripNShip.Utils.Shared.showAlert;


public class AccommodationController {
    public ImageView accommodationImageView;
    public Button buttonPhoto;

    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private ChoiceBox<String> typeChoiceBox;

    @FXML
    private TextField capacityField;

    private Accommodation accommodation;

    private final AccommodationService accommodationService = new AccommodationService();

    private File selectedPhoto ;

    public void setAccommodation(Accommodation accommodation) {

        this.accommodation = accommodation;
        if (accommodation != null) {
            nameField.setText(accommodation.getName());
            addressField.setText(accommodation.getAddress());
            capacityField.setText(String.valueOf(accommodation.getCapacity()));
            typeChoiceBox.setValue(accommodation.getType().name());
        }
    }

    @FXML
    private void initialize() {

        typeChoiceBox.setItems(FXCollections.observableArrayList("HOTEL", "AIRBNB", "GUESTHOUSE"));
        typeChoiceBox.setValue("HOTEL");
    }

    private void closeWindow() {
        ((Stage) capacityField.getScene().getWindow()).close();
    }

    public void saveAccommodation(ActionEvent actionEvent) {
        // Récupération des champs
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String typeValue = typeChoiceBox.getValue();
        String capacityText = capacityField.getText().trim();

        // Vérification des champs
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

        // Création ou mise à jour
        if (accommodation == null) {
            accommodation = new Accommodation();
        }
        String Photo = Shared.uploadProfilePhoto(selectedPhoto,Shared.ACCOMMODATION_PATH);

        accommodation.setName(name);
        accommodation.setAddress(address);
        accommodation.setType(typeEnum);
        accommodation.setCapacity(capacity);
        accommodation.setPhotosAccommodation(Photo);

        if (accommodation.getIdAccommodation() == 0) {
            accommodationService.add(accommodation);
            showSuccessMessage("Accommodation added successfully!");
        } else {
            accommodationService.update(accommodation);
            showSuccessMessage("Accommodation updated successfully!");
        }

        closeWindow();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Input Validation Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void selectPhoto(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) buttonPhoto.getScene().getWindow();
        selectedPhoto = fileChooser.showOpenDialog(stage);

        if (selectedPhoto != null) {
            System.out.println("Selected photo: " + selectedPhoto.getAbsolutePath());
            try {
                Image image = new Image(selectedPhoto.toURI().toString());
                accommodationImageView.setImage(image);
            } catch (Exception e) {
                System.err.println("Error loading selected image: " + e.getMessage());
                e.printStackTrace();
                Shared.showAlert(Alert.AlertType.ERROR, "Error", "Could not load the selected image.");
            }
        }
    }
}

