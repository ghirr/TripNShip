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
import org.Esprit.TripNShip.Entities.Transport;
import org.Esprit.TripNShip.Entities.TransportType;
import org.Esprit.TripNShip.Services.TransportService;

import java.io.IOException;


public class TransportController {

    @FXML private Button addTicketButton;
    @FXML private TableView<Transport> transportTable;
    @FXML private TableColumn<String, Transport> transportIdColumn;
    @FXML private TableColumn<TransportType, Transport> transportationColumn;
    @FXML private TableColumn<String, Transport> companyNameColumn;
    @FXML private TableColumn<Integer, Transport> companyPhoneColumn;
    @FXML private TableColumn<String, Transport> companyWebsiteColumn;
    @FXML private TableColumn<String, Transport> companyEmailColumn;
    @FXML private TableColumn<Transport, Void> actionsColumn;


    private ObservableList<Transport> transportList = FXCollections.observableArrayList();
    TransportService ts = new TransportService();

    @FXML
    public void initialize() {
        // Lier les colonnes aux attributs
        transportIdColumn.setCellValueFactory(new PropertyValueFactory<>("transportId"));
        transportationColumn.setCellValueFactory(new PropertyValueFactory<>("transportation"));
        companyNameColumn.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        companyPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("companyPhone"));
        companyWebsiteColumn.setCellValueFactory(new PropertyValueFactory<>("companyWebsite"));
        companyEmailColumn.setCellValueFactory(new PropertyValueFactory<>("companyEmail"));

        // Charger tous les tickets depuis le service
        transportList.addAll(ts.getAll());

        // Afficher dans la TableView
       transportTable.setItems(transportList);

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
                    Transport transport = getTableView().getItems().get(getIndex());
                    handleEdit(transport);
                });

                deleteButton.setOnAction(event -> {
                    Transport transport = getTableView().getItems().get(getIndex());
                    handleDelete(transport);
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
    public void toAddTransport(ActionEvent event) throws IOException {
        {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/addTransport.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Add Transport");
            stage.setScene(new Scene(root));
            stage.show();
        }
    }
    private void handleDelete(Transport transport) {
        ts.delete(transport); // appelle ta vraie méthode
        transportTable.getItems().remove(transport); // met à jour la table
    }



    private void handleEdit(Transport transport) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/updateTransport.fxml"));
            Parent root = loader.load();

            UpdateTransportController controller = loader.getController();
            controller.setTransport(transport);

            Stage stage = new Stage();
            stage.setTitle("update transport");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            transportTable.refresh(); // Actualise la table après fermeture
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

