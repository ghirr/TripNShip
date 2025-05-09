package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.TypeAccommodation;
import org.Esprit.TripNShip.Services.AccommodationService;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.util.List;



public class TableAccommodationController {

    @FXML private ComboBox<String> entriesComboBox;
    @FXML private Region spacer;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button exportExcelButton;
    @FXML private TableView<Accommodation> accommodationTable;
    @FXML private TableColumn<Accommodation, TypeAccommodation> typeColumn;

    @FXML private TableColumn<Accommodation, String> nameColumn;
    @FXML private TableColumn<Accommodation, String> addressColumn;
    @FXML private TableColumn<Accommodation, Float> priceColumn;
    @FXML private TableColumn<Accommodation, Integer> capacityColumn;
    @FXML private TableColumn<Accommodation, Void> actionColumn;

    private final AccommodationService accommodationService = new AccommodationService();
    private ObservableList<Accommodation> accommodationList = FXCollections.observableArrayList();
    private static TableAccommodationController instance;

    public TableAccommodationController(){
        instance = this;
    }
    public static TableAccommodationController getInstance() {
        return instance;
    }

    @FXML
    private void initialize() {
        // Lier les colonnes aux propriétés
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("priceNight"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        // Charger les données
        loadAccommodationsFromDatabase();

        // Configurer le ComboBox
        entriesComboBox.getItems().addAll("5", "10", "25", "50");
        entriesComboBox.getSelectionModel().selectFirst();

        // Configurer les boutons
        addButton.setOnAction(event -> openAddAccommodationForm());
        exportExcelButton.setOnAction(event -> handleExportExcel());

        // Recherche dynamique
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAccommodations(newValue);
        });

        // Colonne d'actions (modifier, supprimer)
        setupActionButtons();
    }

    public void loadAccommodationsFromDatabase() {
        List<Accommodation> accommodations = accommodationService.getAll();
        accommodationList.setAll(accommodations);
        accommodationTable.setItems(accommodationList);
    }

    private void filterAccommodations(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            accommodationTable.setItems(accommodationList);
        } else {
            ObservableList<Accommodation> filteredList = FXCollections.observableArrayList();
            for (Accommodation accommodation : accommodationList) {
                if (accommodation.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                        accommodation.getAddress().toLowerCase().contains(searchText.toLowerCase()) ||
                        accommodation.getType().name().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(accommodation);
                }
            }
            accommodationTable.setItems(filteredList);
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
                    Accommodation accommodation = getTableView().getItems().get(getIndex());
                    handleEditAccommodation(accommodation);
                });

                deleteButton.setOnAction(event -> {
                    Accommodation accommodation = getTableView().getItems().get(getIndex());
                    accommodationService.delete(accommodation);
                    loadAccommodationsFromDatabase();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonsContainer);
            }
        });
    }

    private void handleEditAccommodation(Accommodation accommodation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagementFXML/EditAccommodation.fxml"));
            Parent root = loader.load();

            // Récupère le contrôleur
            EditAccommodationController controller = loader.getController();
            // Passe les données à modifier
            controller.setAccommodation(accommodation); // à créer dans AccommodationController
            // Recharge la table après modification
            loadAccommodationsFromDatabase();


            Stage stage = new Stage();
            stage.setTitle("Edit Accommodation");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors de l'ouverture du formulaire de modification : " + e.getMessage());
        }


    }

    private void handleExportExcel() {
        // TODO: Implémenter l'export Excel
        System.out.println("Export to Excel triggered");
    }

    private void openAddAccommodationForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagementFXML/Accommodation.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Accommodation");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("⚠️ Erreur lors du chargement de Accommodation.fxml : " + e.getMessage());
        }
    }



    @FXML
    private void handleAddButton(ActionEvent event) {
        System.out.println("Add button clicked!");
        try {
            // Charge le fichier Accommodation.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagement/Accommodation.fxml"));
            Parent root = loader.load();

            // Création de la nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Add Accommodation");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Ouvre la fenêtre en modal

        } catch (IOException e) {
            e.printStackTrace(); // Pour voir les erreurs en console
        }
    }
}

