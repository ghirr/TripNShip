package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.TypeAccommodation;
import org.Esprit.TripNShip.Services.AccommodationService;

public class EditAccommodationController {

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private ChoiceBox<String> typeChoiceBox;
    @FXML private TextField priceField;
    @FXML private TextField capacityField;
    @FXML private Button editAccommodationButton;

    private Accommodation accommodation;
    private final AccommodationService accommodationService = new AccommodationService();

    private Runnable onAccommodationUpdated;
    private Stage stage;

    @FXML
    private void initialize() {
        typeChoiceBox.setItems(FXCollections.observableArrayList("HOTEL", "AIRBNB", "GUESTHOUSE"));
    }

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
        if (accommodation != null) {
            nameField.setText(accommodation.getName());
            addressField.setText(accommodation.getAddress());
            typeChoiceBox.setValue(accommodation.getType().name());
            priceField.setText(String.valueOf(accommodation.getPriceNight()));
            capacityField.setText(String.valueOf(accommodation.getCapacity()));
        }
    }

    public void setOnAccommodationUpdated(Runnable callback) {
        this.onAccommodationUpdated = callback;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void setAccommodationToEdit(ActionEvent actionEvent) {
        if (accommodation == null) {
            showAlert(Alert.AlertType.ERROR, "No accommodation selected for editing.");
            return;
        }

        // Lecture des valeurs
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String typeValue = typeChoiceBox.getValue();
        String priceText = priceField.getText().trim();
        String capacityText = capacityField.getText().trim();

        // Contrôle de validité des champs
        if (name.isEmpty() || !name.matches("[a-zA-Z0-9\\s]+")) {
            showAlert(Alert.AlertType.ERROR, "Please enter a valid name (letters and numbers only).");
            return;
        }

        if (address.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Address cannot be empty.");
            return;
        }

        if (typeValue == null || typeValue.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please select a type of accommodation.");
            return;
        }

        float price;
        try {
            price = Float.parseFloat(priceText);
            if (price <= 0) {
                showAlert(Alert.AlertType.ERROR, "Price must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Please enter a valid price.");
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityText);
            if (capacity <= 0) {
                showAlert(Alert.AlertType.ERROR, "Capacity must be a positive integer.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Please enter a valid capacity.");
            return;
        }

        TypeAccommodation typeEnum;
        try {
            typeEnum = TypeAccommodation.valueOf(typeValue);
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid accommodation type selected.");
            return;
        }

        // Mise à jour
        try {
            accommodation.setName(name);
            accommodation.setAddress(address);
            accommodation.setType(typeEnum);
            accommodation.setPriceNight(price);
            accommodation.setCapacity(capacity);

            accommodationService.update(accommodation);
            handleUpdate();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error while updating accommodation.");
            e.printStackTrace();
        }
    }

    private void handleUpdate() {
        if (onAccommodationUpdated != null) {
            onAccommodationUpdated.run();
        }

        showAlert(Alert.AlertType.INFORMATION, "Accommodation updated successfully.");

        if (stage != null) {
            stage.close();
        } else {
            closeWindow();
        }
    }

    private void closeWindow() {
        ((Stage) priceField.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Accommodation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
