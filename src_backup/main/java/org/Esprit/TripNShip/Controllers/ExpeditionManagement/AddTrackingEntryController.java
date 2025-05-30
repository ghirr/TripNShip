package org.Esprit.TripNShip.Controllers.ExpeditionManagement;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Services.ServiceExpedition;
import org.Esprit.TripNShip.Services.ServiceTrackingHistory;

import java.io.IOException;
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

    @FXML
    private Button mapPickerBtn;

    private Expedition expedition;
    private Transporter transporter;
    private ServiceExpedition expeditionService;
    private ServiceTrackingHistory trackingService;
    private TransporterExpeditionsController parentController;
    private Runnable refreshCallback;

    // Location coordinates
    private double latitude = 0;
    private double longitude = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        expeditionService = new ServiceExpedition();
        trackingService = new ServiceTrackingHistory();

        // Initialize status combo box with all package statuses
        statusComboBox.setItems(FXCollections.observableArrayList(PackageStatus.values()));

        // Set up button handlers
        saveBtn.setOnAction(event -> handleSaveTracking());
        cancelBtn.setOnAction(event -> closeWindow());

        // Configure map picker button
        mapPickerBtn.setOnAction(event -> openLocationPicker());

        // Set up validation listeners
        statusComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            statusErrorLabel.setText(newValue == null ? "Status is required" : "");
        });

        locationField.textProperty().addListener((observable, oldValue, newValue) -> {
            locationErrorLabel.setText(newValue.isEmpty() ? "Location is required" : "");
        });

        // Make location field read-only (will be set via map picker)
        locationField.setEditable(false);
    }

    private void openLocationPicker() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExpeditionManagement/MapLocationPicker.fxml"));
            Parent root = loader.load();

            MapLocationPickerController controller = loader.getController();

            // Set callback to handle location selection
            controller.setCallback(new MapLocationPickerController.LocationCallback() {
                @Override
                public void locationSelected(String location, double lat, double lng) {
                    // Update location field and store coordinates
                    locationField.setText(location);
                    latitude = lat;
                    longitude = lng;

                    // Clear error if any
                    locationErrorLabel.setText("");
                }
            });

            // If expedition has a current location, try to set initial map position
            // For now, we'll use default location in the map

            Stage stage = new Stage();
            stage.setTitle("Pick Package Location");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.showAndWait();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open location picker: " + e.getMessage());
            e.printStackTrace();
        }
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

            // Create the location note combining location, coordinates, and additional notes
            String locationNote = location;

            // Append coordinates if available
            if (latitude != 0 && longitude != 0) {
                locationNote += " [" + latitude + "," + longitude + "]";
            }

            // Append notes if available
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
                expedition.setCurrentLocation(locationNote);
                expeditionService.update(expedition);
            }

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Tracking entry added successfully!");

            // Refresh parent controller and close window
            if (parentController != null) {
                parentController.refreshExpeditions();
            }
            // Execute callback if provided
            if (refreshCallback != null) {
                refreshCallback.run();
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
            locationErrorLabel.setText("Location is required. Please select a location on the map.");
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

    public void setCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }
}
