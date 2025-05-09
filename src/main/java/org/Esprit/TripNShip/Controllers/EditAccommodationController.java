package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.AccommodationBooking;
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

    private Runnable onAccommodationUpdated; // callback
    private Stage stage; // to close the window

    @FXML
    private void initialize() {
        // Initialize ChoiceBox with possible accommodation types
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

        try {
            // Validate inputs before updating
            if (nameField.getText().isEmpty() || addressField.getText().isEmpty() || priceField.getText().isEmpty() || capacityField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "All fields must be filled.");
                return;
            }

            // Parse and set the new values for the accommodation
            accommodation.setName(nameField.getText());
            accommodation.setAddress(addressField.getText());
            accommodation.setType(TypeAccommodation.valueOf(typeChoiceBox.getValue())); // Ensure value is valid
            accommodation.setPriceNight(Float.parseFloat(priceField.getText()));
            accommodation.setCapacity(Integer.parseInt(capacityField.getText()));

            // Update the accommodation in the service layer
            accommodationService.update(accommodation);

            handleUpdate(); // Handle post-update actions

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid number format for price or capacity.");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid accommodation type selected.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error while updating accommodation.");
            e.printStackTrace();
        }
    }

    private void handleUpdate() {
        // Trigger the callback to notify parent controller of the update
        if (onAccommodationUpdated != null) {
            onAccommodationUpdated.run();
        }

        // Show success message
        showAlert(Alert.AlertType.INFORMATION, "Accommodation updated successfully.");

        // Close the window
        if (stage != null) {
            stage.close();
        } else {
            closeWindow(); // fallback if stage not injected
        }
    }

    private void closeWindow() {
        // Close the window if stage was not injected
        ((Stage) priceField.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        // Show alert with provided message
        Alert alert = new Alert(alertType);
        alert.setTitle("Accommodation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //public void setBookingToEdit(AccommodationBooking booking) {
        // This method can be used if you need to edit booking info, but currently not implemented}
}
