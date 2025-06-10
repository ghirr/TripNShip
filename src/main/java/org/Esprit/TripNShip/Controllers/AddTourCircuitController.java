package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.TourCircuit;
import org.Esprit.TripNShip.Services.TourCircuitService;

import java.net.URL;
import java.util.ResourceBundle;

public class AddTourCircuitController implements Initializable {

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField priceField;

    @FXML
    private TextField durationField;

    @FXML
    private TextField destinationField;

    @FXML
    private CheckBox guideCheckBox;

    @FXML
    private Button addCircuitButton;

    @FXML
    private ImageView closeIcon;

    private final TourCircuitService tourCircuitService = new TourCircuitService();
    private ResourceBundle rs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.rs = resources;
        addCircuitButton.setOnAction(event -> addCircuit());
    }

    private void addCircuit() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String priceText = priceField.getText().trim();
        String duration = durationField.getText().trim();
        String destination = destinationField.getText().trim();
        boolean guideIncluded = guideCheckBox.isSelected();

        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || duration.isEmpty() || destination.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        try {
            float price = Float.parseFloat(priceText);

            TourCircuit circuit = new TourCircuit();
            circuit.setNameCircuit(name);
            circuit.setDescriptionCircuit(description);
            circuit.setPriceCircuit(price);
            circuit.setDuration(duration);
            circuit.setDestination(destination);
            circuit.setGuideIncluded(guideIncluded);

            tourCircuitService.add(circuit);

            showAlert(Alert.AlertType.INFORMATION,"success.title", "success.message");
            clearFields();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Price must be a valid number.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add tour circuit: " + e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        descriptionArea.clear();
        priceField.clear();
        durationField.clear();
        destinationField.clear();
        guideCheckBox.setSelected(false);
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
