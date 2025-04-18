package org.Esprit.TripNShip.Controllers.ExpeditionManagement;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Services.ServiceExpedition;
import org.Esprit.TripNShip.Services.UserService;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class EditExpeditionController implements Initializable {

    @FXML
    private Label headerLabel;

    @FXML
    private Label expeditionIdLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField weightField;

    @FXML
    private Label weightErrorLabel;

    @FXML
    private ComboBox<PackageType> packageTypeCombo;

    @FXML
    private Label packageTypeErrorLabel;

    @FXML
    private TextField departureCityField;

    @FXML
    private Label departureCityErrorLabel;

    @FXML
    private TextField arrivalCityField;

    @FXML
    private Label arrivalCityErrorLabel;

    @FXML
    private DatePicker sendDatePicker;

    @FXML
    private Label sendDateErrorLabel;

    @FXML
    private ComboBox<Transporter> transporterCombo;

    @FXML
    private Label transporterErrorLabel;

    @FXML
    private Button saveBtn;

    @FXML
    private Button cancelBtn;

    private Expedition expedition;
    private ServiceExpedition expeditionService;
    private UserService userService;
    private ClientExpeditionsController parentController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        expeditionService = new ServiceExpedition();
        userService = new UserService();

        // Initialize package types
        packageTypeCombo.setItems(FXCollections.observableArrayList(PackageType.values()));

        // Load transporters
        loadTransporters();

        // Clear error labels initially
        clearErrorLabels();

        // Add listeners for real-time validation
        setupValidationListeners();

        // Set button handlers
        saveBtn.setOnAction(this::handleSaveExpedition);
        cancelBtn.setOnAction(event -> closeWindow());
    }

    private void clearErrorLabels() {
        weightErrorLabel.setText("");
        packageTypeErrorLabel.setText("");
        departureCityErrorLabel.setText("");
        arrivalCityErrorLabel.setText("");
        sendDateErrorLabel.setText("");
        transporterErrorLabel.setText("");
    }

    private void setupValidationListeners() {
        // Weight field validation
        weightField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                if (!isNumeric(newValue)) {
                    weightErrorLabel.setText("Please enter a valid number");
                } else {
                    weightErrorLabel.setText("");
                }
            } else {
                weightErrorLabel.setText("");
            }
        });

        // Package type validation
        packageTypeCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            packageTypeErrorLabel.setText(newValue == null ? "Please select a package type" : "");
        });

        // Departure city validation
        departureCityField.textProperty().addListener((observable, oldValue, newValue) -> {
            departureCityErrorLabel.setText(newValue.isEmpty() ? "Please enter a departure city" : "");
        });

        // Arrival city validation
        arrivalCityField.textProperty().addListener((observable, oldValue, newValue) -> {
            arrivalCityErrorLabel.setText(newValue.isEmpty() ? "Please enter an arrival city" : "");
        });

        // Send date validation
        sendDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                sendDateErrorLabel.setText(newValue.isBefore(LocalDate.now()) ?
                        "Send date cannot be in the past" : "");
            } else {
                sendDateErrorLabel.setText("Please select a send date");
            }
        });

        // Transporter validation
        transporterCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            transporterErrorLabel.setText(newValue == null ? "Please select a transporter" : "");
        });
    }

    public void setExpedition(Expedition expedition) {
        this.expedition = expedition;

        // Set the header with expedition number
        headerLabel.setText("Edit Expedition #" + expedition.getExpeditionId());
        expeditionIdLabel.setText("#" + expedition.getExpeditionId());

        // Set the status label
        statusLabel.setText(expedition.getPackageStatus().toString());
        statusLabel.getStyleClass().addAll(getStatusStyleClass(expedition.getPackageStatus()));

        // Populate form fields with expedition data
        weightField.setText(String.valueOf(expedition.getWeight()));
        packageTypeCombo.setValue(expedition.getPackageType());
        departureCityField.setText(expedition.getDepartureCity());
        arrivalCityField.setText(expedition.getArrivalCity());

        // Convert Date to LocalDate for DatePicker - Fixed conversion
        LocalDate sendDate;
        if (expedition.getSendDate() instanceof java.sql.Date) {
            // Direct conversion for SQL date
            sendDate = ((java.sql.Date) expedition.getSendDate()).toLocalDate();
        } else {
            // Fallback for java.util.Date
            sendDate = expedition.getSendDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        sendDatePicker.setValue(sendDate);

        // Set selected transporter
        if (expedition.getTransporter() != null) {
            for (Transporter t : transporterCombo.getItems()) {
                if (t.getIdUser() == expedition.getTransporter().getIdUser()) {
                    transporterCombo.setValue(t);
                    break;
                }
            }
        }

        // Disable the form if expedition is not in PENDING status
        if (expedition.getPackageStatus() != PackageStatus.PENDING) {
            disableForm();
        }
    }

    private void disableForm() {
        weightField.setDisable(true);
        packageTypeCombo.setDisable(true);
        departureCityField.setDisable(true);
        arrivalCityField.setDisable(true);
        sendDatePicker.setDisable(true);
        transporterCombo.setDisable(true);
        saveBtn.setDisable(true);

        // Add message explaining why form is disabled
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Editing Restricted");
        alert.setHeaderText("This expedition cannot be edited");
        alert.setContentText("Only expeditions with PENDING status can be modified. " +
                "This expedition is currently in " + expedition.getPackageStatus() + " status.");
        alert.showAndWait();
    }

    public void setParentController(ClientExpeditionsController parentController) {
        this.parentController = parentController;
    }

    private void loadTransporters() {
        // In a real app, we would filter users to get only transporters
        List<Transporter> transporters = userService.getAllTransporters();

        // If no transporters found in database, use dummy data
        if (transporters.isEmpty()) {
            // Dummy transporter data
            transporters.add(new Transporter(
                    1, "John", "Smith", Gender.HOMME, "john@example.com", "password",
                    "profile.jpg", LocalDate.of(1985, 5, 15).atStartOfDay(),
                    "123-456-7890", TransportType.DHL, "www.johnsmith.com"
            ));
            transporters.add(new Transporter(
                    2, "Jane", "Doe", Gender.FEMME, "jane@example.com", "password",
                    "profile.jpg", LocalDate.of(1990, 8, 20).atStartOfDay(),
                    "987-654-3210", TransportType.FEDEX, "www.janedoe.com"
            ));
        }

        transporterCombo.setItems(FXCollections.observableArrayList(transporters));

        // Set up display for transporter items
        transporterCombo.setCellFactory(param -> new ListCell<Transporter>() {
            @Override
            protected void updateItem(Transporter item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getFirstName() + " " + item.getLastName() + " (" + item.getTransportType() + ")");
                }
            }
        });

        transporterCombo.setButtonCell(new ListCell<Transporter>() {
            @Override
            protected void updateItem(Transporter item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getFirstName() + " " + item.getLastName() + " (" + item.getTransportType() + ")");
                }
            }
        });
    }

    private void handleSaveExpedition(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            // Get values from form
            double weight = Double.parseDouble(weightField.getText());
            PackageType packageType = packageTypeCombo.getValue();
            String departureCity = departureCityField.getText();
            String arrivalCity = arrivalCityField.getText();
            Date sendDate = Date.from(sendDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            Transporter transporter = transporterCombo.getValue();

            // Update expedition object with new values
            expedition.setWeight(weight);
            expedition.setPackageType(packageType);
            expedition.setDepartureCity(departureCity);
            expedition.setArrivalCity(arrivalCity);
            expedition.setSendDate(sendDate);
            expedition.setTransporter(transporter);

            // Recalculate shipping cost based on weight and package type
            double baseRate = 10.0;
            double multiplier = 1.0;

            switch (packageType) {
                case DOCUMENT:
                    multiplier = 1.0;
                    break;
                case PARCEL:
                    multiplier = 1.5;
                    break;
                case FRAGILE:
                    multiplier = 1.8;
                    break;
                case LETTER:
                    multiplier = 2.0;
                    break;
            }

            double shippingCost = baseRate * weight * multiplier;
            expedition.setShippingCost(shippingCost);

            // Calculate estimated delivery date (simple: send date + 5 days)
            LocalDate estimatedDeliveryLocalDate = sendDatePicker.getValue().plusDays(5);
            Date estimatedDeliveryDate = Date.from(estimatedDeliveryLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            expedition.setEstimatedDeliveryDate(estimatedDeliveryDate);

            // Save the updated expedition
            expeditionService.update1(expedition);

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Expedition updated successfully!");

            // Refresh the parent controller and close window
            if (parentController != null) {
                parentController.refreshExpeditions();
            }

            closeWindow();

        } catch (NumberFormatException e) {
            weightErrorLabel.setText("Please enter a valid number for weight.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;
        clearErrorLabels();

        if (weightField.getText().isEmpty()) {
            weightErrorLabel.setText("Please enter weight in kg");
            isValid = false;
        } else if (!isNumeric(weightField.getText())) {
            weightErrorLabel.setText("Please enter a valid number");
            isValid = false;
        }

        if (packageTypeCombo.getValue() == null) {
            packageTypeErrorLabel.setText("Please select a package type");
            isValid = false;
        }

        if (departureCityField.getText().isEmpty()) {
            departureCityErrorLabel.setText("Please enter a departure city");
            isValid = false;
        }

        if (arrivalCityField.getText().isEmpty()) {
            arrivalCityErrorLabel.setText("Please enter an arrival city");
            isValid = false;
        }

        if (sendDatePicker.getValue() == null) {
            sendDateErrorLabel.setText("Please select a send date");
            isValid = false;
        } else if (sendDatePicker.getValue().isBefore(LocalDate.now())) {
            sendDateErrorLabel.setText("Send date cannot be in the past");
            isValid = false;
        }

        if (transporterCombo.getValue() == null) {
            transporterErrorLabel.setText("Please select a transporter");
            isValid = false;
        }

        return isValid;
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String getStatusStyleClass(PackageStatus status) {
        switch (status) {
            case PENDING:
                return "status-pending";
            case SHIPPED:
                return "status-shipped";
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

    private void closeWindow() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}