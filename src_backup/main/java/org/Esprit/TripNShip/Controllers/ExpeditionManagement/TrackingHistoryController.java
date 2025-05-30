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
import javafx.util.Callback;
import org.Esprit.TripNShip.Entities.Expedition;
import org.Esprit.TripNShip.Entities.PackageStatus;
import org.Esprit.TripNShip.Entities.TrackingHistory;
import org.Esprit.TripNShip.Services.ServiceTrackingHistory;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // Regular expression pattern to extract coordinates from location text
    private static final Pattern COORDINATES_PATTERN = Pattern.compile("\\[([-+]?\\d+\\.\\d+),([-+]?\\d+\\.\\d+)\\]");

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

                        // Extract location text without coordinates for display
                        String locationText = item.getLocationNote();

                        // Check if we have coordinates in the location
                        double[] coordinates = extractCoordinates(locationText);

                        // Clean location text for display (remove the coordinates)
                        String displayLocation = locationText.replaceAll("\\s*\\[[-+]?\\d+\\.\\d+,[-+]?\\d+\\.\\d+\\]\\s*", " ");

                        // Location and notes
                        Label locationLabel = new Label(displayLocation);
                        locationLabel.getStyleClass().add("tracking-location");
                        locationLabel.setWrapText(true);

                        // Actions row
                        HBox actionsRow = new HBox();
                        actionsRow.setAlignment(Pos.CENTER_RIGHT);
                        actionsRow.setPadding(new Insets(10, 0, 0, 0));
                        actionsRow.setSpacing(10);

                        // Add "View on Map" button if coordinates are available
                        if (coordinates != null) {
                            Button mapButton = new Button("View on Map");
                            mapButton.getStyleClass().add("secondary-button");
                            mapButton.setOnAction(event -> showLocationOnMap(displayLocation, coordinates[0], coordinates[1]));
                            actionsRow.getChildren().add(mapButton);
                        }

                        // Add components to container
                        container.getChildren().addAll(header, locationLabel);

                        // Only add actions row if it has any children
                        if (!actionsRow.getChildren().isEmpty()) {
                            container.getChildren().add(actionsRow);
                        }

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

    /**
     * Extract latitude and longitude from location text if available
     * @param locationText The location text potentially containing coordinates
     * @return double array with [lat, lng] or null if not found
     */
    private double[] extractCoordinates(String locationText) {
        if (locationText == null || locationText.isEmpty()) {
            return null;
        }

        Matcher matcher = COORDINATES_PATTERN.matcher(locationText);
        if (matcher.find()) {
            try {
                double lat = Double.parseDouble(matcher.group(1));
                double lng = Double.parseDouble(matcher.group(2));
                return new double[] {lat, lng};
            } catch (NumberFormatException e) {
                // Log the error and continue
                System.err.println("Error parsing coordinates: " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    /**
     * Opens a map view showing the specified location
     */
    private void showLocationOnMap(String locationName, double latitude, double longitude) {
        try {
            // Load the location viewer map
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExpeditionManagement/LocationViewer.fxml"));
            Parent root = loader.load();

            // Get controller and set the location
            LocationViewerController controller = loader.getController();
            controller.setLocation(locationName, latitude, longitude);

            // Create and show the stage
            Stage stage = new Stage();
            stage.setTitle("Location Map - " + locationName);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.show();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open map view");
            alert.setContentText("An error occurred: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
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
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }
}
