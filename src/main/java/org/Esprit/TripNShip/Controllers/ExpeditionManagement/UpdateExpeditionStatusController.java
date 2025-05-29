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
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UpdateExpeditionStatusController implements Initializable {

    @FXML
    private Label expeditionIdLabel;

    @FXML
    private Label currentStatusLabel;

    @FXML
    private ComboBox<PackageStatus> statusComboBox;

    @FXML
    private TextField locationField;

    @FXML
    private Label locationErrorLabel;

    @FXML
    private Button updateBtn;

    @FXML
    private Button cancelBtn;

    // Map picker button
    @FXML
    private Button mapPickerBtn;

    private Expedition expedition;
    private Transporter transporter;
    private ServiceExpedition expeditionService;
    private ServiceTrackingHistory trackingService;
    private TransporterExpeditionsController parentController;

    // Location coordinates
    private double latitude = 0;
    private double longitude = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        expeditionService = new ServiceExpedition();
        trackingService = new ServiceTrackingHistory();

        // Set up button handlers
        updateBtn.setOnAction(event -> handleUpdateStatus());
        cancelBtn.setOnAction(event -> closeWindow());

        // Configure map picker button
        mapPickerBtn.setOnAction(event -> openLocationPicker());

        // Set up validation listeners
        locationField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                locationErrorLabel.setText("Location is required");
            } else {
                locationErrorLabel.setText("");
            }
        });
    }

    public void setExpedition(Expedition expedition) {
        this.expedition = expedition;

        // Update UI with expedition details
        expeditionIdLabel.setText("#" + expedition.getExpeditionId());
        currentStatusLabel.setText(expedition.getPackageStatus().toString());
        locationField.setText(expedition.getCurrentLocation());

        // Style the status label based on current status
        currentStatusLabel.getStyleClass().clear();
        currentStatusLabel.getStyleClass().addAll("expedition-status", getStatusStyleClass(expedition.getPackageStatus()));

        // Initialize the status combobox with all possible statuses except the current one
        initializeStatusComboBox(expedition.getPackageStatus());
    }

    private void initializeStatusComboBox(PackageStatus currentStatus) {
        // Get all statuses except the current one
        List<PackageStatus> availableStatuses = Arrays.stream(PackageStatus.values())
                .filter(status -> status != currentStatus)
                .collect(Collectors.toList());

        statusComboBox.setItems(FXCollections.observableArrayList(availableStatuses));

        // Set a default selection to the most likely next status
        switch (currentStatus) {
            case PENDING:
                selectStatus(PackageStatus.SHIPPED);
                break;
            case AWAITING_CLIENT_APPROVAL:
                selectStatus(PackageStatus.SHIPPED);
                break;
            case SHIPPED:
                selectStatus(PackageStatus.IN_TRANSIT);
                break;
            case IN_TRANSIT:
                selectStatus(PackageStatus.DELIVERED);
                break;
            default:
                // No default selection for other statuses
                break;
        }
    }

    private void selectStatus(PackageStatus status) {
        if (statusComboBox.getItems().contains(status)) {
            statusComboBox.setValue(status);
        }
    }

    public void setTransporter(Transporter transporter) {
        this.transporter = transporter;
    }

    public void setParentController(TransporterExpeditionsController parentController) {
        this.parentController = parentController;
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

            // If we have coordinates from a previous location, use them as initial
            // For now we'll use default (Tunisia)

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

    private void handleUpdateStatus() {
        if (!validateInputs()) {
            return;
        }

        try {
            PackageStatus newStatus = statusComboBox.getValue();
            String newLocation = locationField.getText().trim();

            // Create location string with coordinates if available
            String locationWithCoordinates = newLocation;
            if (latitude != 0 && longitude != 0) {
                locationWithCoordinates = newLocation + " [" + latitude + "," + longitude + "]";
            }

            // Update the expedition status and location
            expedition.setPackageStatus(newStatus);
            expedition.setCurrentLocation(locationWithCoordinates);

            // Save the updated expedition
            expeditionService.update(expedition);

            // Create a tracking history entry
            TrackingHistory trackingEntry = new TrackingHistory(
                    expedition,
                    newStatus,
                    newLocation,
                    LocalDateTime.now(),
                    transporter
            );

            // Save the tracking entry
            trackingService.addTrackingEntry(trackingEntry);

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Expedition status updated successfully!");

            // Refresh the parent controller and close window
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
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a new status.");
            isValid = false;
        }

        if (locationField.getText().trim().isEmpty()) {
            locationErrorLabel.setText("Location is required. Please select a location on the map.");
            isValid = false;
        }

        return isValid;
    }

    private String getStatusStyleClass(PackageStatus status) {
        switch (status) {
            case PENDING:
                return "status-pending";
            case SHIPPED:
                return "status-shipped";
            case IN_TRANSIT:
                return "status-in-transit";
            case DELIVERED:
                return "status-delivered";
            case CANCELLED:
                return "status-cancelled";
            case AWAITING_CLIENT_APPROVAL:
                return "status-awaiting-approval";
            default:
                return "status-pending";
        }
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