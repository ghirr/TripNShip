package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    // UI Elements
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

    // Services
    private final RentalAgencyService rentalAgencyService = new RentalAgencyService();

    // Data
    private ObservableList<RentalAgency> agencyList = FXCollections.observableArrayList();

    // Initialization
    @FXML
    private void initialize() {
        setupTableColumns();
        setupSearchField();
        setupButtons();
        loadAgenciesFromDatabase();
        setupActionButtons();
    }

    // Table setup
    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nameAgency"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("addressAgency"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactAgency"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
    }

    private void loadAgenciesFromDatabase() {
        List<RentalAgency> agencies = rentalAgencyService.getAll();
        agencyList.setAll(agencies);
        agencyTable.setItems(agencyList);
    }



    // Search field setup
    private void setupSearchField() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterAgencies(newValue));
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

    // Button setup
    private void setupButtons() {
        addAgencyButton.setOnAction(event -> openAddAgencyForm());
        exportExcelButton.setOnAction(event -> handleExportExcel());
    }

    // Action buttons in table
    private void setupActionButtons() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/icons8-edit-64.png")));
            private final ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/icons8-delete-48.png")));
            private final HBox hbox = new HBox(10, editIcon, deleteIcon);

            {
                editIcon.setFitWidth(30);
                editIcon.setFitHeight(30);
                deleteIcon.setFitWidth(30);
                deleteIcon.setFitHeight(30);

                editIcon.setStyle("-fx-cursor: hand;");
                deleteIcon.setStyle("-fx-cursor: hand;");

                editIcon.setOnMouseClicked(event -> {
                    RentalAgency agency = getTableView().getItems().get(getIndex());
                    handleEditAgency(agency);
                });

                deleteIcon.setOnMouseClicked(event -> {
                    RentalAgency agency = getTableView().getItems().get(getIndex());
                    handleDeleteAgency(agency);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }


    // CRUD Actions
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

            loadAgenciesFromDatabase(); // Refresh
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteAgency(RentalAgency agency) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Suppression d'agence");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette agence ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                rentalAgencyService.delete(agency);
                loadAgenciesFromDatabase();
            }
        });
    }

    private void openAddAgencyForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/AddRentalAgency.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une Agence");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadAgenciesFromDatabase(); // Refresh
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleExportExcel() {
        // À implémenter
    }
}
