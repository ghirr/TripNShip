package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.RentalAgency;
import org.Esprit.TripNShip.Services.RentalAgencyService;

public class UpdateRentalAgencyController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField contactField;
    @FXML
    private TextField ratingField;
    @FXML
    private Button updateAgencyButton;

    private RentalAgencyService rentalAgencyService;
    private RentalAgency agencyToUpdate;

    @FXML
    public void initialize() {
        rentalAgencyService = new RentalAgencyService();
    }


    public void setAgencyData(RentalAgency agency) {
        this.agencyToUpdate = agency;
        populateFields(agency);
    }

    private void populateFields(RentalAgency agency) {
        if (agency != null) {
            nameField.setText(agency.getNameAgency());
            addressField.setText(agency.getAddressAgency());
            contactField.setText(agency.getContactAgency());
            ratingField.setText(String.valueOf(agency.getRating()));
        }
    }

    @FXML
    private void handleUpdateAction() {
        if (validateInput()) {
            updateAgency();
            showSuccessAlert();
            closeWindow();
        }
    }

    private void updateAgency() {
        agencyToUpdate.setNameAgency(nameField.getText().trim());
        agencyToUpdate.setAddressAgency(addressField.getText().trim());
        agencyToUpdate.setContactAgency(contactField.getText().trim());
        agencyToUpdate.setRating(Float.parseFloat(ratingField.getText().trim()));

        rentalAgencyService.update(agencyToUpdate);
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errorMessage.append("Please enter the agency name.\n");
        }
        if (addressField.getText() == null || addressField.getText().trim().isEmpty()) {
            errorMessage.append("Please enter the agency address.\n");
        }
        if (contactField.getText() == null || contactField.getText().trim().isEmpty()) {
            errorMessage.append("Please enter the contact information.\n");
        }

        String ratingText = ratingField.getText();
        try {
            float rating = Float.parseFloat(ratingText);
            if (rating < 0 || rating > 5) {
                errorMessage.append("Rating must be between 0 and 5.\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("Please enter a valid rating number.\n");
        }

        if (errorMessage.length() > 0) {
            showAlert("Invalid Input", "Please correct the following errors:", errorMessage.toString());
            return false;
        }

        return true;
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Agency Updated");
        alert.setContentText("The rental agency has been successfully updated.");
        alert.showAndWait();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) updateAgencyButton.getScene().getWindow();
        stage.close();
    }
}
