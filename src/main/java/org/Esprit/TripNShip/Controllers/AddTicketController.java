package org.Esprit.TripNShip.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Itinerary;
import org.Esprit.TripNShip.Entities.Ticket;
import org.Esprit.TripNShip.Services.ItineraryService;
import org.Esprit.TripNShip.Services.TicketService;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class AddTicketController implements Initializable {

    @FXML private ComboBox<String> itineraryCodeComboBox;
    @FXML private TextField userEmailField;
    @FXML private DatePicker departureDatePicker;
    @FXML private TextField departureTimeField;
    @FXML private TextField arrivalDateField;
    @FXML private TextField arrivalTimeField;

    @FXML private Button addTicketButton;
    private Runnable onTicketAdded;

    public void setOnTicketAdded(Runnable onTicketAdded) {
        this.onTicketAdded = onTicketAdded;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ItineraryService is = new ItineraryService();
        List<Itinerary> itineraries = is.getAll();
        for (Itinerary i : itineraries) {
            itineraryCodeComboBox.getItems().add(i.getItineraryCode());
        }
        itineraryCodeComboBox.setOnAction(event -> onItineraryCodeSelected());
        departureDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateArrivalDate());
        departureTimeField.setEditable(false);
        arrivalTimeField.setEditable(false);
        arrivalDateField.setEditable(false);
    }

    public void addTicket(ActionEvent event) throws IOException {
        if(!validInputs()) return;
        TicketService ts = new TicketService();
        ts.add(new Ticket(itineraryCodeComboBox.getValue(),userEmailField.getText(), departureDatePicker.getValue(), LocalDate.parse(arrivalDateField.getText())));

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("SA7it");
        alert.setContentText("Ticket Added !");
        alert.show();
        Stage stage = (Stage) itineraryCodeComboBox.getScene().getWindow();
        if (onTicketAdded != null) {
            onTicketAdded.run(); // Rafraîchir la table dans TicketController
        }
        stage.close();
    }
    public boolean validInputs() {
        if (itineraryCodeComboBox.getValue() == null ||  userEmailField.getText().isEmpty() || departureDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please Fill in all required fields");
            return false;

        }

        if (!userEmailField.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
            showAlert(Alert.AlertType.ERROR,"Email Error","Please give a valid email address");
            return false;
        }
        try {
            departureDatePicker.getValue();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,"Date Format Error","Please set the dates in the format YYYY-MM-DD");
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


    @FXML
    private void onItineraryCodeSelected() {
        String selectedCode = itineraryCodeComboBox.getValue();
        if (selectedCode != null) {
            ItineraryService is = new ItineraryService();
            Itinerary itinerary = is.getItineraryByCode(selectedCode);
            if (itinerary != null) {
                departureTimeField.setText(itinerary.getDepartureTime().toString());
                arrivalTimeField.setText(itinerary.getArrivalTime().toString());
                updateArrivalDate();
            }
        }
    }
    private void updateArrivalDate() {
        String selectedCode = itineraryCodeComboBox.getValue();
        LocalDate departureDate = departureDatePicker.getValue();

        if (selectedCode == null || departureDate == null) {
            return;
        }

        ItineraryService is = new ItineraryService();
        Itinerary itinerary = is.getItineraryByCode(selectedCode);
        if (itinerary != null && itinerary.getDepartureTime() != null && itinerary.getDuration() != null) {
            LocalDate calculatedArrivalDate = Ticket.calculateArrivalDate(departureDate, itinerary.getDepartureTime(), itinerary.getDuration());
            if (calculatedArrivalDate != null) {
                arrivalDateField.setText(calculatedArrivalDate.toString());
            }
        }
    }


}
