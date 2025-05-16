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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Itinerary;
import org.Esprit.TripNShip.Entities.Transport;
import org.Esprit.TripNShip.Entities.TransportType;
import org.Esprit.TripNShip.Services.TransportService;

import java.io.IOException;
import java.util.Optional;


public class TransportController {

    @FXML private Button addTransportButton;
    @FXML private TableView<Transport> transportTable;
    @FXML private TableColumn<Transport, String> logoColumn;
    @FXML private TableColumn<Transport, String> transporterReferenceColumn;
    @FXML private TableColumn<Transport, String> transportationColumn;
    @FXML private TableColumn<Transport, String> companyNameColumn;
    @FXML private TableColumn<Integer, Transport> companyPhoneColumn;
    @FXML private TableColumn<String, Transport> companyWebsiteColumn;
    @FXML private TableColumn<String, Transport> companyEmailColumn;
    @FXML private TableColumn<Transport, Void> actionsColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<TransportType> transportationFilterComboBox;


    private ObservableList<Transport> transportList = FXCollections.observableArrayList();
    TransportService ts = new TransportService();

    @FXML
    public void initialize() {
        // Lier les colonnes aux attributs
        logoColumn.setCellValueFactory(new PropertyValueFactory<>("logoPath"));
        logoColumn.setCellFactory(column->new TableCell<>(){
           private final ImageView imageView = new ImageView();
            {
                imageView.setFitHeight(40);
                imageView.setFitWidth(40);
                imageView.setPreserveRatio(true);
            }
            @Override
            protected void updateItem(String path, boolean empty){
                super.updateItem(path,empty);
                if(empty || path==null||path.isEmpty()){
                    setGraphic(null);
                }
                else{
                    try{
                        imageView.setImage(new Image("file:" + path));
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
                });
        transporterReferenceColumn.setCellValueFactory(new PropertyValueFactory<>("transporterReference"));
        transportationColumn.setCellValueFactory(new PropertyValueFactory<>("transportation"));
        companyNameColumn.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        companyPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("companyPhone"));
        companyWebsiteColumn.setCellValueFactory(new PropertyValueFactory<>("companyWebsite"));
        companyEmailColumn.setCellValueFactory(new PropertyValueFactory<>("companyEmail"));

        transportList.addAll(ts.getAll());

       transportTable.setItems(transportList);

        addActionsToTable();
        setupSearch();

        //pour le combobox : filtre par transportation type
        transportationFilterComboBox.getItems().add(null); // Option "Tous"
        transportationFilterComboBox.getItems().addAll(TransportType.values());
        transportationFilterComboBox.setOnAction(event -> filterTransportList());
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this transport");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ts.delete(transport);
            transportTable.getItems().remove(transport);
        }

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
    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<Transport> filteredList = FXCollections.observableArrayList();

            for (Transport transport : transportList) {
                if (transport.getCompanyName().toLowerCase().contains(newValue.toLowerCase())) {
                    filteredList.add(transport);
                }
            }

            transportTable.setItems(filteredList);

            // Si la barre est vide, on remet toute la liste
            if (newValue.isEmpty()) {
                transportTable.setItems(transportList);
            }
            addActionsToTable(); // pour remettre les boutons actions en place
        });
    }
    private void filterTransportList() {
        TransportType selectedType = transportationFilterComboBox.getValue();
        String searchKeyword = searchField.getText().toLowerCase();

        ObservableList<Transport> filteredList = transportList.filtered(transport -> {
            boolean matchesType = (selectedType == null || transport.getTransportation() == selectedType);
            boolean matchesCompanyName = (searchKeyword.isEmpty() || transport.getCompanyName().toLowerCase().contains(searchKeyword));
            return matchesType && matchesCompanyName;
        });

        transportTable.setItems(filteredList);
        addActionsToTable();
    }


}

