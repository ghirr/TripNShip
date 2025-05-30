package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.Esprit.TripNShip.Entities.Itinerary;
import org.Esprit.TripNShip.Services.ItineraryService;
import org.Esprit.TripNShip.Services.TransportService;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class UpdateItineraryController implements Initializable {

    @FXML private TextField itineraryCodeField;
    @FXML private ComboBox<String> transporterReferenceComboBox;
    @FXML    private TextField departureLocationField;
   // @FXML private TextField departureTimeField;
   @FXML private Spinner<LocalTime> departureTimeSpinner;
    @FXML    private TextField arrivalLocationField;
    @FXML    private TextField durationField;
    @FXML    private TextField priceField;
    @FXML    Button updateItineraryButton;

    private final ItineraryService is = new ItineraryService();
    private Itinerary itinerary;

    public void setItinerary(Itinerary itinerary) {
        this.itinerary = itinerary;
        itineraryCodeField.setText(itinerary.getItineraryCode());
        transporterReferenceComboBox.setValue(itinerary.getTransporterReference());
        departureLocationField.setText(itinerary.getDepartureLocation());
        arrivalLocationField.setText(itinerary.getArrivalLocation());
        departureTimeSpinner.getValueFactory().setValue(itinerary.getDepartureTime());
        durationField.setText(itinerary.getDuration());
        priceField.setText(Double.toString(itinerary.getPrice()));
    }

    @FXML
    private void saveItinerary() {
        if(!validInputs()) return;
        itinerary.setItineraryCode(itineraryCodeField.getText());
        itinerary.setTransporterReference(transporterReferenceComboBox.getValue());
        itinerary.setDepartureLocation((departureLocationField.getText()));
        itinerary.setDepartureTime(departureTimeSpinner.getValue());
        itinerary.setArrivalLocation(arrivalLocationField.getText());
        itinerary.setDuration((durationField.getText()));
        itinerary.setPrice(Double.parseDouble(priceField.getText()));

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
        if (itineraryCodeField.getText().isEmpty() ||transporterReferenceComboBox.getValue()==null|| departureLocationField.getText().isEmpty() || departureTimeSpinner.getValue()==null||arrivalLocationField.getText().isEmpty()||durationField.getText().isEmpty()||priceField.getText().isEmpty()) {
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
        /*try {
            LocalTime.parse(departureTimeField.getText());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,"Time Format Error","Please set the time in the format HH:MM");
            return false;
        }*/

        return true;
    }

    public void showAlert(Alert.AlertType alertType, String title, String message){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SpinnerValueFactory<LocalTime> timeFactory = new SpinnerValueFactory<>() {
            {
                setConverter(new StringConverter<>() {
                    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

                    @Override
                    public String toString(LocalTime time) {
                        return time != null ? formatter.format(time) : "";
                    }

                    @Override
                    public LocalTime fromString(String string) {
                        return LocalTime.parse(string, formatter);
                    }
                });

                setValue(LocalTime.of(8, 0)); // Valeur par défaut
            }

            @Override
            public void decrement(int steps) {
                setValue(getValue().minusMinutes(steps * 05));
            }

            @Override
            public void increment(int steps) {
                setValue(getValue().plusMinutes(steps * 05));
            }
        };

        departureTimeSpinner.setValueFactory(timeFactory);

        TransportService transportService = new TransportService();
        List<String> references = transportService.getAllTransportReferences(); // ⇦ Crée cette méthode
        transporterReferenceComboBox.getItems().addAll(references);
    }
}
