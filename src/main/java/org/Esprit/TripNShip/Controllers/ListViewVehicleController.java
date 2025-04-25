package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Vehicle;
import org.Esprit.TripNShip.Services.VehicleService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ListViewVehicleController {

    @FXML private ComboBox<String> entriesComboBox;
    @FXML private TextField searchField;
    @FXML private Button addVehicleButton;
    @FXML private Button exportExcelButton;
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, String> brandColumn;
    @FXML private TableColumn<Vehicle, String> modelColumn;
    @FXML private TableColumn<Vehicle, String> licensePlateColumn;
    @FXML private TableColumn<Vehicle, Float> dailyPriceColumn;
    @FXML private TableColumn<Vehicle, Boolean> availabilityColumn;
    @FXML private TableColumn<Vehicle, String> typeColumn;
    @FXML private TableColumn<Vehicle, Void> actionColumn;

    private final VehicleService vehicleService = new VehicleService();
    private ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        try {
            // First verify all FXML components are injected
            if (entriesComboBox == null || searchField == null || vehicleTable == null) {
                throw new IllegalStateException("FXML injection failed - critical components are null");
            }

            // Initialize combo box items first
            entriesComboBox.setItems(FXCollections.observableArrayList("5", "10", "25", "50"));

            // Then setup the rest of the components
            setupTableColumns();
            loadVehiclesFromDatabase();
            setupEntriesComboBox();
            setupSearch();
            setupActionButtons();

            // Set button actions
            addVehicleButton.setOnAction(event -> handleAddVehicle());
            exportExcelButton.setOnAction(event -> handleExportExcel());

        } catch (Exception e) {
            showAlert("Initialization Error", "Failed to initialize controller: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        try {
            brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
            modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
            licensePlateColumn.setCellValueFactory(new PropertyValueFactory<>("licensePlate"));
            dailyPriceColumn.setCellValueFactory(new PropertyValueFactory<>("dailyPrice"));
            availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));
            typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

            // Format columns
            dailyPriceColumn.setCellFactory(column -> new TableCell<Vehicle, Float>() {
                @Override
                protected void updateItem(Float price, boolean empty) {
                    super.updateItem(price, empty);
                    setText(empty || price == null ? null : String.format("$%.2f", price));
                }
            });

            availabilityColumn.setCellFactory(column -> new TableCell<Vehicle, Boolean>() {
                @Override
                protected void updateItem(Boolean available, boolean empty) {
                    super.updateItem(available, empty);
                    setText(empty || available == null ? null : available ? "Yes" : "No");
                }
            });
        } catch (Exception e) {
            showAlert("Table Error", "Failed to setup table columns: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadVehiclesFromDatabase() {
        try {
            List<Vehicle> vehicles = vehicleService.getAll();
            vehicleList.setAll(vehicles);
            vehicleTable.setItems(vehicleList);
        } catch (Exception e) {
            showAlert("Data Error", "Failed to load vehicles: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupEntriesComboBox() {
        try {
            entriesComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && vehicleList != null) {
                    try {
                        int entries = Integer.parseInt(newVal);
                        vehicleTable.setItems((ObservableList<Vehicle>) vehicleList.subList(0, Math.min(entries, vehicleList.size())));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format in combo box: " + newVal);
                    }
                }
            });

            // Set default selection only if items exist
            if (!entriesComboBox.getItems().isEmpty()) {
                entriesComboBox.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            showAlert("Combo Box Error", "Failed to setup entries combo box: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterVehicles(newValue);
        });
    }

    private void filterVehicles(String searchText) {
        try {
            if (searchText == null || searchText.isEmpty()) {
                vehicleTable.setItems(vehicleList);
            } else {
                ObservableList<Vehicle> filteredList = FXCollections.observableArrayList();
                String lowerCaseFilter = searchText.toLowerCase();

                for (Vehicle vehicle : vehicleList) {
                    if ((vehicle.getBrand() != null && vehicle.getBrand().toLowerCase().contains(lowerCaseFilter)) ||
                            (vehicle.getModel() != null && vehicle.getModel().toLowerCase().contains(lowerCaseFilter)) ||
                            (vehicle.getLicensePlate() != null && vehicle.getLicensePlate().toLowerCase().contains(lowerCaseFilter)) ||
                            (vehicle.getType() != null && vehicle.getType().toString().toLowerCase().contains(lowerCaseFilter)) ||
                            String.valueOf(vehicle.getDailyPrice()).contains(searchText)) {
                        filteredList.add(vehicle);
                    }
                }
                vehicleTable.setItems(filteredList);
            }
        } catch (Exception e) {
            showAlert("Filter Error", "Failed to filter vehicles: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupActionButtons() {
        try {
            actionColumn.setCellFactory(param -> new TableCell<>() {
                private final Button editButton = new Button("Edit");
                private final Button deleteButton = new Button("Delete");
                private final HBox buttonsContainer = new HBox(5, editButton, deleteButton);

                {
                    // Style buttons
                    editButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 5;");
                    deleteButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 5;");

                    // Set button actions
                    editButton.setOnAction(event -> {
                        Vehicle vehicle = getTableView().getItems().get(getIndex());
                       // handleEditVehicle(vehicle);
                    });

                    deleteButton.setOnAction(event -> {
                        Vehicle vehicle = getTableView().getItems().get(getIndex());
                        handleDeleteVehicle(vehicle);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : buttonsContainer);
                }
            });
        } catch (Exception e) {
            showAlert("Action Error", "Failed to setup action buttons: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleAddVehicle() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/AddVehicle.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add New Vehicle");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh after closing
            loadVehiclesFromDatabase();

        } catch (IOException e) {
            showAlert("Form Error", "Could not load the add vehicle form: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

   /* private void handleEditVehicle(Vehicle vehicle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/UpdateVehicle.fxml"));
            Parent root = loader.load();

            // ✅ Récupérer le contrôleur
            UpdateVehicleController controller = loader.getController();

            // ✅ Passer les données du véhicule sélectionné
            controller.setVehicleData(vehicle);

            Stage stage = new Stage();
            stage.setTitle("Edit Vehicle");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // ✅ Recharger les données après modification
            loadVehiclesFromDatabase();

        } catch (IOException e) {
            showAlert("Error", "Could not load the vehicle edit form: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
*/

    private void handleDeleteVehicle(Vehicle vehicle) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Delete Vehicle");
            alert.setContentText("Are you sure you want to delete " + vehicle.getBrand() + " " + vehicle.getModel() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                vehicleService.delete(vehicle);
                loadVehiclesFromDatabase();
            }
        } catch (Exception e) {
            showAlert("Deletion Error", "Failed to delete vehicle: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleExportExcel() {
        try {
            // Implement actual Excel export logic here
            showAlert("Info", "Excel export functionality will be implemented here", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Export Error", "Failed to export data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}