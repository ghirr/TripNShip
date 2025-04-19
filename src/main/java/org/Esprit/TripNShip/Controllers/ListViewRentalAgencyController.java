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
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.CircuitBooking;
import org.Esprit.TripNShip.Entities.RentalAgency;
import org.Esprit.TripNShip.Services.RentalAgencyService;

import java.io.IOException;
import java.util.List;

public class ListViewRentalAgencyController {

    @FXML private ComboBox<String> entriesComboBox;
    @FXML private Region spacer;
    @FXML private TextField searchField;
    @FXML private Button addAgencyButton;
    @FXML private Button exportExcelButton;
    @FXML private TableView<RentalAgency> agencyTable;
    @FXML private TableColumn<RentalAgency, String> nameColumn;
    @FXML private TableColumn<RentalAgency, String> addressColumn;
    @FXML private TableColumn<RentalAgency, String> contactColumn;
    @FXML private TableColumn<RentalAgency, Float> ratingColumn;
    @FXML private TableColumn<RentalAgency, Void> actionColumn;

    private final RentalAgencyService rentalAgencyService = new RentalAgencyService();
    private ObservableList<RentalAgency> agencyList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Configuration des colonnes avec les noms EXACTS des propriétés
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nameAgency"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("addressAgency"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactAgency"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        // Chargement des données
        loadAgenciesFromDatabase();

        // Configuration du ComboBox
        entriesComboBox.getItems().addAll("10", "25", "50", "100");
        entriesComboBox.getSelectionModel().selectFirst();

        // Configuration des boutons
        addAgencyButton.setOnAction(event -> openAddAgencyForm());
        exportExcelButton.setOnAction(event -> handleExportExcel());

        // Configuration de la recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAgencies(newValue);
        });

        // Configuration de la colonne Actions
        setupActionButtons();
    }

    private void loadAgenciesFromDatabase() {
        List<RentalAgency> agencies = rentalAgencyService.getAll();
        agencyList.setAll(agencies);
        agencyTable.setItems(agencyList);
    }


    private void filterAgencies(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            agencyTable.setItems(agencyList);
        } else {
            ObservableList<RentalAgency> filteredList = FXCollections.observableArrayList();
            for (RentalAgency agency : agencyList) {
                if (agency.getNameAgency().toLowerCase().contains(searchText.toLowerCase()) ||
                        agency.getAddressAgency().toLowerCase().contains(searchText.toLowerCase()) ||
                        agency.getContactAgency().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(agency);
                }
            }
            agencyTable.setItems(filteredList);
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
                    RentalAgency agency = getTableView().getItems().get(getIndex());
                    handleEditAgency(agency);
                });

                deleteButton.setOnAction(event -> {
                    RentalAgency agency = getTableView().getItems().get(getIndex());
                    handleDeleteAgency(agency);
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonsContainer);
            }
        });
    }


    private void handleEditAgency(RentalAgency agency) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/UpdateRentalAgency.fxml"));
            Parent root = loader.load();

            UpdateRentalAgencyController controller = loader.getController();

            controller.setAgencyData(agency);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Agence de Location");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleDeleteAgency(RentalAgency agency) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Agency");
        alert.setContentText("Are you sure you want to delete this agency?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                rentalAgencyService.delete(agency);
                loadAgenciesFromDatabase();
            }
        });
    }

    private void handleExportExcel() {
        // Implémentez l'export Excel
    }


    private void openAddAgencyForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/AddRentalAgency.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Rental Agency");

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}