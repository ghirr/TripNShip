package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Itinerary;
import org.Esprit.TripNShip.Services.ItineraryService;

public class UpdateItineraryController {
    @FXML
    private TextField itineraryIdField;
    @FXML
    private TextField transportIdField;
    @FXML
    private TextField departureLocationField;
    @FXML
    private TextField arrivalLocationField;
    @FXML
    private TextField durationField;

    private final ItineraryService is = new ItineraryService();
    private Itinerary itinerary;

    public void setItinerary(Itinerary itinerary) {
        this.itinerary = itinerary;
        itineraryIdField.setText(String.valueOf(itinerary.getItineraryId()));
        transportIdField.setText(itinerary.getTransportId());
        departureLocationField.setText(itinerary.getDepartureLocation());
        arrivalLocationField.setText(itinerary.getArrivalLocation());
        durationField.setText(itinerary.getDuration());
    }

    @FXML
    private void saveItinerary() {
        itinerary.setTransportId(transportIdField.getText());
        itinerary.setDepartureLocation((departureLocationField.getText()));
        itinerary.setArrivalLocation(arrivalLocationField.getText());
        itinerary.setDuration((durationField.getText()));

        is.update(itinerary);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Update");
        alert.setContentText("Itinerary updated successfully!");
        alert.showAndWait();

        // Ferme la fenêtre
        Stage stage = (Stage) itineraryIdField.getScene().getWindow();
        stage.close();
    }
}