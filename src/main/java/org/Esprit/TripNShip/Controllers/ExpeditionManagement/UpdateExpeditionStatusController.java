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
import java.util.ArrayList;
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

    private Expedition expedition;
    private Transporter transporter;
    private ServiceExpedition expeditionService;
    private ServiceTrackingHistory trackingService;
    private TransporterExpeditionsController parentController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        expeditionService = new ServiceExpedition();
        trackingService = new ServiceTrackingHistory();

        // Set up button handlers
        updateBtn.setOnAction(event -> handleUpdateStatus());
        cancelBtn.setOnAction(event -> closeWindow());

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

        // Load valid next statuses based on current status
        loadNextPossibleStatuses(expedition.getPackageStatus());
    }

    private void loadNextPossibleStatuses(PackageStatus currentStatus) {
        List<PackageStatus> allStatuses = new ArrayList<>(Arrays.asList(PackageStatus.values()));
        List<PackageStatus> availableStatuses;

        // Determine which statuses are valid transitions from the current status
        switch (currentStatus) {
            case PENDING:
                availableStatuses = Arrays.asList(PackageStatus.SHIPPED, PackageStatus.CANCELLED);
                break;
            case SHIPPED:
                availableStatuses = Arrays.asList(PackageStatus.IN_TRANSIT, PackageStatus.CANCELLED);
                break;
            case IN_TRANSIT:
                availableStatuses = Arrays.asList(PackageStatus.DELIVERED, PackageStatus.CANCELLED);
                break;
            case DELIVERED:
                availableStatuses = new ArrayList<>(); // Terminal state
                break;
            case CANCELLED:
                availableStatuses = new ArrayList<>(); // Terminal state
                break;
            default:
                availableStatuses = allStatuses;
                break;
        }

        // Filter out the current status to avoid setting to the same status
        availableStatuses = availableStatuses.stream()
                .filter(status -> status != currentStatus)
                .collect(Collectors.toList());

        statusComboBox.setItems(FXCollections.observableArrayList(availableStatuses));

        // Disable the update button if no transitions are available
        if (availableStatuses.isEmpty()) {
            statusComboBox.setPromptText("No status changes possible");
            statusComboBox.setDisable(true);
            updateBtn.setDisable(true);
        } else {
            statusComboBox.setPromptText("Select new status");
            statusComboBox.setDisable(false);
            updateBtn.setDisable(false);
        }
    }

    public void setTransporter(Transporter transporter) {
        this.transporter = transporter;
    }

    public void setParentController(TransporterExpeditionsController parentController) {
        this.parentController = parentController;
    }

    private void handleUpdateStatus() {
        if (!validateInputs()) {
            return;
        }

        try {
            PackageStatus newStatus = statusComboBox.getValue();
            String newLocation = locationField.getText().trim();

            // Update the expedition status and location
            expedition.setPackageStatus(newStatus);
            expedition.setCurrentLocation(newLocation);

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
        if (statusComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a new status.");
            return false;
        }

        if (locationField.getText().trim().isEmpty()) {
            locationErrorLabel.setText("Location is required");
            return false;
        }

        return true;
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