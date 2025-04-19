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

    @FXML
    private void initialize() {
        // Vérifiez que la TableView est bien injectée
        if (accommodationTable == null) {
            System.out.println("accommodationTable is not initialized.");
            return;
        }
        // Lier les colonnes aux propriétés
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("priceNight"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        // Charger les données
        loadAccommodationsFromDatabase();


        // Configurer le ComboBox
        entriesComboBox.getItems().addAll("5", "10", "25", "50");
        entriesComboBox.getSelectionModel().selectFirst();

        // Configurer les boutons
        //addButton.setOnAction(event -> openAddAccommodationForm());
        exportExcelButton.setOnAction(event -> handleExportExcel());

        // Recherche dynamique
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAccommodations(newValue);
        });

        // Colonne d'actions (modifier, supprimer)
        setupActionButtons();
    }

    private void loadAccommodationsFromDatabase() {
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
        // TODO: Implémenter l’ouverture du formulaire de modification
        System.out.println("Edit accommodation: " + accommodation.getName());
    }

    private void handleExportExcel() {
        // TODO: Implémenter l'export Excel
        System.out.println("Export to Excel triggered");
    }

    @FXML
    private void openAddAccommodationForm() {
        try {
            // Charger le fichier FXML pour le formulaire d'ajout d'hébergement
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagementFXML/Accommodation.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de ce formulaire
            AccommodationController controller = loader.getController();

            // Passer une référence de tableController au formulaire d'ajout (si nécessaire)
            controller.setTableController(this);

            // Créer une nouvelle fenêtre (Stage)
            Stage stage = new Stage();
            stage.setTitle("Add");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();  // Gérer l'exception en affichant l'erreur
        }

    }




    public void addAccommodation(Accommodation accommodation) {
        accommodationList.add(accommodation);
        loadAccommodationsFromDatabase();  // Recharger les données après ajout
    }

    public void updateAccommodation() {
        loadAccommodationsFromDatabase();
    }
        @FXML
        private void handleAddButton (ActionEvent event){
            try {
                // Charge le fichier Accommodation.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagementFXML/Accommodation.fxml"));
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

