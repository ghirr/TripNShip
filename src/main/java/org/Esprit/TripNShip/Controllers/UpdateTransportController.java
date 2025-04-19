package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Transport;
import org.Esprit.TripNShip.Entities.TransportType;
import org.Esprit.TripNShip.Services.TransportService;

public class UpdateTransportController {
    @FXML
    private TextField transportIdField;
    @FXML private TextField transportationField;
    @FXML private TextField companyNameField;
    @FXML private TextField companyPhoneField;
    @FXML private TextField companyEmailField;
    @FXML private TextField companyWebsiteField;
    @FXML private Button addTransportButton;


    private final TransportService ts = new TransportService();
    private Transport transport;

    public void setTransport(Transport transport) {
        this.transport = transport;
        transportIdField.setText(transport.getTransportId());
        transportationField.setText(String.valueOf(transport.getTransportation()));
        companyNameField.setText(transport.getCompanyName());
        companyPhoneField.setText(Integer.toString(transport.getCompanyPhone()));
        companyEmailField.setText(transport.getCompanyEmail());
        companyWebsiteField.setText(transport.getCompanyWebsite());
    }

    @FXML
    private void saveTransport() {
        transport.setTransportId(transportIdField.getText());
        transport.setTransportation(TransportType.valueOf((transportationField.getText())));
        transport.setCompanyName(companyNameField.getText());
        transport.setCompanyPhone(Integer.parseInt((companyPhoneField.getText())));
        transport.setCompanyEmail(companyEmailField.getText());
        transport.setCompanyWebsite(companyWebsiteField.getText());

        ts.update(transport);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Update");
        alert.setContentText("Transport updated successfully!");
        alert.showAndWait();

        // Ferme la fenêtre
        Stage stage = (Stage) transportIdField.getScene().getWindow();
        stage.close();
    }
}