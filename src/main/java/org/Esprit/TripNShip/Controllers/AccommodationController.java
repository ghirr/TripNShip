package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.TypeAccommodation;
import org.Esprit.TripNShip.Services.AccommodationService;

public class AccommodationController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private Button saveButton;

    @FXML
    private TableView<Accommodation> accommodationsTable;

    @FXML
    private ChoiceBox<String> typeChoiceBox;

    @FXML
    private TextField priceField;

    @FXML
    private TextField capacityField;

    private Accommodation accommodation;

    private final AccommodationService accommodationService = new AccommodationService();

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
        if (accommodation != null) {
            nameField.setText(accommodation.getName());
            addressField.setText(accommodation.getAddress());
            priceField.setText(String.valueOf(accommodation.getPriceNight()));
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
        ((Stage) priceField.getScene().getWindow()).close();
    }

    public void saveAccommodation(ActionEvent actionEvent) {
        // Récupération des champs
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String typeValue = typeChoiceBox.getValue();
        String priceText = priceField.getText().trim();
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

        float price;
        try {
            price = Float.parseFloat(priceText);
            if (price <= 0) {
                showError("Price must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid price.");
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

        accommodation.setName(name);
        accommodation.setAddress(address);
        accommodation.setType(typeEnum);
        accommodation.setPriceNight(price);
        accommodation.setCapacity(capacity);

        if (accommodation.getIdAccommodation() == 0) {
            accommodationService.add(accommodation);
            TableAccommodationController.getInstance().loadAccommodationsFromDatabase();
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
}
