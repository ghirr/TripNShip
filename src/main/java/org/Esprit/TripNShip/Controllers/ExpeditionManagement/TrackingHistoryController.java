package org.Esprit.TripNShip.Controllers.ExpeditionManagement;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.Esprit.TripNShip.Entities.Expedition;
import org.Esprit.TripNShip.Entities.PackageStatus;
import org.Esprit.TripNShip.Entities.TrackingHistory;
import org.Esprit.TripNShip.Services.ServiceTrackingHistory;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class TrackingHistoryController implements Initializable {

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
    private Label currentLocationLabel;

    @FXML
    private Label sentDateLabel;

    @FXML
    private Label deliveryDateLabel;

    @FXML
    private ListView<TrackingHistory> trackingListView;

    @FXML
    private Button refreshBtn;

    @FXML
    private Button closeBtn;

    private Expedition expedition;
    private ServiceTrackingHistory trackingService;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        trackingService = new ServiceTrackingHistory();

        // Set up custom cell factory for the ListView
        trackingListView.setCellFactory(createTrackingCellFactory());

        // Set button handlers
        refreshBtn.setOnAction(event -> loadTrackingData());
        closeBtn.setOnAction(event -> closeWindow());
    }

    public void setExpedition(Expedition expedition) {
        this.expedition = expedition;

        // Update header with expedition number
        headerLabel.setText("Tracking History - Expedition #" + expedition.getExpeditionId());

        // Update expedition details in the summary section
        expeditionIdLabel.setText(String.valueOf(expedition.getExpeditionId()));
        statusLabel.setText(expedition.getPackageStatus().toString());
        fromCityLabel.setText(expedition.getDepartureCity());
        toCityLabel.setText(expedition.getArrivalCity());
        currentLocationLabel.setText(expedition.getCurrentLocation());

        // Safely format dates
        if (expedition.getSendDate() != null) {
            sentDateLabel.setText(dateFormat.format(expedition.getSendDate()));
        } else {
            sentDateLabel.setText("Not set");
        }

        if (expedition.getEstimatedDeliveryDate() != null) {
            deliveryDateLabel.setText(dateFormat.format(expedition.getEstimatedDeliveryDate()));
        } else {
            deliveryDateLabel.setText("Not set");
        }

        // Style the status label based on current status
        statusLabel.getStyleClass().clear();
        statusLabel.getStyleClass().addAll("expedition-status", getStatusStyleClass(expedition.getPackageStatus()));

        // Load tracking data
        loadTrackingData();
    }
    private void loadTrackingData() {
        if (expedition != null) {
            List<TrackingHistory> trackingList = trackingService.getTrackingByExpedition(expedition.getExpeditionId());

            // Sort by timestamp descending (newest first)
            trackingList.sort(Comparator.comparing(TrackingHistory::getTimestamp).reversed());

            trackingListView.getItems().clear();
            trackingListView.getItems().addAll(trackingList);

            // Add placeholder text if no tracking data found
            if (trackingList.isEmpty()) {
                trackingListView.setPlaceholder(new Label("No tracking updates available for this expedition yet."));
            }
        }
    }

    private Callback<ListView<TrackingHistory>, ListCell<TrackingHistory>> createTrackingCellFactory() {
        return new Callback<>() {
            @Override
            public ListCell<TrackingHistory> call(ListView<TrackingHistory> param) {
                return new ListCell<>() {
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
                        container.setSpacing(5);

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

                        String updaterName = "Unknown";
                        if (item.getUpdatedBy() != null) {
                            updaterName = item.getUpdatedBy().getFirstName() + " " + item.getUpdatedBy().getLastName();
                        }
                        Label updaterLabel = new Label("Updated by: " + updaterName);
                        updaterLabel.getStyleClass().add("tracking-updater");

                        header.getChildren().addAll(timestampLabel, statusLabel, spacer, updaterLabel);

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
}