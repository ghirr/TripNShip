package org.Esprit.TripNShip.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Ticket;
import org.Esprit.TripNShip.Services.TicketService;

import java.io.IOException;
import java.time.LocalDate;

public class AddTicketController {
    @FXML
    private TextField ticketIdField;
    @FXML
    private TextField itineraryIdField;
    @FXML
    private TextField userEmailField;
    @FXML
    private TextField departureDateField;
    @FXML
    private TextField arrivalDateField;
    @FXML
    private TextField priceField;
    @FXML private Button addTicketButton;


    public void addTicket(ActionEvent event) throws IOException {
        TicketService ts = new TicketService();
        ts.add(new Ticket(itineraryIdField.getText(),userEmailField.getText(), LocalDate.parse(departureDateField.getText()),LocalDate.parse(arrivalDateField.getText()),Double.parseDouble(priceField.getText())));

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("SA7it");
        alert.setContentText("Ticket Added !");
        alert.show();
        Stage stage = (Stage) ticketIdField.getScene().getWindow();
        stage.close();
    }


}
