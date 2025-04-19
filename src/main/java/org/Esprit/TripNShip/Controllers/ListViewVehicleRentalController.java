package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.Esprit.TripNShip.Entities.StautCircuit;
import org.Esprit.TripNShip.Entities.Vehicle;
import org.Esprit.TripNShip.Entities.VehicleRental;
import org.Esprit.TripNShip.Services.VehicleRentalService;
import javafx.util.Callback;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ListViewVehicleRentalController {

    @FXML private ComboBox<Integer> entriesComboBox;
    @FXML private TextField searchField;
    @FXML private Button addVehicleRentalButton;
    @FXML private Button exportExcelButton;
    @FXML private TableView<VehicleRental> vehicleRentalTable;
    @FXML private TableColumn<VehicleRental, LocalDateTime> startDateColumn;
    @FXML private TableColumn<VehicleRental, LocalDateTime> endDateColumn;
    @FXML private TableColumn<VehicleRental, Float> priceColumn;
    @FXML private TableColumn<VehicleRental, StautCircuit> statusColumn;
    @FXML private TableColumn<VehicleRental, Vehicle> vehicleColumn;
    @FXML private TableColumn<VehicleRental, Void> actionColumn;

    private ObservableList<VehicleRental> vehicleRentalList = FXCollections.observableArrayList();
    private VehicleRentalService vehicleRentalService = new VehicleRentalService();

    @FXML
    public void initialize() {

        setupTableColumns();

        // Load data
        loadVehicleRentals();

        // Setup entries combo box
        setupEntriesComboBox();

        // Setup search functionality
        setupSearchFilter();

        // Setup action buttons
        setupActionButtons();
    }

    private void setupTableColumns() {
        // Configure date formatting
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        startDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });

        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });

        priceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statusCircuit"));
        vehicleColumn.setCellValueFactory(new PropertyValueFactory<>("vehicle"));

        // Setup action column
        setupActionColumn();
    }

    private void setupActionColumn() {
        Callback<TableColumn<VehicleRental, Void>, TableCell<VehicleRental, Void>> cellFactory =
                new Callback<>() {
                    @Override
                    public TableCell<VehicleRental, Void> call(final TableColumn<VehicleRental, Void> param) {
                        return new TableCell<>() {
                            private final Button editButton = new Button("Edit");
                            private final Button deleteButton = new Button("Delete");
                            {
                                // Style buttons
                                editButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white;");
                                deleteButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white;");

                                // Set button actions
                                editButton.setOnAction(event -> {
                                    VehicleRental rental = getTableView().getItems().get(getIndex());
                                    handleEditRental(rental);
                                });

                                deleteButton.setOnAction(event -> {
                                    VehicleRental rental = getTableView().getItems().get(getIndex());
                                    handleDeleteRental(rental);
                                });
                            }

                            @Override
                            protected void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    HBox buttons = new HBox(5, editButton, deleteButton);
                                    setGraphic(buttons);
                                }
                            }
                        };
                    }
                };

        actionColumn.setCellFactory(cellFactory);
    }

    private void loadVehicleRentals() {
        // Clear existing data
        vehicleRentalList.clear();

        // Fetch data from service
        vehicleRentalList.addAll(vehicleRentalService.getAll());

        // Set data to table
        vehicleRentalTable.setItems(vehicleRentalList);
    }

    private void setupEntriesComboBox() {
        entriesComboBox.getItems().addAll(10, 25, 50, 100);
        entriesComboBox.getSelectionModel().selectFirst();

        entriesComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            vehicleRentalTable.setPrefHeight(newVal * 30 + 100); // Adjust table height
        });
    }

    private void setupSearchFilter() {
        FilteredList<VehicleRental> filteredData = new FilteredList<>(vehicleRentalList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(rental -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Check all fields for match
                if (rental.getVehicle().getModel().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (rental.getStatus().toString().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (String.valueOf(rental.getTotalPrice()).contains(lowerCaseFilter)) {
                    return true;
                }

                return false;
            });
        });

        SortedList<VehicleRental> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(vehicleRentalTable.comparatorProperty());
        vehicleRentalTable.setItems(sortedData);
    }

    private void setupActionButtons() {
        addVehicleRentalButton.setOnAction(event -> handleAddRental());
        exportExcelButton.setOnAction(event -> handleExportExcel());
    }

    private void handleAddRental() {
        // Implement logic to open add rental dialog
        System.out.println("Add new vehicle rental");
    }

    private void handleEditRental(VehicleRental rental) {
        // Implement logic to open edit dialog
        System.out.println("Edit rental: " + rental.getIdRental());
    }

    private void handleDeleteRental(VehicleRental rental) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Vehicle Rental");
        alert.setContentText("Are you sure you want to delete this rental?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
              //  boolean success = vehicleRentalService.delete(rental.getIdRental());
              //  if (success) {
                //    vehicleRentalList.remove(rental);
                  //  showAlert("Success", "Rental deleted successfully", Alert.AlertType.INFORMATION);
               // } else {
                 //   showAlert("Error", "Failed to delete rental", Alert.AlertType.ERROR);
               // }
            }
        });
    }

    private void handleExportExcel() {
        // Implement export to Excel functionality
        System.out.println("Export to Excel");
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}