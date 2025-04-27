package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Itinerary;
import org.Esprit.TripNShip.Services.ItineraryService;

public class UpdateItineraryController {

    @FXML private TextField itineraryCodeField;
    @FXML    private TextField transporterReferenceField;
    @FXML    private TextField departureLocationField;
    @FXML    private TextField arrivalLocationField;
    @FXML    private TextField durationField;
    @FXML    Button updateItineraryButton;

    private final ItineraryService is = new ItineraryService();
    private Itinerary itinerary;

    public void setItinerary(Itinerary itinerary) {
        this.itinerary = itinerary;
        itineraryCodeField.setText(itinerary.getItineraryCode());
        transporterReferenceField.setText(itinerary.getTransporterReference());
        departureLocationField.setText(itinerary.getDepartureLocation());
        arrivalLocationField.setText(itinerary.getArrivalLocation());
        durationField.setText(itinerary.getDuration());
    }

    @FXML
    private void saveItinerary() {
        if(!validInputs()) return;
        itinerary.setItineraryCode(itineraryCodeField.getText());
        itinerary.setTransporterReference(transporterReferenceField.getText());
        itinerary.setDepartureLocation((departureLocationField.getText()));
        itinerary.setArrivalLocation(arrivalLocationField.getText());
        itinerary.setDuration((durationField.getText()));

        is.update(itinerary);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Update");
        alert.setContentText("Itinerary updated successfully!");
        alert.showAndWait();

        // Ferme la fenêtre
        Stage stage = (Stage) itineraryCodeField.getScene().getWindow();
        stage.close();
    }
    public boolean validInputs() {
        if (itineraryCodeField.getText().isEmpty() ||  transporterReferenceField.getText().isEmpty() || departureLocationField.getText().isEmpty() || arrivalLocationField.getText().isEmpty()||durationField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please Fill in all required fields");
            return false;

        }

        if (!durationField.getText().matches("^([01]?\\d|2[0-3]):[0-5]\\d$")) {
            showAlert(Alert.AlertType.ERROR, "Duration Error", "Please enter a valid duration in HH:MM format (00:00 to 23:59)");
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