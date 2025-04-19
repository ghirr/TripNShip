package org.Esprit.TripNShip.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Transport;
import org.Esprit.TripNShip.Entities.TransportType;
import org.Esprit.TripNShip.Services.TransportService;

import java.io.IOException;

public class AddTransportController {
    @FXML
    private TextField transportIdField;
    @FXML private TextField transportationField;
    @FXML private TextField companyNameField;
    @FXML private TextField companyPhoneField;
    @FXML private TextField companyEmailField;
    @FXML private TextField companyWebsiteField;
    @FXML private Button addTransportButton;

    @FXML
    void addTransport(ActionEvent event) throws IOException{
        TransportService ts = new TransportService();
        ts.add(new Transport(transportIdField.getText(),TransportType.valueOf(transportationField.getText()),companyNameField.getText(),Integer.parseInt(companyPhoneField.getText()),companyEmailField.getText(),companyWebsiteField.getText()));

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("SA7it");
        alert.setContentText("Transport Added !");
        alert.show();
        Stage stage = (Stage) transportIdField.getScene().getWindow();
        stage.close();
    }

}
