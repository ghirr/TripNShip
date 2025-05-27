package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Itinerary;
import org.Esprit.TripNShip.Entities.Ticket;
import org.Esprit.TripNShip.Services.ItineraryService;
import org.Esprit.TripNShip.Services.TicketService;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class UpdateTicketController implements Initializable {
    @FXML private ComboBox<String> itineraryCodeComboBox;
    @FXML private TextField userEmailField;
    @FXML private DatePicker departureDatePicker;
    @FXML private DatePicker arrivalDatePicker;
    @FXML private TextField departureTimeField;
    @FXML private TextField arrivalTimeField;
    @FXML private Button updateTicketButton;

    private final TicketService ts = new TicketService();
    private Ticket ticket;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ItineraryService is = new ItineraryService();
        List<Itinerary> itineraries = is.getAll();
        for (Itinerary itinerary : itineraries) {
            itineraryCodeComboBox.getItems().add(itinerary.getItineraryCode());
        }
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        itineraryCodeComboBox.setValue(ticket.getItineraryCode());
        userEmailField.setText(String.valueOf(ticket.getUserEmail()));
        departureDatePicker.setValue(ticket.getDepartureDate());
        arrivalDatePicker.setValue(ticket.getArrivalDate());
        departureTimeField.setText(ticket.getDepartureTime().toString());
        arrivalTimeField.setText(ticket.getArrivalTime().toString());
    }

    @FXML
    private void saveTicket() {
        if (!validInputs()) return;

        ticket.setItineraryCode(itineraryCodeComboBox.getValue());
        ticket.setUserEmail(userEmailField.getText());
        ticket.setDepartureDate(departureDatePicker.getValue());
        ticket.setArrivalDate(arrivalDatePicker.getValue());
        ticket.setDepartureTime(LocalTime.parse(departureTimeField.getText()));
        ticket.setArrivalTime(LocalTime.parse(arrivalTimeField.getText()));

        ts.update(ticket);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Update");
        alert.setContentText("Ticket updated successfully!");
        alert.showAndWait();

        Stage stage = (Stage) userEmailField.getScene().getWindow();
        stage.close();
    }
    public boolean validInputs() {
        if (itineraryCodeComboBox.getValue() == null ||  userEmailField.getText().isEmpty() || departureDatePicker.getValue() ==null || arrivalDatePicker.getValue()==null||departureTimeField.getText().isEmpty()||arrivalTimeField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please Fill in all required fields");
            return false;

        }

        if (!userEmailField.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
            showAlert(Alert.AlertType.ERROR,"Email Error","Please give a valid email address");
            return false;
        }
        try {
            departureDatePicker.getValue();
            arrivalDatePicker.getValue();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,"Date Format Error","Please set the dates in the format YYYY-MM-DD");
            return false;
        }

        try {
            LocalTime.parse(departureTimeField.getText());
            LocalTime.parse(arrivalTimeField.getText());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,"Time Format Error","Please set the time in the format HH:MM");
            return false;
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

