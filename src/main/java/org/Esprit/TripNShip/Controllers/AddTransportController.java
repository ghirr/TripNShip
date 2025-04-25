package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Transport;
import org.Esprit.TripNShip.Entities.TransportType;
import org.Esprit.TripNShip.Services.TransportService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddTransportController implements Initializable {
    @FXML private TextField transporterReferenceField;
    @FXML ComboBox<TransportType> transportationComboBox;

    @FXML private TextField companyNameField;
    @FXML private TextField companyPhoneField;
    @FXML private TextField companyEmailField;
    @FXML private TextField companyWebsiteField;
    @FXML private Button addTransportButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        transportationComboBox.setItems(FXCollections.observableArrayList(TransportType.values()));
    }
    @FXML
    void addTransport(ActionEvent event) throws IOException{
        TransportService ts = new TransportService();
        ts.add(new Transport(transporterReferenceField.getText(),transportationComboBox.getValue(),companyNameField.getText(),Integer.parseInt(companyPhoneField.getText()),companyEmailField.getText(),companyWebsiteField.getText()));

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("SA7it");
        alert.setContentText("Transport Added !");
        alert.show();
        Stage stage = (Stage) transporterReferenceField.getScene().getWindow();
        stage.close();
    }

}
