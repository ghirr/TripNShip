package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.TourCircuit;
import org.Esprit.TripNShip.Services.TourCircuitService;

public class UpdateTourCircuitController {

    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField priceField;
    @FXML private TextField durationField;
    @FXML private TextField destinationField;
    @FXML private CheckBox guideCheckBox;
    @FXML private Button updateCircuitButton;

    private TourCircuitService tourCircuitService;
    private TourCircuit originalCircuit;
    private TourCircuit modifiedCircuit;

    @FXML
    public void initialize() {
        tourCircuitService = new TourCircuitService();
    }

    public void setCircuitData(TourCircuit circuit) {
        this.originalCircuit = circuit;
        this.modifiedCircuit = new TourCircuit();

        modifiedCircuit.setIdCircuit(originalCircuit.getIdCircuit());
        modifiedCircuit.setNameCircuit(originalCircuit.getNameCircuit());
        modifiedCircuit.setDescriptionCircuit(originalCircuit.getDescriptionCircuit());
        modifiedCircuit.setPriceCircuit(originalCircuit.getPriceCircuit());
        modifiedCircuit.setDuration(originalCircuit.getDuration());
        modifiedCircuit.setDestination(originalCircuit.getDestination());
        modifiedCircuit.setGuideIncluded(originalCircuit.getGuideIncluded());
        modifiedCircuit.setCircuitList(originalCircuit.getCircuitList());

        populateFields(modifiedCircuit);
    }

    private void populateFields(TourCircuit circuit) {
        nameField.setText(circuit.getNameCircuit());
        descriptionArea.setText(circuit.getDescriptionCircuit());
        priceField.setText(String.format("%.2f", circuit.getPriceCircuit()));
        durationField.setText(circuit.getDuration());
        destinationField.setText(circuit.getDestination());
        guideCheckBox.setSelected(circuit.getGuideIncluded());
    }

    @FXML
    private void handleUpdateAction() {
        if (validateInput()) {
            updateCircuit();
            showSuccessAlert();
            closeWindow();
        }
    }

    private void updateCircuit() {
        modifiedCircuit.setNameCircuit(nameField.getText().trim());
        modifiedCircuit.setDescriptionCircuit(descriptionArea.getText().trim());
        modifiedCircuit.setPriceCircuit(Float.parseFloat(priceField.getText().trim()));
        modifiedCircuit.setDuration(durationField.getText().trim());
        modifiedCircuit.setDestination(destinationField.getText().trim());
        modifiedCircuit.setGuideIncluded(guideCheckBox.isSelected());

        // Appel du service (ne retourne rien)
        tourCircuitService.update(modifiedCircuit);

        // Mise à jour de l'objet original
        originalCircuit.setNameCircuit(modifiedCircuit.getNameCircuit());
        originalCircuit.setDescriptionCircuit(modifiedCircuit.getDescriptionCircuit());
        originalCircuit.setPriceCircuit(modifiedCircuit.getPriceCircuit());
        originalCircuit.setDuration(modifiedCircuit.getDuration());
        originalCircuit.setDestination(modifiedCircuit.getDestination());
        originalCircuit.setGuideIncluded(modifiedCircuit.getGuideIncluded());
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errorMessage.append("Veuillez saisir le nom du circuit.\n");
        }

        if (descriptionArea.getText() == null || descriptionArea.getText().trim().isEmpty()) {
            errorMessage.append("Veuillez saisir la description.\n");
        }

        try {
            float price = Float.parseFloat(priceField.getText().trim());
            if (price <= 0) {
                errorMessage.append("Le prix doit être supérieur à 0.\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("Veuillez saisir un prix valide.\n");
        }

        if (durationField.getText() == null || durationField.getText().trim().isEmpty()) {
            errorMessage.append("Veuillez saisir la durée.\n");
        }

        if (destinationField.getText() == null || destinationField.getText().trim().isEmpty()) {
            errorMessage.append("Veuillez saisir la destination.\n");
        }

        if (errorMessage.length() > 0) {
            showAlert("Erreur de saisie", "Veuillez corriger les erreurs suivantes :", errorMessage.toString());
            return false;
        }

        return true;
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Circuit mis à jour");
        alert.setContentText("Le circuit a été mis à jour avec succès.");
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
        Stage stage = (Stage) updateCircuitButton.getScene().getWindow();
        stage.close();
    }
}
