package org.Esprit.TripNShip.Controllers.ExpeditionManagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Expedition;
import org.Esprit.TripNShip.Entities.PackageStatus;
import org.Esprit.TripNShip.Entities.TrackingHistory;
import org.Esprit.TripNShip.Entities.Transporter;
import org.Esprit.TripNShip.Services.ServiceTrackingHistory;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ManageTrackingHistoryController implements Initializable {

    @FXML
    private Label headerLabel;

    @FXML
    private Label expeditionIdLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label fromCityLabel;

    @FXML
    private Label toCityLabel;

    @FXML
    private Button addNewEntryBtn;

    @FXML
    private Button refreshBtn;

    @FXML
    private ListView<TrackingHistory> trackingListView;

    @FXML
    private Button closeBtn;

    private Expedition expedition;
    private Transporter transporter;
    private TransporterExpeditionsController parentController;
    private ServiceTrackingHistory trackingService;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        trackingService = new ServiceTrackingHistory();

        // Set up custom cell factory for the ListView
        trackingListView.setCellFactory(createTrackingCellFactory());

        // Set button handlers
        addNewEntryBtn.setOnAction(event -> handleAddNewEntry());
        refreshBtn.setOnAction(event -> loadTrackingData());
        closeBtn.setOnAction(event -> closeWindow());
    }

    public void setExpedition(Expedition expedition) {
        this.expedition = expedition;

        // Update header with expedition number
        headerLabel.setText("Manage Tracking History - Expedition #" + expedition.getExpeditionId());

        // Update expedition details in the summary section
        expeditionIdLabel.setText(String.valueOf(expedition.getExpeditionId()));
        statusLabel.setText(expedition.getPackageStatus().toString());
        fromCityLabel.setText(expedition.getDepartureCity());
        toCityLabel.setText(expedition.getArrivalCity());

        // Style the status label based on current status
        statusLabel.getStyleClass().clear();
        statusLabel.getStyleClass().addAll("expedition-status", getStatusStyleClass(expedition.getPackageStatus()));

        // Load tracking data
        loadTrackingData();
    }

    public void setTransporter(Transporter transporter) {
        this.transporter = transporter;
    }

    public void setParentController(TransporterExpeditionsController parentController) {
        this.parentController = parentController;
    }

    private void loadTrackingData() {
        if (expedition != null && transporter != null) {
            List<TrackingHistory> allTrackingList = trackingService.getTrackingByExpedition(expedition.getExpeditionId());

            // Filter to only show entries created by this transporter
            List<TrackingHistory> transporterTrackingList = allTrackingList.stream()
                    .filter(entry -> entry.getUpdatedBy().getIdUser() == transporter.getIdUser())
                    .sorted(Comparator.comparing(TrackingHistory::getTimestamp).reversed())
                    .collect(Collectors.toList());

            trackingListView.getItems().clear();
            trackingListView.getItems().addAll(transporterTrackingList);

            // Add placeholder text if no tracking data found
            if (transporterTrackingList.isEmpty()) {
                trackingListView.setPlaceholder(new Label("You haven't created any tracking entries for this expedition yet."));
            }
        }
    }

    private void handleAddNewEntry() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExpeditionManagement/AddTrackingEntry.fxml"));
            Parent root = loader.load();

            AddTrackingEntryController controller = loader.getController();
            controller.setExpedition(expedition);
            controller.setTransporter(transporter);
            controller.setCallback(this::loadTrackingData);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Tracking Entry");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            // Refresh data after dialog closes
            loadTrackingData();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open add tracking form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEditEntry(TrackingHistory trackingEntry) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExpeditionManagement/EditTrackingEntry.fxml"));
            Parent root = loader.load();

            EditTrackingEntryController controller = loader.getController();
            controller.setTrackingEntry(trackingEntry);
            controller.setCallback(this::loadTrackingData);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Tracking Entry");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            // Refresh data after dialog closes
            loadTrackingData();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open edit tracking form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeleteEntry(TrackingHistory trackingEntry) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setHeaderText("Delete Tracking Entry");
        confirmDialog.setContentText("Are you sure you want to delete this tracking entry? This action cannot be undone.");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = trackingService.deleteTrackingEntry(trackingEntry.getId());

            if (deleted) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Tracking entry deleted successfully.");
                loadTrackingData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete tracking entry.");
            }
        }
    }

    private javafx.util.Callback<ListView<TrackingHistory>, ListCell<TrackingHistory>> createTrackingCellFactory() {
        return new javafx.util.Callback<ListView<TrackingHistory>, ListCell<TrackingHistory>>() {
            @Override
            public ListCell<TrackingHistory> call(ListView<TrackingHistory> param) {
                return new ListCell<TrackingHistory>() {
                    @Override
                    protected void updateItem(TrackingHistory item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                            return;
                        }

                        // Create tracking entry UI
                        VBox container = new VBox();
                        container.getStyleClass().add("tracking-entry");
                        container.setPadding(new Insets(10, 15, 10, 15));
                        container.setSpacing(10);

                        // Header with timestamp and status
                        HBox header = new HBox();
                        header.setAlignment(Pos.CENTER_LEFT);
                        header.setSpacing(15);

                        Label timestampLabel = new Label(item.getTimestamp().format(dateTimeFormatter));
                        timestampLabel.getStyleClass().add("tracking-timestamp");

                        Label statusLabel = new Label(item.getStatus().toString());
                        statusLabel.getStyleClass().addAll("expedition-status", getStatusStyleClass(item.getStatus()));
                        statusLabel.setPadding(new Insets(3, 10, 3, 10));

                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);

                        // Action buttons
                        Button editButton = new Button("Edit");
                        editButton.getStyleClass().add("secondary-button");
                        editButton.setOnAction(event -> handleEditEntry(item));

                        Button deleteButton = new Button("Delete");
                        deleteButton.getStyleClass().add("danger-button");
                        deleteButton.setOnAction(event -> handleDeleteEntry(item));

                        HBox buttonBox = new HBox(10);
                        buttonBox.getChildren().addAll(editButton, deleteButton);

                        header.getChildren().addAll(timestampLabel, statusLabel, spacer, buttonBox);

                        // Location and notes
                        Label locationLabel = new Label(item.getLocationNote());
                        locationLabel.getStyleClass().add("tracking-location");
                        locationLabel.setWrapText(true);

                        // Add components to container
                        container.getChildren().addAll(header, locationLabel);

                        // Set alternating background colors
                        if (getIndex() % 2 == 0) {
                            container.setStyle("-fx-background-color: white;");
                        } else {
                            container.setStyle("-fx-background-color: #f5f6fa;");
                        }

                        setGraphic(container);
                    }
                };
            }
        };
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
        Stage stage = (Stage) closeBtn.getScene().getWindow();
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