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
import org.Esprit.TripNShip.Entities.Ticket;
import org.Esprit.TripNShip.Services.TicketService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class TicketController {
    @FXML private Button addTicketButton;
    @FXML private TableView<Ticket> ticketTable;
    @FXML private TableColumn<String, Ticket> itineraryCodeColumn;
    @FXML private TableColumn<String, Ticket> userEmailColumn;
    @FXML private TableColumn<LocalDate, Ticket> departureDateColumn;
    @FXML private TableColumn<LocalDate, Ticket> arrivalDateColumn;
    @FXML private TableColumn<Double, Ticket> priceColumn;
    @FXML private TableColumn<Ticket, Void> actionsColumn;
    @FXML private TextField searchField;


    private ObservableList<Ticket> ticketList = FXCollections.observableArrayList();
    TicketService ts = new TicketService();

    @FXML
    public void initialize() {
        // Lier les colonnes aux attributs
        itineraryCodeColumn.setCellValueFactory(new PropertyValueFactory<>("itineraryCode"));
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
        departureDateColumn.setCellValueFactory(new PropertyValueFactory<>("departureDate"));
        arrivalDateColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalDate"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        ticketList.addAll(ts.getAll());

        ticketTable.setItems(ticketList);

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
                    Ticket ticket = getTableView().getItems().get(getIndex());
                    handleEdit(ticket);
                });

                deleteButton.setOnAction(event -> {
                    Ticket ticket = getTableView().getItems().get(getIndex());
                    handleDelete(ticket);
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

    public void toAddTicket(ActionEvent event) throws IOException {
        {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/addTicket.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Add Ticket");
            stage.setScene(new Scene(root));
            stage.show();
        }
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



}



