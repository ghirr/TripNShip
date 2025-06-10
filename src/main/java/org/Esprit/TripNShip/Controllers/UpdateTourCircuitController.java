package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
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
    @FXML
    private ImageView closeIcon;

    private TourCircuitService tourCircuitService;
    private TourCircuit originalCircuit;

    @FXML
    public void initialize() {
        tourCircuitService = new TourCircuitService();
    }

    public void setCircuitData(TourCircuit circuit) {
        this.originalCircuit = circuit;
        populateFields(circuit);
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
            // Mettre à jour les valeurs dans l'objet
            originalCircuit.setNameCircuit(nameField.getText().trim());
            originalCircuit.setDescriptionCircuit(descriptionArea.getText().trim());
            originalCircuit.setPriceCircuit(Float.parseFloat(priceField.getText().trim()));
            originalCircuit.setDuration(durationField.getText().trim());
            originalCircuit.setDestination(destinationField.getText().trim());
            originalCircuit.setGuideIncluded(guideCheckBox.isSelected());

            // Appel du service sans récupérer de retour boolean
            try {
                tourCircuitService.update(originalCircuit); // suppose que cette méthode gère l'erreur si besoin
                showSuccessAlert();
                closeWindow();
            } catch (Exception e) {
                showAlert("Erreur", "Échec de la mise à jour", e.getMessage());
            }
        }
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

    @FXML
    private void handleCloseForm() {
        Stage stage = (Stage) closeIcon.getScene().getWindow();
        stage.close();
    }

    private void closeWindow() {
        Stage stage = (Stage) updateCircuitButton.getScene().getWindow();
        stage.close();
    }
}
