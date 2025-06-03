package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Itinerary;
import org.Esprit.TripNShip.Entities.Ticket;
import org.Esprit.TripNShip.Services.ItineraryService;
import org.Esprit.TripNShip.Services.TicketService;
import org.Esprit.TripNShip.Utils.TicketPDFGenerator;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class TicketController {
    @FXML private Button addTicketButton;
    @FXML private TableView<Ticket> ticketTable;
    @FXML private TableColumn<String, Ticket> itineraryCodeColumn;
    @FXML private TableColumn<String, Ticket> userEmailColumn;
    @FXML private TableColumn<LocalDate, Ticket> departureDateColumn;
    @FXML private TableColumn<LocalDate, Ticket> arrivalDateColumn;
    @FXML private TableColumn<Ticket, Void> actionsColumn;
    @FXML private TextField searchField;


    private ObservableList<Ticket> ticketList = FXCollections.observableArrayList();
    TicketService ts = new TicketService();
    ItineraryService is = new ItineraryService();

    @FXML
    public void initialize() {
        // Lier les colonnes aux attributs
        itineraryCodeColumn.setCellValueFactory(new PropertyValueFactory<>("itineraryCode"));
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
        departureDateColumn.setCellValueFactory(new PropertyValueFactory<>("departureDate"));
        arrivalDateColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalDate"));

        ticketList.addAll(ts.getAll());

        ticketTable.setItems(ticketList);

        addActionsToTable();
        setupSearch();

    }

    private void addActionsToTable() {
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final Button printButton = new Button("Print");
            private final Button viewMapButton = new Button("View");
            private final HBox buttonBox = new HBox(5, editButton, deleteButton,printButton,viewMapButton);

            {
                editButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 5;");
                deleteButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 5;");
                printButton.setStyle("-fx-background-color: #0000FF; -fx-text-fill: white; -fx-background-radius: 5;");

                editButton.setOnAction(event -> {
                    Ticket ticket = getTableView().getItems().get(getIndex());
                    handleEdit(ticket);
                });

                deleteButton.setOnAction(event -> {
                    Ticket ticket = getTableView().getItems().get(getIndex());
                    handleDelete(ticket);
                });

                printButton.setOnAction(event->{
                    Ticket ticket = getTableView().getItems().get(getIndex());
                    handlePrint(ticket);
                });

                viewMapButton.setOnAction(event -> {
                    Ticket ticket = getTableView().getItems().get(getIndex());
                    Itinerary itinerary = is.getItineraryByCode(ticket.getItineraryCode());

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mapView.fxml"));
                        Parent root = loader.load();

                        MapController controller = loader.getController();

                        // Utiliser les vraies villes depuis l'itinéraire
                        String departure = itinerary.getDepartureLocation();
                        String arrival = itinerary.getArrivalLocation();
                        controller.displayRoute(departure, arrival);

                        Stage mapStage = new Stage();
                        mapStage.setTitle("Visualisation de l'itinéraire");
                        mapStage.setScene(new Scene(root));
                        mapStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
    private String generateHTML(String coord1, String coord2) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="utf-8" />
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <link rel="stylesheet" href="https://unpkg.com/leaflet/dist/leaflet.css"/>
              <script src="https://unpkg.com/leaflet/dist/leaflet.js"></script>
            </head>
            <body>
              <div id="map" style="width: 100%; height: 100vh;"></div>
              <script>
                var map = L.map('map').setView([%s], 6);
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                  maxZoom: 19,
                  attribution: '© OpenStreetMap'
                }).addTo(map);

                var point1 = [%s];
                var point2 = [%s];

                L.marker(point1).addTo(map).bindPopup("Départ").openPopup();
                L.marker(point2).addTo(map).bindPopup("Arrivée");

                var polyline = L.polyline([point1, point2], {color: 'blue'}).addTo(map);
                map.fitBounds(polyline.getBounds());
              </script>
            </body>
            </html>
            """.formatted(coord1, coord1, coord2);
    }


    public void toAddTicket(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addTicket.fxml"));
        Parent root = loader.load();

        AddTicketController controller = loader.getController();

        // Callback pour rafraîchir la table après ajout
        controller.setOnTicketAdded(() -> refreshTicketTable());

        Stage stage = new Stage();
        stage.setTitle("Add Ticket");
        stage.setScene(new Scene(root));
        stage.show();
    }
    private void handleDelete(Ticket ticket) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this ticket");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ts.delete(ticket);
            ticketTable.getItems().remove(ticket); // pour la table
        }
    }

    private void handlePrint(Ticket ticket) {
        try {

            String filename = "tickets/ticket_" + ticket.getTicketId() + ".pdf";
            File file = new File(filename);

            file.getParentFile().mkdirs();//pour creer le dossier

            TicketPDFGenerator.generatePDF(ticket, filename);
            if (file.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("PDF Generated");
            alert.setHeaderText(null);
            alert.setContentText("Ticket PDF saved as " + filename);
            alert.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not generate PDF");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }



    private void handleEdit(Ticket ticket) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/updateTicket.fxml"));
            Parent root = loader.load();

            UpdateTicketController controller = loader.getController();
            controller.setTicket(ticket);

            Stage stage = new Stage();
            stage.setTitle("update ticket");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            ticketTable.refresh(); // Actualise la table après fermeture
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<Ticket> filteredList = FXCollections.observableArrayList();

            for (Ticket ticket : ticketList) {
                if (ticket.getUserEmail().toLowerCase().contains(newValue.toLowerCase()) ||
                        ticket.getDepartureDate().toString().contains(newValue)) {
                    filteredList.add(ticket);
                }
            }

            ticketTable.setItems(filteredList);

            // Si la barre est vide, on remet toute la liste
            if (newValue.isEmpty()) {
                ticketTable.setItems(ticketList);
            }
            addActionsToTable(); // pour remettre les boutons actions en place
        });
    }

    public void refreshTicketTable() {
        ticketList.setAll(ts.getAll());
        ticketTable.setItems(ticketList);
        addActionsToTable(); // Remettre les boutons d'action
    }

}



