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
import java.util.Optional;
import java.util.ResourceBundle;

public class ClientExpeditionsController implements Initializable {

    @FXML
    private TilePane expeditionTiles;

    @FXML
    private Button newExpeditionBtn;

    @FXML
    private Button refreshBtn;

    private final int STATIC_CLIENT_ID = 1;
    private ServiceExpedition expeditionService;
    private UserService userService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        expeditionService = new ServiceExpedition();
        userService = new UserService();

        // Load expeditions data
        loadExpeditions();

        // Set up button handlers
        newExpeditionBtn.setOnAction(event -> handleNewExpedition());
        refreshBtn.setOnAction(event -> loadExpeditions());
    }

    private void loadExpeditions() {
        // Clear existing tiles
        expeditionTiles.getChildren().clear();

        // Get expeditions for the specified client ID
        List<Expedition> expeditions = expeditionService.getExpeditionsForClient(STATIC_CLIENT_ID);
        System.out.println("Client expeditions loaded: " + expeditions.size());

        if (expeditions.isEmpty()) {
            Label noExpeditionsLabel = new Label("No expeditions found for this client");
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
        card.setMinHeight(180);
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

        // Left column
        Label routeLabel = new Label("Route: " + expedition.getDepartureCity() + " → " + expedition.getArrivalCity());
        Label weightLabel = new Label("Weight: " + expedition.getWeight() + " kg");
        Label costLabel = new Label("Cost: $" + String.format("%.2f", expedition.getShippingCost()));

        // Right column
        Label dateLabel = new Label("Send date: " + dateFormat.format(expedition.getSendDate()));
        Label deliveryLabel = new Label("Est. delivery: " + dateFormat.format(expedition.getEstimatedDeliveryDate()));

        String transporterName = expedition.getTransporter() != null ?
                expedition.getTransporter().getFirstName() + " " + expedition.getTransporter().getLastName() :
                "Not assigned";
        Label transporterLabel = new Label("Transporter: " + transporterName);

        // Add to grid
        detailsGrid.add(routeLabel, 0, 0);
        detailsGrid.add(weightLabel, 0, 1);
        detailsGrid.add(costLabel, 0, 2);
        detailsGrid.add(dateLabel, 1, 0);
        detailsGrid.add(deliveryLabel, 1, 1);
        detailsGrid.add(transporterLabel, 1, 2);

        content.getChildren().add(detailsGrid);

        // Create buttons
        HBox buttons = new HBox();
        buttons.setSpacing(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(12, 0, 0, 0));

        // View details button
        Button viewDetailsBtn = new Button("Details");
        viewDetailsBtn.setPrefWidth(90);
        viewDetailsBtn.getStyleClass().addAll("primary-button");
        viewDetailsBtn.setOnAction(event -> handleViewExpedition(expedition));

        // Track history button
        Button trackHistoryBtn = new Button("Track");
        trackHistoryBtn.setPrefWidth(90);
        trackHistoryBtn.getStyleClass().addAll("secondary-button");
        trackHistoryBtn.setOnAction(event -> handleTrackHistory(expedition));

        // Edit button (only available for PENDING expeditions)
        Button editBtn = new Button("Edit");
        editBtn.setPrefWidth(90);
        editBtn.getStyleClass().addAll("neutral-button");
        editBtn.setOnAction(event -> handleEditExpedition(expedition));

        // Disable edit button if expedition is not in PENDING state
        if (expedition.getPackageStatus() != PackageStatus.PENDING) {
            editBtn.setDisable(true);
            editBtn.setTooltip(new Tooltip("Only PENDING expeditions can be edited"));
        }

        // Delete button
        Button deleteBtn = new Button("Delete");
        deleteBtn.setPrefWidth(90);
        deleteBtn.getStyleClass().addAll("danger-button");
        deleteBtn.setOnAction(event -> handleDeleteExpedition(expedition));

        // Disable delete button if expedition is not in PENDING state
        if (expedition.getPackageStatus() != PackageStatus.PENDING) {
            deleteBtn.setDisable(true);
            deleteBtn.setTooltip(new Tooltip("Only PENDING expeditions can be deleted"));
        }

        buttons.getChildren().addAll(viewDetailsBtn, trackHistoryBtn, editBtn, deleteBtn);

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

    private void handleNewExpedition() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExpeditionManagement/NewExpedition.fxml"));
            Parent root = loader.load();

            NewExpeditionController controller = loader.getController();

            // The issue is here - getById() returns a User object that can't be directly cast to Client
            User user = userService.getById(STATIC_CLIENT_ID);

            // Fix: Check if the role is CLIENT before casting
            if (user != null && user.getRole() == Role.CLIENT) {
                // Instead of casting directly, create a new Client object with the User data
                Client client = new Client(
                        user.getIdUser(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getGender(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getProfilePhoto(),
                        user.getBirthdayDate(),
                        user.getPhoneNumber()
                );

                controller.setClientId(STATIC_CLIENT_ID);
                controller.setClient(client);
                controller.setParentController(this);

                Stage stage = new Stage();
                stage.setTitle("Create New Expedition");
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Client with ID " + STATIC_CLIENT_ID + " not found.");
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open new expedition form: " + e.getMessage());
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

    private void handleTrackHistory(Expedition expedition) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExpeditionManagement/TrackingHistory.fxml"));
            Parent root = loader.load();

            TrackingHistoryController controller = loader.getController();
            controller.setExpedition(expedition);

            Stage stage = new Stage();
            stage.setTitle("Tracking History");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open tracking history: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEditExpedition(Expedition expedition) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExpeditionManagement/EditExpedition.fxml"));
            Parent root = loader.load();

            EditExpeditionController controller = loader.getController();
            controller.setExpedition(expedition);
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Edit Expedition");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open edit expedition form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeleteExpedition(Expedition expedition) {
        // Confirm deletion with dialog
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setHeaderText("Delete Expedition #" + expedition.getExpeditionId() + "?");
        confirmDialog.setContentText("Are you sure you want to delete this expedition? This action cannot be undone.");

        ButtonType deleteButton = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmDialog.getButtonTypes().setAll(deleteButton, cancelButton);

        Optional<ButtonType> result = confirmDialog.showAndWait();

        if (result.isPresent() && result.get() == deleteButton) {
            try {
                // Delete the expedition
                boolean deleted = expeditionService.delete1(expedition);

                if (deleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Expedition Deleted",
                            "Expedition #" + expedition.getExpeditionId() + " has been deleted successfully.");

                    // Refresh expeditions list
                    loadExpeditions();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Deletion Failed",
                            "Could not delete expedition. Please try again.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Deletion Error",
                        "An error occurred while deleting the expedition: " + e.getMessage());
                e.printStackTrace();
            }
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