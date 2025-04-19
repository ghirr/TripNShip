package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Ticket;
import org.Esprit.TripNShip.Services.TicketService;

import java.io.IOException;
import java.time.LocalDate;

public class TicketController {
    @FXML private Button addTicketButton;
    @FXML private TableView<Ticket> ticketTable;
    @FXML private TableColumn<Integer, Ticket> ticketIdColumn;
    @FXML private TableColumn<String, Ticket> itineraryIdColumn;
    @FXML private TableColumn<Integer, Ticket> userIdColumn;
    @FXML private TableColumn<LocalDate, Ticket> departureDateColumn;
    @FXML private TableColumn<LocalDate, Ticket> arrivalDateColumn;
    @FXML private TableColumn<Double, Ticket> priceColumn;

    private ObservableList<Ticket> ticketList = FXCollections.observableArrayList();
    TicketService ts = new TicketService();

    @FXML
    public void initialize() {
        // Lier les colonnes aux attributs
        ticketIdColumn.setCellValueFactory(new PropertyValueFactory<>("ticketId"));
        itineraryIdColumn.setCellValueFactory(new PropertyValueFactory<>("itineraryId"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        departureDateColumn.setCellValueFactory(new PropertyValueFactory<>("departureDate"));
        arrivalDateColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalDate"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Charger tous les tickets depuis le service
        ticketList.addAll(ts.getAll());

        // Afficher dans la TableView
        ticketTable.setItems(ticketList);
    }

    public void toAddTicket(ActionEvent event) throws IOException {
        {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/AddTicket.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Add Ticket");
            stage.setScene(new Scene(root));
            stage.show();
        }
    }
}
