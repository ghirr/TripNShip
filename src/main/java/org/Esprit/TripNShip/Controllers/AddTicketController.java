package org.Esprit.TripNShip.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Ticket;
import org.Esprit.TripNShip.Services.TicketService;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class AddTicketController {

    @FXML private TextField itineraryCodeField;
    @FXML private TextField userEmailField;
    @FXML private DatePicker departureDatePicker;
    @FXML private DatePicker arrivalDatePicker;
    @FXML private TextField priceField;
    @FXML private TextField departureTimeField;
    @FXML private TextField arrivalTimeField;
    @FXML private Button addTicketButton;


    public void addTicket(ActionEvent event) throws IOException {
        if(!validInputs()) return;
        TicketService ts = new TicketService();
        ts.add(new Ticket(itineraryCodeField.getText(),userEmailField.getText(), departureDatePicker.getValue(),arrivalDatePicker.getValue(), LocalTime.parse(departureTimeField.getText()),LocalTime.parse(arrivalTimeField.getText()),Double.parseDouble(priceField.getText())));

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("SA7it");
        alert.setContentText("Ticket Added !");
        alert.show();
        Stage stage = (Stage) itineraryCodeField.getScene().getWindow();
        stage.close();
    }
    public boolean validInputs() {
        if (itineraryCodeField.getText().isEmpty() ||  userEmailField.getText().isEmpty() || departureDatePicker.getValue() == null || arrivalDatePicker.getValue()==null||priceField.getText().isEmpty()) {
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
            showAlert(Alert.AlertType.ERROR, "Time Format Error", "Please set the time in the format HH:MM");
            return false;
        }
        try{
            Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR,"Price Format Error","Please set the price as Double");
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
