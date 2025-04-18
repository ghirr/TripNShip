package org.Esprit.TripNShip.Controllers.ExpeditionManagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Services.ServiceExpedition;
import org.Esprit.TripNShip.Services.UserService;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TransporterExpeditionsController implements Initializable {

    @FXML
    private TilePane expeditionTiles;

    @FXML
    private Button filterAllBtn;

    @FXML
    private Button filterPendingBtn;

    @FXML
    private Button filterInTransitBtn;

    @FXML
    private Button refreshBtn;

    private final int STATIC_TRANSPORTER_ID = 9; // For testing, in real app this would come from login
    private ServiceExpedition expeditionService;
    private UserService userService;
    private PackageStatus currentFilter = null; // null means show all

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        expeditionService = new ServiceExpedition();
        userService = new UserService();

        // Load expeditions data
        loadExpeditions();

        // Set up button handlers
        filterAllBtn.setOnAction(event -> {
            currentFilter = null;
            updateFilterButtons(filterAllBtn);
            loadExpeditions();
        });

        filterPendingBtn.setOnAction(event -> {
            currentFilter = PackageStatus.PENDING;
            updateFilterButtons(filterPendingBtn);
            loadExpeditions();
        });

        filterInTransitBtn.setOnAction(event -> {
            currentFilter = PackageStatus.IN_TRANSIT;
            updateFilterButtons(filterInTransitBtn);
            loadExpeditions();
        });

        refreshBtn.setOnAction(event -> loadExpeditions());
    }

    private void updateFilterButtons(Button activeButton) {
        // Reset all buttons to secondary style
        filterAllBtn.getStyleClass().remove("primary-button");
        filterPendingBtn.getStyleClass().remove("primary-button");
        filterInTransitBtn.getStyleClass().remove("primary-button");

        filterAllBtn.getStyleClass().add("secondary-button");
        filterPendingBtn.getStyleClass().add("secondary-button");
        filterInTransitBtn.getStyleClass().add("secondary-button");

        // Set active button to primary style
        activeButton.getStyleClass().remove("secondary-button");
        activeButton.getStyleClass().add("primary-button");
    }

    private void loadExpeditions() {
        // Clear existing tiles
        expeditionTiles.getChildren().clear();

        // Get expeditions for the specified transporter ID
        List<Expedition> expeditions = expeditionService.getExpeditionsForTransporter(STATIC_TRANSPORTER_ID);

        // Apply filter if set
        if (currentFilter != null) {
            expeditions = expeditions.stream()
                    .filter(e -> e.getPackageStatus() == currentFilter)
                    .collect(Collectors.toList());
        }

        System.out.println("Transporter expeditions loaded: " + expeditions.size());

        if (expeditions.isEmpty()) {
            Label noExpeditionsLabel = new Label("No expeditions found matching the filter");
            noExpeditionsLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
            expeditionTiles.getChildren().add(noExpeditionsLabel);
        } else {
            for (Expedition expedition : expeditions) {
                expeditionTiles.getChildren().add(createExpeditionCard(expedition));
            }
        }
    }

    private VBox createExpeditionCard(Expedition expedition) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // Create card container
        VBox card = new VBox();
        card.getStyleClass().add("expedition-card");
        card.setSpacing(12);
        card.setPadding(new Insets(15));
        card.setMinHeight(200);
        card.setPrefWidth(450);

        // Create card header with ID and status
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(15);
        header.setPadding(new Insets(0, 0, 8, 0)); // Add padding at the bottom

        Label idLabel = new Label("Expedition #" + expedition.getExpeditionId());
        idLabel.getStyleClass().add("expedition-card-header");

        Label statusLabel = new Label(expedition.getPackageStatus().toString());
        statusLabel.getStyleClass().addAll("expedition-status", getStatusStyleClass(expedition.getPackageStatus()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(idLabel, spacer, statusLabel);

        // Create content with expedition details
        VBox content = new VBox();
        content.setSpacing(8);
        content.getStyleClass().add("expedition-card-content");

        // Create two-column layout for details
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(20);
        detailsGrid.setVgap(8);

        // Client info
        String clientName = expedition.getClient() != null ?
                expedition.getClient().getFirstName() + " " + expedition.getClient().getLastName() :
                "Unknown Client";
        Label clientLabel = new Label("Client: " + clientName);

        // Left column
        Label routeLabel = new Label("Route: " + expedition.getDepartureCity() + " → " + expedition.getArrivalCity());
        Label weightLabel = new Label("Weight: " + expedition.getWeight() + " kg");
        Label packageTypeLabel = new Label("Type: " + expedition.getPackageType().toString());

        // Right column
        Label sendDateLabel = new Label("Send: " + dateFormat.format(expedition.getSendDate()));
        Label deliveryLabel = new Label("Delivery: " + dateFormat.format(expedition.getEstimatedDeliveryDate()));
        Label currentLocationLabel = new Label("Location: " + expedition.getCurrentLocation());

        // Add to grid
        detailsGrid.add(clientLabel, 0, 0, 2, 1); // Span 2 columns
        detailsGrid.add(routeLabel, 0, 1);
        detailsGrid.add(weightLabel, 0, 2);
        detailsGrid.add(packageTypeLabel, 0, 3);
        detailsGrid.add(sendDateLabel, 1, 1);
        detailsGrid.add(deliveryLabel, 1, 2);
        detailsGrid.add(currentLocationLabel, 1, 3);

        content.getChildren().add(detailsGrid);

        // Create buttons
        HBox buttons = new HBox();
        buttons.setSpacing(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        Button updateStatusBtn = new Button("Update Status");
        updateStatusBtn.setPrefWidth(120);
        updateStatusBtn.getStyleClass().addAll("primary-button");
        updateStatusBtn.setOnAction(event -> handleUpdateStatus(expedition));

        Button addTrackingBtn = new Button("Add Tracking");
        addTrackingBtn.setPrefWidth(120);
        addTrackingBtn.getStyleClass().addAll("secondary-button");
        addTrackingBtn.setOnAction(event -> handleAddTracking(expedition));

        Button viewDetailsBtn = new Button("Details");
        viewDetailsBtn.setPrefWidth(100);
        viewDetailsBtn.getStyleClass().addAll("neutral-button");
        viewDetailsBtn.setOnAction(event -> handleViewExpedition(expedition));

        buttons.getChildren().addAll(updateStatusBtn, addTrackingBtn, viewDetailsBtn);

        // Add all components to the card
        card.getChildren().addAll(header, content, buttons);

        // Add hover effect handler
        card.setOnMouseEntered(e -> card.getStyleClass().add("expedition-card-hover"));
        card.setOnMouseExited(e -> card.getStyleClass().remove("expedition-card-hover"));

        return card;
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

    private void handleUpdateStatus(Expedition expedition) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExpeditionManagement/UpdateExpeditionStatus.fxml"));
            Parent root = loader.load();

            UpdateExpeditionStatusController controller = loader.getController();

            // Get the current transporter
            Transporter transporter = (Transporter) userService.getById(STATIC_TRANSPORTER_ID);

            controller.setExpedition(expedition);
            controller.setTransporter(transporter);
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Update Expedition Status");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open update status form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleAddTracking(Expedition expedition) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExpeditionManagement/AddTrackingEntry.fxml"));
            Parent root = loader.load();

            AddTrackingEntryController controller = loader.getController();

            // Get the current transporter
            Transporter transporter = (Transporter) userService.getById(STATIC_TRANSPORTER_ID);

            controller.setExpedition(expedition);
            controller.setTransporter(transporter);
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Add Tracking Entry");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open tracking entry form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleViewExpedition(Expedition expedition) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExpeditionManagement/ExpeditionDetails.fxml"));
            Parent root = loader.load();

            ExpeditionDetailsController controller = loader.getController();
            controller.setExpedition(expedition);

            Stage stage = new Stage();
            stage.setTitle("Expedition Details");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open expedition details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void refreshExpeditions() {
        loadExpeditions();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}