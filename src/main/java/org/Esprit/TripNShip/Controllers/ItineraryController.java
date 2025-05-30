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
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Itinerary;
import org.Esprit.TripNShip.Services.ItineraryService;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Optional;

public class ItineraryController {

    @FXML
    private TableView<Itinerary> itineraryTable;
    @FXML private TableColumn<String, Itinerary> itineraryCodeColumn;
    @FXML private TableColumn<String, Itinerary> transporterReferenceColumn;
    @FXML private TableColumn<String, Itinerary> departureLocationColumn;
    @FXML private TableColumn<LocalTime,Itinerary> departureTimeColumn;
    @FXML private TableColumn<String, Itinerary> arrivalLocationColumn;
    @FXML private TableColumn<LocalTime,Itinerary> arrivalTimeColumn;
    @FXML private TableColumn<String, Itinerary> durationColumn;
    @FXML private TableColumn<Double, Itinerary> priceColumn;
    @FXML private TableColumn<Itinerary, Void> actionsColumn;
    private ObservableList<Itinerary> itineraryList = FXCollections.observableArrayList();
    @FXML Button addItineraryButton;
    @FXML TextField searchField;
    ItineraryService is = new ItineraryService();

    @FXML
    public void initialize() {
        // Lier les colonnes aux attributs
        itineraryCodeColumn.setCellValueFactory(new PropertyValueFactory<>("itineraryCode"));
        transporterReferenceColumn.setCellValueFactory(new PropertyValueFactory<>("transporterReference"));
        departureLocationColumn.setCellValueFactory(new PropertyValueFactory<>("departureLocation"));
        departureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        arrivalLocationColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalLocation"));
        arrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Charger tous les tickets depuis le service dans l'observableList
        itineraryList.addAll(is.getAll());

        // Afficher dans la TableView
        itineraryTable.setItems(itineraryList);

        addActionsToTable();
        setupSearch();

    }

    private void addActionsToTable() {
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonBox = new HBox(5, editButton, deleteButton);

            {
                editButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 5;");
                deleteButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 5;");

                editButton.setOnAction(event -> {
                    Itinerary itinerary = getTableView().getItems().get(getIndex());
                    handleEdit(itinerary);
                });

                deleteButton.setOnAction(event -> {
                    Itinerary itinerary = getTableView().getItems().get(getIndex());
                    handleDelete(itinerary);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
            }
        });
    }
    public void toAddItinerary(ActionEvent event) throws IOException {
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addItinerary.fxml"));
            Parent root = loader.load();

            AddItineraryController controller = loader.getController();

            controller.setOnItineraryAdded(() -> refreshItineraryTable());
            Stage stage = new Stage();
            stage.setTitle("Add Itinerary");
            stage.setScene(new Scene(root));
            stage.show();
        }
    }
    private void handleDelete(Itinerary itinerary) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this itinerary");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            is.delete(itinerary);
            itineraryTable.getItems().remove(itinerary);
        }

    }

    private void handleEdit(Itinerary itinerary) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/updateItinerary.fxml"));
            Parent root = loader.load();

            UpdateItineraryController controller = loader.getController();
            controller.setItinerary(itinerary);

            Stage stage = new Stage();
            stage.setTitle("Modifier l'itinéraire");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            itineraryTable.refresh(); // Actualise la table après fermeture
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<Itinerary> filteredList = FXCollections.observableArrayList();

            for (Itinerary itinerary : itineraryList) {
                if (itinerary.getTransporterReference().toLowerCase().contains(newValue.toLowerCase()) ||
                        itinerary.getDepartureLocation().toLowerCase().contains(newValue)||(itinerary.getArrivalLocation().toLowerCase().contains(newValue))) {
                    filteredList.add(itinerary);
                }
            }

            itineraryTable.setItems(filteredList);

            // Si la barre est vide, on remet toute la liste
            if (newValue.isEmpty()) {
                itineraryTable.setItems(itineraryList);
            }
            addActionsToTable(); // pour remettre les boutons actions en place
        });
    }
    public void refreshItineraryTable() {
        itineraryList.setAll(is.getAll());
        itineraryTable.setItems(itineraryList);
        addActionsToTable(); // Remettre les boutons d'action
    }
}
