package org.Esprit.TripNShip.Controllers.ExpeditionManagement;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Services.ServiceExpedition;
import org.Esprit.TripNShip.Services.ServiceTrackingHistory;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class AddTrackingEntryController implements Initializable {

    @FXML
    private Label expeditionIdLabel;

    @FXML
    private Label fromCityLabel;

    @FXML
    private Label toCityLabel;

    @FXML
    private ComboBox<PackageStatus> statusComboBox;

    @FXML
    private Label statusErrorLabel;

    @FXML
    private TextField locationField;

    @FXML
    private Label locationErrorLabel;

    @FXML
    private TextArea notesTextArea;

    @FXML
    private Button saveBtn;

    @FXML
    private Button cancelBtn;

    private Expedition expedition;
    private Transporter transporter;
    private ServiceExpedition expeditionService;
    private ServiceTrackingHistory trackingService;
    private TransporterExpeditionsController parentController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        expeditionService = new ServiceExpedition();
        trackingService = new ServiceTrackingHistory();

        // Initialize status combo box with all package statuses
        statusComboBox.setItems(FXCollections.observableArrayList(PackageStatus.values()));

        // Set up button handlers
        saveBtn.setOnAction(event -> handleSaveTracking());
        cancelBtn.setOnAction(event -> closeWindow());

        // Set up validation listeners
        statusComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            statusErrorLabel.setText(newValue == null ? "Status is required" : "");
        });

        locationField.textProperty().addListener((observable, oldValue, newValue) -> {
            locationErrorLabel.setText(newValue.isEmpty() ? "Location is required" : "");
        });
    }

    public void setExpedition(Expedition expedition) {
        this.expedition = expedition;

        // Update UI with expedition details
        expeditionIdLabel.setText("#" + expedition.getExpeditionId());
        fromCityLabel.setText(expedition.getDepartureCity());
        toCityLabel.setText(expedition.getArrivalCity());
        locationField.setText(expedition.getCurrentLocation());

        // Set the current status as default
        statusComboBox.setValue(expedition.getPackageStatus());
    }

    public void setTransporter(Transporter transporter) {
        this.transporter = transporter;
    }

    public void setParentController(TransporterExpeditionsController parentController) {
        this.parentController = parentController;
    }

    private void handleSaveTracking() {
        if (!validateInputs()) {
            return;
        }

        try {
            PackageStatus status = statusComboBox.getValue();
            String location = locationField.getText().trim();
            String notes = notesTextArea.getText().trim();

            // Create the location note combining location and additional notes
            String locationNote = location;
            if (!notes.isEmpty()) {
                locationNote += " - " + notes;
            }

            // Create tracking history entry
            TrackingHistory trackingEntry = new TrackingHistory(
                    expedition,
                    status,
                    locationNote,
                    LocalDateTime.now(),
                    transporter
            );

            // Save tracking entry
            trackingService.addTrackingEntry(trackingEntry);

            // Update expedition status and location if changed
            if (expedition.getPackageStatus() != status || !expedition.getCurrentLocation().equals(location)) {
                expedition.setPackageStatus(status);
                expedition.setCurrentLocation(location);
                expeditionService.update(expedition);
            }

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Tracking entry added successfully!");

            // Refresh parent controller and close window
            if (parentController != null) {
                parentController.refreshExpeditions();
            }

            closeWindow();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (statusComboBox.getValue() == null) {
            statusErrorLabel.setText("Status is required");
            isValid = false;
        }

        if (locationField.getText().trim().isEmpty()) {
            locationErrorLabel.setText("Location is required");
            isValid = false;
        }

        return isValid;
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
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