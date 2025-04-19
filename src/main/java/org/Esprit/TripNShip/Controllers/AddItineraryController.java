package org.Esprit.TripNShip.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Itinerary;
import org.Esprit.TripNShip.Services.ItineraryService;
import javafx.scene.Node;


import java.io.IOException;

public class AddItineraryController {
    @FXML
    private TextField itineraryIdField;
    @FXML private TextField transportIdField;
    @FXML private TextField departureLocationField;
    @FXML private TextField arrivalLocationField;
    @FXML private TextField durationField;
    @FXML private Button addItineraryButton;

    public void addItinerary(ActionEvent event) throws IOException {
        ItineraryService is = new ItineraryService();
        is.add(new Itinerary(itineraryIdField.getText(), transportIdField.getText(),departureLocationField.getText(), arrivalLocationField.getText(),durationField.getText()));

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("SA7it");
        alert.setContentText("itinerary Added !");
        alert.show();
        Stage stage = (Stage) itineraryIdField.getScene().getWindow();
        stage.close();
    }
    public void toMain(ActionEvent event) throws IOException {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainTransport.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
    }
}
