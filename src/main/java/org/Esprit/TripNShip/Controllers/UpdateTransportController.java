package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Transport;
import org.Esprit.TripNShip.Entities.TransportType;
import org.Esprit.TripNShip.Services.TransportService;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateTransportController implements Initializable {
    @FXML private TextField transportIdField;
    @FXML private TextField transporterReferenceField;
    @FXML private TextField companyNameField;
    @FXML private TextField companyPhoneField;
    @FXML private TextField companyEmailField;
    @FXML private TextField companyWebsiteField;
    @FXML private Button updateTransportButton;
    @FXML
    ComboBox<TransportType> transportationComboBox;

    public void initialize(URL location, ResourceBundle resources) {
        transportationComboBox.setItems(FXCollections.observableArrayList(TransportType.values()));

    }

    private final TransportService ts = new TransportService();
    private Transport transport;

    public void setTransport(Transport transport) {
        this.transport = transport;
        transportIdField.setText(Integer.toString(transport.getTransportId()));
        transporterReferenceField.setText(transport.getTransporterReference());
        transportationComboBox.setValue(transport.getTransportation());
        companyNameField.setText(transport.getCompanyName());
        companyPhoneField.setText(Integer.toString(transport.getCompanyPhone()));
        companyEmailField.setText(transport.getCompanyEmail());
        companyWebsiteField.setText(transport.getCompanyWebsite());
    }

    @FXML
    private void saveTransport() {
        if  (validInputs()==false) return;

        transport.setTransportId(Integer.parseInt(transportIdField.getText()));
        transport.setTransporterReference(transporterReferenceField.getText());
        transport.setTransportation(transportationComboBox.getValue());
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
    public boolean validInputs() {
     try {
         if (transporterReferenceField.getText().isEmpty() || transportIdField.getText().isEmpty() || transportationComboBox.getValue() == null || companyNameField.getText().isEmpty() || companyEmailField.getText().isEmpty()) {
             showAlert(Alert.AlertType.ERROR, "Validation Error", "Please Fill in all required fields");
             return false;
         }
     }
        catch(NullPointerException e){
             showAlert(Alert.AlertType.ERROR, "Error", "Some fields are empty");
             return false;
         }

        if (!companyEmailField.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
            showAlert(Alert.AlertType.ERROR,"Email Error","Please give a valid email address");
            return false;
        }
        if (!companyPhoneField.getText().matches("\\d{6,10}")){
            showAlert(Alert.AlertType.ERROR,"Phone number Error","please give a valid phone number : between 6 and 10 numbers ");
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
