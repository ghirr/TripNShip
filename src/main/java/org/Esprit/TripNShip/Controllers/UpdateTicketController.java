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
    private TextField itineraryCodeField;
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
        itineraryCodeField.setText(ticket.getItineraryCode());
        userEmailField.setText(String.valueOf(ticket.getUserEmail()));
        departureDateField.setText(ticket.getDepartureDate().toString());
        arrivalDateField.setText(ticket.getArrivalDate().toString());
        priceField.setText(Double.toString((ticket.getPrice())));
    }

    @FXML
    private void saveTicket() {
        if (!validInputs()) return;

        ticket.setTicketId(Integer.parseInt(ticketIdField.getText()));
        ticket.setItineraryCode(itineraryCodeField.getText());
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
    public boolean validInputs() {
        if (itineraryCodeField.getText().isEmpty() ||  userEmailField.getText().isEmpty() || departureDateField.getText().isEmpty() || arrivalDateField.getText().isEmpty()||priceField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please Fill in all required fields");
            return false;

        }

        if (!userEmailField.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
            showAlert(Alert.AlertType.ERROR,"Email Error","Please give a valid email address");
            return false;
        }
        try {
            LocalDate.parse(departureDateField.getText());
            LocalDate.parse(arrivalDateField.getText());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,"Date Format Error","Please set the dates in the format YYYY-MM-DD");
            return false;
        }
        try{
            Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR,"Price Format Error","Please set the price as Double");
        }

        return true;
    }

    public void showAlert(Alert.AlertType alertType, String title, String message){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

