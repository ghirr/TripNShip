package org.Esprit.TripNShip.Controllers.ExpeditionManagement;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Services.ServiceExpedition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;

public class AdminExpeditionsController implements Initializable {

    @FXML
    private TableView<Expedition> expeditionsTable;

    @FXML
    private TableColumn<Expedition, Integer> idColumn;

    @FXML
    private TableColumn<Expedition, PackageStatus> statusColumn;

    @FXML
    private TableColumn<Expedition, String> clientColumn;

    @FXML
    private TableColumn<Expedition, String> transporterColumn;

    @FXML
    private TableColumn<Expedition, PackageType> packageTypeColumn;

    @FXML
    private TableColumn<Expedition, Double> weightColumn;

    @FXML
    private TableColumn<Expedition, String> routeColumn;

    @FXML
    private TableColumn<Expedition, Date> sendDateColumn;

    @FXML
    private TableColumn<Expedition, Date> deliveryDateColumn;

    @FXML
    private TableColumn<Expedition, Double> shippingCostColumn;

    @FXML
    private ComboBox<PackageStatus> statusFilterCombo;

    @FXML
    private TextField searchField;

    @FXML
    private Button clearFiltersBtn;

    @FXML
    private Button refreshBtn;

    @FXML
    private Button exportBtn;

    @FXML
    private Button viewDetailsBtn;

    @FXML
    private Button viewTrackingBtn;

    @FXML
    private Button deleteExpeditionBtn;

    @FXML
    private Label totalExpeditionsLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label timestampLabel;

    private ServiceExpedition expeditionService;
    private ObservableList<Expedition> expeditionsList = FXCollections.observableArrayList();
    private FilteredList<Expedition> filteredExpeditions;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        expeditionService = new ServiceExpedition();

        // Initialize table columns
        setupTableColumns();

        // Initialize status filter combo box
        statusFilterCombo.getItems().add(null); // Add null option for "All"
        statusFilterCombo.getItems().addAll(PackageStatus.values());
        statusFilterCombo.setPromptText("All Statuses");

        // Set up filtering
        setupFiltering();

        // Set up button actions
        setupButtons();

        // Load data
        loadExpeditions();

        // Set the current timestamp
        timestampLabel.setText("Last updated: " + LocalDateTime.now().format(timestampFormatter));
    }

    private void setupTableColumns() {
        // Basic columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("expeditionId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("packageStatus"));
        packageTypeColumn.setCellValueFactory(new PropertyValueFactory<>("packageType"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        sendDateColumn.setCellValueFactory(new PropertyValueFactory<>("sendDate"));
        deliveryDateColumn.setCellValueFactory(new PropertyValueFactory<>("estimatedDeliveryDate"));
        shippingCostColumn.setCellValueFactory(new PropertyValueFactory<>("shippingCost"));

        // Custom cell factories for complex columns
        clientColumn.setCellValueFactory(cellData -> {
            Client client = cellData.getValue().getClient();
            if (client != null) {
                return new SimpleStringProperty(client.getFirstName() + " " + client.getLastName());
            } else {
                return new SimpleStringProperty("N/A");
            }
        });

//        transporterColumn.setCellValueFactory(cellData -> {
//            Transporter transporter = cellData.getValue().getTransporter();
//            if (transporter != null) {
//               // return new SimpleStringProperty(transporter.getFirstName() + " " + transporter.getLastName());
////            } else {
//                return new SimpleStringProperty("Not Assigned");
//            }
//        });

        routeColumn.setCellValueFactory(cellData -> {
            Expedition exp = cellData.getValue();
            return new SimpleStringProperty(exp.getDepartureCity() + " → " + exp.getArrivalCity());
        });

        // Format date columns
        sendDateColumn.setCellFactory(column -> new TableCell<Expedition, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                }
            }
        });

        deliveryDateColumn.setCellFactory(column -> new TableCell<Expedition, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                }
            }
        });

        // Format currency column
        shippingCostColumn.setCellFactory(column -> new TableCell<Expedition, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(item));
                }
            }
        });

        // Format status column with colored cells
        statusColumn.setCellFactory(column -> new TableCell<Expedition, PackageStatus>() {
            @Override
            protected void updateItem(PackageStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());

                    // Set background color based on status
                    String textColor = "white";
                    String bgColor = switch (item) {
                        case PENDING -> "#f39c12"; // Orange
                        case SHIPPED -> "#9b59b6"; // Purple
                        case IN_TRANSIT -> "#3498db"; // Blue
                        case DELIVERED -> "#2ecc71"; // Green
                        case CANCELLED -> "#e74c3c"; // Red
                        default -> "#7f8c8d"; // Gray
                    };

                    setStyle("-fx-background-color: " + bgColor + ";" +
                            "-fx-text-fill: " + textColor + ";" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 2px 8px;" +
                            "-fx-background-radius: 4px;");
                }
            }
        });

        // Add row selection listener
        expeditionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            viewDetailsBtn.setDisable(!hasSelection);
            viewTrackingBtn.setDisable(!hasSelection);
            deleteExpeditionBtn.setDisable(!hasSelection);
        });
    }

    private void setupFiltering() {
        // Create filtered list wrapped around the original data
        filteredExpeditions = new FilteredList<>(expeditionsList, p -> true);

        // Set up filter predicates
        statusFilterCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            updateFilters();
        });

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            updateFilters();
        });

        // Wrap the FilteredList in a SortedList
        SortedList<Expedition> sortedData = new SortedList<>(filteredExpeditions);

        // Bind the SortedList comparator to the TableView comparator
        sortedData.comparatorProperty().bind(expeditionsTable.comparatorProperty());

        // Add sorted (and filtered) data to the table
        expeditionsTable.setItems(sortedData);
    }

    private void updateFilters() {
        PackageStatus statusFilter = statusFilterCombo.getValue();
        String searchText = searchField.getText().toLowerCase().trim();

        filteredExpeditions.setPredicate(expedition -> {
            // Filter by status if selected
            if (statusFilter != null && expedition.getPackageStatus() != statusFilter) {
                return false;
            }

            // If no search text, return true (show all that match status)
            if (searchText.isEmpty()) {
                return true;
            }

            // Search in client name
            if (expedition.getClient() != null) {
                String clientName = expedition.getClient().getFirstName() + " " + expedition.getClient().getLastName();
                if (clientName.toLowerCase().contains(searchText)) {
                    return true;
                }
            }

            // Search in transporter name
            if (expedition.getTransporter() != null) {
//                String transporterName = expedition.getTransporter().getFirstName() + " " +
//                        expedition.getTransporter().getLastName();
//                if (transporterName.toLowerCase().contains(searchText)) {
//                    return true;
//                }
            }

            // Search in cities
            if (expedition.getDepartureCity().toLowerCase().contains(searchText) ||
                    expedition.getArrivalCity().toLowerCase().contains(searchText) ||
                    expedition.getCurrentLocation().toLowerCase().contains(searchText)) {
                return true;
            }

            // Search in expedition ID
            if (String.valueOf(expedition.getExpeditionId()).contains(searchText)) {
                return true;
            }

            return false;
        });

        // Update total count
        updateTotalCount();
    }

    private void updateTotalCount() {
        totalExpeditionsLabel.setText(String.format("Total: %d expeditions", filteredExpeditions.size()));
    }

    private void setupButtons() {
        // Disable detail buttons initially (no selection)
        viewDetailsBtn.setDisable(true);
        viewTrackingBtn.setDisable(true);
        deleteExpeditionBtn.setDisable(true);

        // Set up button handlers
        refreshBtn.setOnAction(event -> {
            loadExpeditions();
            statusLabel.setText("Data refreshed");
            timestampLabel.setText("Last updated: " + LocalDateTime.now().format(timestampFormatter));
        });

        clearFiltersBtn.setOnAction(event -> {
            statusFilterCombo.setValue(null);
            searchField.clear();
            statusLabel.setText("Filters cleared");
        });

        exportBtn.setOnAction(event -> exportToCSV());

        viewDetailsBtn.setOnAction(event -> {
            Expedition selectedExpedition = expeditionsTable.getSelectionModel().getSelectedItem();
            if (selectedExpedition != null) {
                handleViewExpedition(selectedExpedition);
            }
        });

        viewTrackingBtn.setOnAction(event -> {
            Expedition selectedExpedition = expeditionsTable.getSelectionModel().getSelectedItem();
            if (selectedExpedition != null) {
                handleViewTracking(selectedExpedition);
            }
        });

        deleteExpeditionBtn.setOnAction(event -> {
            Expedition selectedExpedition = expeditionsTable.getSelectionModel().getSelectedItem();
            if (selectedExpedition != null) {
                handleDeleteExpedition(selectedExpedition);
            }
        });
    }

    private void loadExpeditions() {
        try {
            // Clear existing data
            expeditionsList.clear();

            // Get all expeditions
            List<Expedition> allExpeditions = expeditionService.getAll();

            // Add to observable list
            expeditionsList.addAll(allExpeditions);

            // Update UI
            updateTotalCount();
            statusLabel.setText("Loaded " + allExpeditions.size() + " expeditions");
        } catch (Exception e) {
            statusLabel.setText("Error loading data: " + e.getMessage());
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Data Loading Error");
            alert.setHeaderText("Failed to load expeditions");
            alert.setContentText("An error occurred while loading expeditions: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleViewExpedition(Expedition expedition) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExpeditionManagement/ExpeditionDetails.fxml"));
            Parent root = loader.load();

            ExpeditionDetailsController controller = loader.getController();
            controller.setExpedition(expedition);

            Stage stage = new Stage();
            stage.setTitle("Expedition Details - #" + expedition.getExpeditionId());
            stage.setScene(new Scene(root));
            stage.show();

            statusLabel.setText("Viewing details for expedition #" + expedition.getExpeditionId());
        } catch (IOException e) {
            statusLabel.setText("Error opening details view");
            e.printStackTrace();
        }
    }

    private void handleViewTracking(Expedition expedition) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExpeditionManagement/TrackingHistory.fxml"));
            Parent root = loader.load();

            TrackingHistoryController controller = loader.getController();
            controller.setExpedition(expedition);

            Stage stage = new Stage();
            stage.setTitle("Tracking History - Expedition #" + expedition.getExpeditionId());
            stage.setScene(new Scene(root));
            stage.show();

            statusLabel.setText("Viewing tracking history for expedition #" + expedition.getExpeditionId());
        } catch (IOException e) {
            statusLabel.setText("Error opening tracking view");
            e.printStackTrace();
        }
    }

    private void handleDeleteExpedition(Expedition expedition) {
        // Show confirmation dialog
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
                    // Remove from the list
                    expeditionsList.remove(expedition);
                    updateTotalCount();

                    statusLabel.setText("Expedition #" + expedition.getExpeditionId() + " deleted successfully");

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Deletion Successful");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Expedition #" + expedition.getExpeditionId() + " has been deleted successfully.");
                    successAlert.showAndWait();
                } else {
                    statusLabel.setText("Failed to delete expedition #" + expedition.getExpeditionId());

                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Deletion Failed");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Failed to delete the expedition. It might be referenced by other records.");
                    errorAlert.showAndWait();
                }
            } catch (Exception e) {
                statusLabel.setText("Error during deletion: " + e.getMessage());
                e.printStackTrace();

                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Deletion Error");
                errorAlert.setHeaderText("An error occurred during deletion");
                errorAlert.setContentText("Error details: " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }

    private void exportToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Expeditions Report");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("expeditions_report_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                ".csv");

        File file = fileChooser.showSaveDialog(expeditionsTable.getScene().getWindow());

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // Write header
                writer.write("ID,Status,Client,Transporter,Package Type,Weight (kg),Departure City,Arrival City," +
                        "Current Location,Send Date,Delivery Date,Shipping Cost\n");

                // Write data rows
                for (Expedition expedition : filteredExpeditions) {
                    String clientName = expedition.getClient() != null ?
                            expedition.getClient().getFirstName() + " " + expedition.getClient().getLastName() : "N/A";

             //       String transporterName = expedition.getTransporter() != null ?
//                            expedition.getTransporter().getFirstName() + " " + expedition.getTransporter().getLastName() : "Not Assigned";

//                    String row = String.format("%d,%s,\"%s\",\"%s\",%s,%.2f,\"%s\",\"%s\",\"%s\",%s,%s,%.2f\n",
//                            expedition.getExpeditionId(),
//                            expedition.getPackageStatus(),
//                            clientName,
//                            transporterName,
//                            expedition.getPackageType(),
//                            expedition.getWeight(),
//                            expedition.getDepartureCity(),
//                            expedition.getArrivalCity(),
//                            expedition.getCurrentLocation(),
//                            dateFormat.format(expedition.getSendDate()),
//                            dateFormat.format(expedition.getEstimatedDeliveryDate()),
//                            expedition.getShippingCost());
//
//                    writer.write(row);
                }

                statusLabel.setText("Exported " + filteredExpeditions.size() + " expeditions to CSV");

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Export Successful");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Successfully exported " + filteredExpeditions.size() +
                        " expeditions to " + file.getName());
                successAlert.showAndWait();

            } catch (IOException e) {
                statusLabel.setText("Error exporting to CSV: " + e.getMessage());
                e.printStackTrace();

                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Export Error");
                errorAlert.setHeaderText("Failed to export data");
                errorAlert.setContentText("An error occurred: " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }
}