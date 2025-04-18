package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.Esprit.TripNShip.Entities.RentalAgency;
import org.Esprit.TripNShip.Services.RentalAgencyService;

public class AddRentalAgencyController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField contactField;

    @FXML
    private TextField ratingField;

    @FXML
    private Button addAgencyButton;

    private final RentalAgencyService rentalAgencyService = new RentalAgencyService();

    @FXML
    private void initialize() {
        addAgencyButton.setOnAction(event -> addRentalAgency());
    }

    private void addRentalAgency() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String contact = contactField.getText().trim();
        String ratingText = ratingField.getText().trim();

        if (name.isEmpty() || address.isEmpty() || contact.isEmpty() || ratingText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        try {

            float rating = Float.parseFloat(ratingText);
            RentalAgency agency = new RentalAgency();
            agency.setNameAgency(name);
            agency.setAddressAgency(address);
            agency.setContactAgency(contact);
            agency.setRating(rating);

            rentalAgencyService.add(agency);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Rental Agency added successfully!");

            clearFields();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Rating must be a valid number.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add Rental Agency: " + e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        addressField.clear();
        contactField.clear();
        ratingField.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
