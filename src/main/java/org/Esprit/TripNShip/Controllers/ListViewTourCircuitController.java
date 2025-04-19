package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.TourCircuit;
import org.Esprit.TripNShip.Services.TourCircuitService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ListViewTourCircuitController {

    @FXML private ComboBox<String> entriesComboBox;
    @FXML private Region spacer;
    @FXML private TextField searchField;
    @FXML private Button addTourCircuitButton;
    @FXML private Button exportExcelButton;
    @FXML private TableView<TourCircuit> tourCircuitTable;
    @FXML private TableColumn<TourCircuit, String> titleColumn;
    @FXML private TableColumn<TourCircuit, String> locationColumn;
    @FXML private TableColumn<TourCircuit, Float> priceColumn;
    @FXML private TableColumn<TourCircuit, String> durationColumn;
    @FXML private TableColumn<TourCircuit, String> agencyColumn;
    @FXML private TableColumn<TourCircuit, Void> actionColumn;

    private final TourCircuitService tourCircuitService = new TourCircuitService();
    private ObservableList<TourCircuit> circuitList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Configuration des colonnes
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("nameCircuit"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("priceCircuit"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        agencyColumn.setCellValueFactory(new PropertyValueFactory<>("descriptionCircuit")); // A adapter selon votre besoin

        // Chargement des données
        loadCircuitsFromDatabase();

        // Configuration du ComboBox
        entriesComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            // Implémenter la pagination si nécessaire
        });

        // Configuration des boutons
        addTourCircuitButton.setOnAction(event -> handleAddTourCircuit());
        exportExcelButton.setOnAction(event -> handleExportExcel());

        // Configuration de la recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterCircuits(newValue);
        });

        // Configuration de la colonne Actions
        setupActionButtons();
    }

    private void loadCircuitsFromDatabase() {
        List<TourCircuit> circuits = tourCircuitService.getAll();
        circuitList.setAll(circuits);
        tourCircuitTable.setItems(circuitList);
    }

    private void filterCircuits(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            tourCircuitTable.setItems(circuitList);
        } else {
            ObservableList<TourCircuit> filteredList = FXCollections.observableArrayList();
            for (TourCircuit circuit : circuitList) {
                if (circuit.getNameCircuit().toLowerCase().contains(searchText.toLowerCase()) ||
                        circuit.getDestination().toLowerCase().contains(searchText.toLowerCase()) ||
                        circuit.getDuration().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(circuit);
                }
            }
            tourCircuitTable.setItems(filteredList);
        }
    }

    private void setupActionButtons() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonsContainer = new HBox(5, editButton, deleteButton);

            {
                editButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 5;");
                deleteButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 5;");

                editButton.setOnAction(event -> {
                    TourCircuit circuit = getTableView().getItems().get(getIndex());
                    handleEditCircuit(circuit);
                });

                deleteButton.setOnAction(event -> {
                    TourCircuit circuit = getTableView().getItems().get(getIndex());
                    handleDeleteCircuit(circuit);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonsContainer);
            }
        });
    }

    private void handleAddTourCircuit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/AddTourCircuit.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add New Tour Circuit");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            // Obtenir le contrôleur et configurer si nécessaire
            AddTourCircuitController controller = loader.getController();

            stage.showAndWait();

            // Rafraîchir après fermeture
            loadCircuitsFromDatabase();

        } catch (IOException e) {
            showAlert("Error", "Could not load the form: " + e.getMessage());
        }
    }

    private void handleEditCircuit(TourCircuit circuit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Esprit/TripNShip/Views/EditTourCircuitForm.fxml"));
            Parent root = loader.load();

           // EditTourCircuitController controller = loader.getController();
           // controller.setTourCircuit(circuit);

            Stage stage = new Stage();
            stage.setTitle("Edit Tour Circuit");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadCircuitsFromDatabase();
        } catch (IOException e) {
            showAlert("Error", "Could not load the edit form: " + e.getMessage());
        }
    }

    private void handleDeleteCircuit(TourCircuit circuit) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete Tour Circuit");
        alert.setContentText("Are you sure you want to delete " + circuit.getNameCircuit() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            tourCircuitService.delete(circuit);
            loadCircuitsFromDatabase();
        }
    }

    private void handleExportExcel() {
        // Implémentez l'export Excel ici
        showAlert("Info", "Excel export functionality will be implemented here");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}