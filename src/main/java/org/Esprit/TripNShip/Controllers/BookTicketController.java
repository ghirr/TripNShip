package org.Esprit.TripNShip.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Itinerary;
import org.Esprit.TripNShip.Entities.Ticket;
import org.Esprit.TripNShip.Services.ItineraryService;
import org.Esprit.TripNShip.Services.TicketService;
import org.Esprit.TripNShip.Utils.UserSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class BookTicketController {

    @FXML private ComboBox<String> itineraryCodeComboBox;
    @FXML private TextField userEmailField;
    @FXML private DatePicker departureDatePicker;
    @FXML private TextField departureLocationField;
    @FXML private TextField arrivalLocationField;
    @FXML private TextField departureTimeField;
    @FXML private TextField arrivalDateField;
    @FXML private TextField arrivalTimeField;
    @FXML private Button addTicketButton;

    private Runnable onTicketAdded;
    private Itinerary selectedItinerary;

    public void setOnTicketAdded(Runnable onTicketAdded) {
        this.onTicketAdded = onTicketAdded;
    }

    public void setItinerary(Itinerary itinerary) {
        this.selectedItinerary = itinerary;
        if (itinerary != null) {
            itineraryCodeComboBox.setValue(itinerary.getItineraryCode());
            departureLocationField.setText(itinerary.getDepartureLocation());
            arrivalLocationField.setText(itinerary.getArrivalLocation());
            departureTimeField.setText(itinerary.getDepartureTime().toString());
            arrivalTimeField.setText(itinerary.getArrivalTime().toString());
            updateArrivalDate();
        }
    }

    @FXML
    public void initialize() {
        ItineraryService is = new ItineraryService();
        List<Itinerary> itineraries = is.getAll();
        for (Itinerary i : itineraries) {
            itineraryCodeComboBox.getItems().add(i.getItineraryCode());
        }

        itineraryCodeComboBox.setOnAction(event -> onItineraryCodeSelected());
        departureDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateArrivalDate());

        UserSession currentUser = UserSession.getInstance();
        if (currentUser != null) {
            userEmailField.setText(currentUser.getUserEmail());
            userEmailField.setEditable(false); // facultatif : rendre le champ non modifiable
        }


        // Make fields non-editable
        itineraryCodeComboBox.setDisable(true);
        departureLocationField.setEditable(false);
        arrivalLocationField.setEditable(false);
        departureTimeField.setEditable(false);
        arrivalTimeField.setEditable(false);
        arrivalDateField.setEditable(false);



    }

    public void addTicket(ActionEvent event) throws IOException {
        if (!validInputs()) return;

        TicketService ts = new TicketService();
        ts.add(new Ticket(
                itineraryCodeComboBox.getValue(),
                userEmailField.getText(),
                departureDatePicker.getValue(),
                LocalDate.parse(arrivalDateField.getText()))
        );

        showAlert(Alert.AlertType.CONFIRMATION, "Success", "Ticket added successfully!");
        Stage stage = (Stage) itineraryCodeComboBox.getScene().getWindow();
        if (onTicketAdded != null) {
            onTicketAdded.run(); // Refresh the table in the main controller
        }
        stage.close();
    }

    private boolean validInputs() {
        if (itineraryCodeComboBox.getValue() == null || userEmailField.getText().isEmpty() || departureDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all required fields.");
            return false;
        }

        if (!userEmailField.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email", "Please enter a valid email address.");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
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
                departureLocationField.setText(itinerary.getDepartureLocation());
                arrivalLocationField.setText(itinerary.getArrivalLocation());
                updateArrivalDate();
            }
        }
    }

    private void updateArrivalDate() {
        String selectedCode = itineraryCodeComboBox.getValue();
        LocalDate departureDate = departureDatePicker.getValue();
        if (selectedCode == null || departureDate == null) return;

        ItineraryService is = new ItineraryService();
        Itinerary itinerary = is.getItineraryByCode(selectedCode);
        if (itinerary != null && itinerary.getDepartureTime() != null && itinerary.getDuration() != null) {
            LocalDate arrivalDate = Ticket.calculateArrivalDate(
                    departureDate, itinerary.getDepartureTime(), itinerary.getDuration()
            );
            if (arrivalDate != null) {
                arrivalDateField.setText(arrivalDate.toString());
            }
        }
    }
}
