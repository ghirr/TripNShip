package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Transport;
import org.Esprit.TripNShip.Entities.TransportType;
import org.Esprit.TripNShip.Services.TransportService;

import java.io.File;
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
    @FXML private Button chooseLogoButton;
    @FXML private Label logoPathLabel;
    @FXML private Button addTransportButton;

    private String selectedLogoPath="";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        transportationComboBox.setItems(FXCollections.observableArrayList(TransportType.values()));
    }
    @FXML
    void addTransport(ActionEvent event) throws IOException{
        if (validInputs()==false) return;
        TransportService ts = new TransportService();
        ts.add(new Transport(selectedLogoPath,transporterReferenceField.getText(),transportationComboBox.getValue(),companyNameField.getText(),Integer.parseInt(companyPhoneField.getText()),companyEmailField.getText(),companyWebsiteField.getText()));

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("SA7it");
        alert.setContentText("Transport Added !");
        alert.show();
        Stage stage = (Stage) transporterReferenceField.getScene().getWindow();
        stage.close();
    }
    public boolean validInputs() {
        try {
            if (transporterReferenceField.getText().isEmpty() ||  transportationComboBox.getValue() == null || companyNameField.getText().isEmpty() || companyEmailField.getText().isEmpty()) {
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
    @FXML
    void chooseLogo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select the transporter Logo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedLogoPath = file.getAbsolutePath();
            logoPathLabel.setText(file.getName());
        }
    }

}
