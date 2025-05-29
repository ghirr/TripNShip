package org.Esprit.TripNShip.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Itinerary;
import org.Esprit.TripNShip.Services.ItineraryService;
import org.Esprit.TripNShip.Utils.GeoUtils;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class AddItineraryController {
    @FXML
    private TextField itineraryCodeField;
    @FXML private TextField transporterReferenceField;
    @FXML private TextField departureLocationField;
    @FXML private TextField departureTimeField;
    @FXML private TextField arrivalLocationField;
    @FXML private TextField durationField;
    @FXML private TextField priceField;

    @FXML private Button addItineraryButton;

    public void addItinerary(ActionEvent event) throws IOException {
        if (!validInputs()) return;
        ItineraryService is = new ItineraryService();
        is.add(new Itinerary(itineraryCodeField.getText(), transporterReferenceField.getText(),departureLocationField.getText(), LocalTime.parse(departureTimeField.getText()), arrivalLocationField.getText(),durationField.getText(),Double.parseDouble(priceField.getText())));

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Add Itinerary");
        alert.setContentText("itinerary Added !");
        alert.show();
        Stage stage = (Stage) itineraryCodeField.getScene().getWindow();
        stage.close();
    }

    public boolean validInputs() {
                    if (itineraryCodeField.getText().isEmpty() ||  transporterReferenceField.getText().isEmpty() || departureLocationField.getText().isEmpty() || arrivalLocationField.getText().isEmpty()||departureTimeField.getText().isEmpty()||durationField.getText().isEmpty()||priceField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please Fill in all required fields");
                return false;

        }

        if (!durationField.getText().matches("^([01]?\\d|2[0-3]):[0-5]\\d$")) {
            showAlert(Alert.AlertType.ERROR, "Duration Error", "Please enter a valid duration in HH:MM format (00:00 to 23:59)");
            return false;
        }
        try{
            Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR,"Price Format Error","Please set the price as Double");
            return false;
        }
        try {
            LocalTime.parse(departureTimeField.getText());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Time Format Error", "Please set the time in the format HH:MM");
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
