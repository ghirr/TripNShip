package org.Esprit.TripNShip.Controllers.ExpeditionManagement;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.PackageStatus;
import org.Esprit.TripNShip.Entities.TrackingHistory;
import org.Esprit.TripNShip.Services.ServiceExpedition;
import org.Esprit.TripNShip.Services.ServiceTrackingHistory;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class EditTrackingEntryController implements Initializable {

    @FXML
    private Label entryIdLabel;

    @FXML
    private Label createdDateLabel;

    @FXML
    private ComboBox<PackageStatus> statusComboBox;

    @FXML
    private Label statusErrorLabel;

    @FXML
    private TextArea locationNotesArea;

    @FXML
    private Label notesErrorLabel;

    @FXML
    private Button saveBtn;

    @FXML
    private Button cancelBtn;

    private TrackingHistory trackingEntry;
    private ServiceTrackingHistory trackingService;
    private ServiceExpedition expeditionService;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private Runnable refreshCallback;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        trackingService = new ServiceTrackingHistory();
        expeditionService = new ServiceExpedition();

        // Initialize status combo box with all package statuses
        statusComboBox.setItems(FXCollections.observableArrayList(PackageStatus.values()));

        // Set up button handlers
        saveBtn.setOnAction(event -> handleSave());
        cancelBtn.setOnAction(event -> closeWindow());

        // Set up validation listeners
        statusComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            statusErrorLabel.setText(newValue == null ? "Status is required" : "");
        });

        locationNotesArea.textProperty().addListener((observable, oldValue, newValue) -> {
            notesErrorLabel.setText(newValue.isEmpty() ? "Location notes are required" : "");
        });
    }

    public void setTrackingEntry(TrackingHistory trackingEntry) {
        this.trackingEntry = trackingEntry;

        // Populate form with tracking entry data
        entryIdLabel.setText("#" + trackingEntry.getId());
        createdDateLabel.setText(trackingEntry.getTimestamp().format(dateTimeFormatter));
        statusComboBox.setValue(trackingEntry.getStatus());
        locationNotesArea.setText(trackingEntry.getLocationNote());
    }

    public void setCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    private void handleSave() {
        if (!validateInputs()) {
            return;
        }

        try {
            // Update tracking entry with new values
            trackingEntry.setStatus(statusComboBox.getValue());
            trackingEntry.setLocationNote(locationNotesArea.getText().trim());

            // Save the updated tracking entry
            boolean updated = trackingService.updateTrackingEntry(trackingEntry);

            if (updated) {
                // Also update the expedition status if needed
                trackingEntry.getExpedition().setPackageStatus(trackingEntry.getStatus());
                trackingEntry.getExpedition().setCurrentLocation(extractLocation(trackingEntry.getLocationNote()));
                expeditionService.update(trackingEntry.getExpedition());

                showAlert(Alert.AlertType.INFORMATION, "Success", "Tracking entry updated successfully!");

                // Refresh parent view if callback is provided
                if (refreshCallback != null) {
                    refreshCallback.run();
                }

                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update tracking entry.");
            }

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
        } else {
            statusErrorLabel.setText("");
        }

        if (locationNotesArea.getText().trim().isEmpty()) {
            notesErrorLabel.setText("Location notes are required");
            isValid = false;
        } else {
            notesErrorLabel.setText("");
        }

        return isValid;
    }

    // Helper method to extract location from notes (assuming format is "Location - Additional notes")
    private String extractLocation(String locationNote) {
        if (locationNote == null || locationNote.isEmpty()) {
            return "";
        }

        // If the note contains a separator, take just the location part
        int separatorIndex = locationNote.indexOf(" - ");
        if (separatorIndex > 0) {
            return locationNote.substring(0, separatorIndex);
        }

        // Otherwise return the whole note as the location
        return locationNote;
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
