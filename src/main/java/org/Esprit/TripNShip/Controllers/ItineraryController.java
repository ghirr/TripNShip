package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Itinerary;
import org.Esprit.TripNShip.Services.ItineraryService;

import java.io.IOException;

public class ItineraryController {

    @FXML
    private TableView<Itinerary> itineraryTable;
    @FXML private TableColumn<String, Itinerary> itineraryIdColumn;
    @FXML private TableColumn<String, Itinerary> transportIdColumn;
    @FXML private TableColumn<String, Itinerary> departureLocationColumn;
    @FXML private TableColumn<String, Itinerary> arrivalLocationColumn;
    @FXML private TableColumn<String, Itinerary> durationColumn;
    @FXML private TableColumn<Itinerary, Void> actionsColumn;
    private ObservableList<Itinerary> itineraryList = FXCollections.observableArrayList();
    ItineraryService is = new ItineraryService();

    @FXML
    public void initialize() {
        // Lier les colonnes aux attributs
        itineraryIdColumn.setCellValueFactory(new PropertyValueFactory<>("itineraryId"));
        transportIdColumn.setCellValueFactory(new PropertyValueFactory<>("transportId"));
        departureLocationColumn.setCellValueFactory(new PropertyValueFactory<>("departureLocation"));
        arrivalLocationColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalLocation"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        // Charger tous les tickets depuis le service
        itineraryList.addAll(is.getAll());

        // Afficher dans la TableView
        itineraryTable.setItems(itineraryList);

        addActionsToTable();

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
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/AddItinerary.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Add Itinerary");
            stage.setScene(new Scene(root));
            stage.show();
        }
    }
    private void handleDelete(Itinerary itinerary) {
        is.delete(itinerary); // appelle ta vraie méthode
        itineraryTable.getItems().remove(itinerary); // met à jour la table
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

}
