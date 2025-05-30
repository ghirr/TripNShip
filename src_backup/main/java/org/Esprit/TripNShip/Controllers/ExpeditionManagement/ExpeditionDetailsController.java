package org.Esprit.TripNShip.Controllers.ExpeditionManagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import org.Esprit.TripNShip.Entities.Expedition;
import org.Esprit.TripNShip.Entities.PackageStatus;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class ExpeditionDetailsController implements Initializable {

    @FXML
    private Label idLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label typeLabel;

    @FXML
    private Label weightLabel;

    @FXML
    private Label costLabel;

    @FXML
    private Label departureCityLabel;

    @FXML
    private Label arrivalCityLabel;

    @FXML
    private Label currentLocationLabel;

    @FXML
    private Label sendDateLabel;

    @FXML
    private Label estimatedDeliveryLabel;

    @FXML
    private Label transporterNameLabel;

    @FXML
    private Label transporterContactLabel;

    @FXML
    private Button trackHistoryBtn;

    @FXML
    private Button closeBtn;

    private Expedition expedition;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set button handlers
        trackHistoryBtn.setOnAction(event -> openTrackingHistory());
        closeBtn.setOnAction(event -> ((Stage) closeBtn.getScene().getWindow()).close());
    }

    public void setExpedition(Expedition expedition) {
        this.expedition = expedition;
        displayExpeditionDetails();
    }

    private void displayExpeditionDetails() {
        if (expedition == null) {
            return;
        }

        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // Set values to labels
        idLabel.setText(String.valueOf(expedition.getExpeditionId()));

        // Set status with appropriate style class
        statusLabel.setText(expedition.getPackageStatus().toString());
        statusLabel.getStyleClass().add(getStatusStyleClass(expedition.getPackageStatus()));

        typeLabel.setText(expedition.getPackageType().toString());
        weightLabel.setText(String.format("%.2f kg", expedition.getWeight()));
        costLabel.setText(String.format("$%.2f", expedition.getShippingCost()));
        departureCityLabel.setText(expedition.getDepartureCity());
        arrivalCityLabel.setText(expedition.getArrivalCity());
        currentLocationLabel.setText(expedition.getCurrentLocation());
        sendDateLabel.setText(dateFormat.format(expedition.getSendDate()));
        estimatedDeliveryLabel.setText(dateFormat.format(expedition.getEstimatedDeliveryDate()));

        // Set transporter details
        if (expedition.getTransporter() != null) {
            transporterNameLabel.setText(expedition.getTransporter().getFirstName() + " " +
                    expedition.getTransporter().getLastName());

            transporterContactLabel.setText(expedition.getTransporter().getEmail() + " | " +
                    expedition.getTransporter().getPhoneNumber());
        } else {
            transporterNameLabel.setText("Not assigned");
            transporterContactLabel.setText("N/A");
        }
    }

    private String getStatusStyleClass(PackageStatus status) {
        switch (status) {
            case PENDING:
                return "status-pending";
            case IN_TRANSIT:
                return "status-in-transit";
            case DELIVERED:
                return "status-delivered";
            case CANCELLED:
                return "status-cancelled";
            default:
                return "status-pending";
        }
    }

    private void openTrackingHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExpeditionManagement/TrackingHistory.fxml"));
            Parent root = loader.load();

            TrackingHistoryController controller = loader.getController();
            controller.setExpedition(expedition);

            Stage stage = new Stage();
            stage.setTitle("Tracking History");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open tracking history: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
