package org.Esprit.TripNShip.Controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.TypeAccommodation;
import org.Esprit.TripNShip.Services.AccommodationService;

import java.io.IOException;
import java.util.List;

public class TableAccommodationController {

    @FXML private TableColumn<Accommodation, ImageView> photosAccommodationColumn;
    @FXML private ComboBox<String> entriesComboBox;
    @FXML private ComboBox<String> typeComboBox; // ComboBox pour filtrer par type
    @FXML private Region spacer;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private TableView<Accommodation> accommodationTable;
    @FXML private TableColumn<Accommodation, TypeAccommodation> typeColumn;
    @FXML private TableColumn<Accommodation, String> nameColumn;
    @FXML private TableColumn<Accommodation, String> addressColumn;
    @FXML private TableColumn<Accommodation, Integer> capacityColumn;
    @FXML private TableColumn<Accommodation, Void> actionColumn;

    private final AccommodationService accommodationService = new AccommodationService();
    private ObservableList<Accommodation> accommodationList = FXCollections.observableArrayList();
    private static TableAccommodationController instance;

    public TableAccommodationController() {
        instance = this;
    }

    public static TableAccommodationController getInstance() {
        return instance;
    }

    @FXML
    private void initialize() {
        // Lier les colonnes aux propriétés des entités
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        photosAccommodationColumn.setCellValueFactory(cellData -> {
            String imagePath = cellData.getValue().getPhotosAccommodation();
            Image image = new Image(imagePath);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            return new SimpleObjectProperty<>(imageView);
        });

        // Charger les données des accommodations depuis la base de données
        loadAccommodationsFromDatabase();

        // Configurer le ComboBox pour filtrer par type d'hébergement
        typeComboBox.getItems().add("All"); // Ajouter l'option "All"
        for (TypeAccommodation type : TypeAccommodation.values()) {
            typeComboBox.getItems().add(type.name()); // Ajouter le type d'hébergement
        }
        typeComboBox.getSelectionModel().selectFirst(); // Sélectionner "All" par défaut

        // Ajouter un listener pour filtrer les accommodations par type
        typeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("All".equals(newValue)) {
                loadAccommodationsFromDatabase(); // Recharger toutes les accommodations si "All"
            } else {
                filterAccommodationsByType(newValue); // Filtrer par type sélectionné
            }
        });

        // Configurer les boutons
        addButton.setOnAction(event -> openAddAccommodationForm());

        // Recherche dynamique par nom ou adresse
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAccommodations(newValue);
        });

        // Configurer la colonne d'actions (modifier, supprimer)
        setupActionButtons();
    }

    // Charger les accommodations depuis la base de données
    public void loadAccommodationsFromDatabase() {
        List<Accommodation> accommodations = accommodationService.getAll();
        accommodationList.setAll(accommodations); // Mettre à jour la liste observable
        accommodationTable.setItems(accommodationList); // Recharger la table
        accommodationTable.refresh(); // Forcer le rafraîchissement
    }

    // Filtrer les accommodations par nom, adresse ou type
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
        accommodationTable.refresh();
    }

    // Filtrer les accommodations par type
    private void filterAccommodationsByType(String type) {
        ObservableList<Accommodation> filteredList = FXCollections.observableArrayList();
        for (Accommodation accommodation : accommodationList) {
            String accommodationType = accommodation.getType().name().replaceAll(" ", "_");
            if (accommodationType.equalsIgnoreCase(type.replaceAll(" ", "_"))) {
                filteredList.add(accommodation);
            }
        }
        accommodationTable.setItems(filteredList);
        accommodationTable.refresh();
    }

    // Configurer les boutons d'édition et de suppression dans la colonne d'actions
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
                    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmationAlert.setTitle("Confirm Deletion");
                    confirmationAlert.setHeaderText("Are you sure you want to delete this accommodation?");
                    confirmationAlert.setContentText("This action cannot be undone.");
                    confirmationAlert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                accommodationService.delete(accommodation);
                                loadAccommodationsFromDatabase();
                            } catch (Exception e) {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("Error");
                                errorAlert.setHeaderText("Error Deleting Accommodation");
                                errorAlert.setContentText("An error occurred while trying to delete the accommodation: " + e.getMessage());
                                errorAlert.showAndWait();
                            }
                        }
                    });
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

            EditAccommodationController controller = loader.getController();
            controller.setAccommodation(accommodation);

            Stage stage = new Stage();
            stage.setTitle("Edit Accommodation");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadAccommodationsFromDatabase();
            accommodationTable.refresh();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors de l'ouverture du formulaire de modification : " + e.getMessage());
        }
    }

    // Méthode modifiée pour recharger la liste directement après fermeture de la fenêtre ajout
    private void openAddAccommodationForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagementFXML/Accommodation.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Accommodation");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();  // Attend la fermeture de la fenêtre modale

            // Recharge les données depuis la base
            List<Accommodation> newData = accommodationService.getAll();

            // Clear + setAll dans l'ObservableList liée
            accommodationList.clear();
            accommodationList.addAll(newData);

            // Rebind explicitement la TableView
            accommodationTable.setItems(null);
            accommodationTable.setItems(accommodationList);

            // Forcer le rafraîchissement visuel complet
            accommodationTable.refresh();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de Accommodation.fxml : " + e.getMessage());
        }
    }


    @FXML
    private void handleAddButton(ActionEvent event) {
        openAddAccommodationForm();
    }
}
