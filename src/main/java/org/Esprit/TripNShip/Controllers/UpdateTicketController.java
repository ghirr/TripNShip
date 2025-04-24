package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Ticket;
import org.Esprit.TripNShip.Services.TicketService;

import java.time.LocalDate;

public class UpdateTicketController {
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
    @FXML private Button updateTicketButton;

    private final TicketService ts = new TicketService();
    private Ticket ticket;

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        ticketIdField.setText(String.valueOf(ticket.getTicketId()));
        itineraryIdField.setText(ticket.getItineraryId());
        userEmailField.setText(String.valueOf(ticket.getUserEmail()));
        departureDateField.setText(ticket.getDepartureDate().toString());
        arrivalDateField.setText(ticket.getArrivalDate().toString());
        priceField.setText(Double.toString((ticket.getPrice())));
    }

    @FXML
    private void saveTicket() {
        ticket.setTicketId(Integer.parseInt(ticketIdField.getText()));
        ticket.setItineraryId(itineraryIdField.getText());
        ticket.setUserEmail(userEmailField.getText());
        ticket.setDepartureDate(LocalDate.parse(departureDateField.getText()));
        ticket.setArrivalDate(LocalDate.parse(arrivalDateField.getText()));
        ticket.setPrice(Double.parseDouble(priceField.getText()));

        ts.update(ticket);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Update");
        alert.setContentText("Ticket updated successfully!");
        alert.showAndWait();

        Stage stage = (Stage) ticketIdField.getScene().getWindow();
        stage.close();
    }
}

