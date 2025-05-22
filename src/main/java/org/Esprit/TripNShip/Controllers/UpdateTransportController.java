package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Transport;
import org.Esprit.TripNShip.Entities.TransportType;
import org.Esprit.TripNShip.Services.TransportService;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;
import java. io. File;

public class UpdateTransportController implements Initializable {
    @FXML private TextField transporterReferenceField;
    @FXML private TextField companyNameField;
    @FXML private TextField companyPhoneField;
    @FXML private TextField companyEmailField;
    @FXML private TextField companyWebsiteField;
    @FXML private Label companyLogoPathLabel;
    @FXML private Button chooseLogoButton;
    @FXML private Button updateTransportButton;
    @FXML private ComboBox<TransportType> transportationComboBox;
    private String selectedLogoPath ;


    public void initialize(URL location, ResourceBundle resources) {
        transportationComboBox.setItems(FXCollections.observableArrayList(TransportType.values()));

    }

    private final TransportService ts = new TransportService();
    private Transport transport;

    public void setTransport(Transport transport) {
        this.transport = transport;
        transporterReferenceField.setText(transport.getTransporterReference());
        transportationComboBox.setValue(transport.getTransportation());
        companyNameField.setText(transport.getCompanyName());
        companyPhoneField.setText(Integer.toString(transport.getCompanyPhone()));
        companyEmailField.setText(transport.getCompanyEmail());
        companyWebsiteField.setText(transport.getCompanyWebsite());
        selectedLogoPath = transport.getLogoPath(); // le logo actuel
        companyLogoPathLabel.setText(selectedLogoPath);
    }

    @FXML
    private void saveTransport() {
        if  (validInputs()==false) return;

        transport.setTransporterReference(transporterReferenceField.getText());
        transport.setTransportation(transportationComboBox.getValue());
        transport.setCompanyName(companyNameField.getText());
        transport.setCompanyPhone(Integer.parseInt((companyPhoneField.getText())));
        transport.setCompanyEmail(companyEmailField.getText());
        transport.setCompanyWebsite(companyWebsiteField.getText());
        transport.setLogoPath(selectedLogoPath);

        ts.update(transport);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Update");
        alert.setContentText("Transport updated successfully!");
        alert.showAndWait();

        // Ferme la fenêtre
        Stage stage = (Stage) companyNameField.getScene().getWindow();
        stage.close();
    }
    public boolean validInputs() {
     try {
         if (transporterReferenceField.getText().isEmpty() || transportationComboBox.getValue() == null || companyNameField.getText().isEmpty() || companyEmailField.getText().isEmpty()) {
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
            try {
                String filename = file.getName();
                selectedLogoPath = file.getAbsolutePath();
                companyLogoPathLabel.setText(file.getName());
                String destDir="C:\\xampp\\htdocs\\tripnship\\images\\transporterslogos";// chemin actuel ou nouveau
                Files.copy(Path.of(selectedLogoPath), Path.of(destDir,filename), StandardCopyOption.REPLACE_EXISTING);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    }
