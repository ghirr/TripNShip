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

public class NewExpeditionController implements Initializable {

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
    private Button createBtn;

    @FXML
    private Button cancelBtn;

    private int clientId;
    private Client client;
    private ServiceExpedition expeditionService;
    private UserService userService;
    private ClientExpeditionsController parentController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        expeditionService = new ServiceExpedition();
        userService = new UserService();

        // Initialize package types
        packageTypeCombo.setItems(FXCollections.observableArrayList(PackageType.values()));

        // Set default date to today
        sendDatePicker.setValue(LocalDate.now());

        // Load transporters (in real app, filter by availability, rating, etc.)
        loadTransporters();

        // Clear error labels initially
        clearErrorLabels();

        // Add listeners for real-time validation
        setupValidationListeners();

        // Set button handlers
        createBtn.setOnAction(this::handleCreateExpedition);
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
            packageTypeErrorLabel.setText(newValue == null ? "" : "");
        });

        // Departure city validation
        departureCityField.textProperty().addListener((observable, oldValue, newValue) -> {
            departureCityErrorLabel.setText(newValue.isEmpty() ? "" : "");
        });

        // Arrival city validation
        arrivalCityField.textProperty().addListener((observable, oldValue, newValue) -> {
            arrivalCityErrorLabel.setText(newValue.isEmpty() ? "" : "");
        });

        // Send date validation
        sendDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                sendDateErrorLabel.setText(newValue.isBefore(LocalDate.now()) ?
                        "Send date cannot be in the past" : "");
            } else {
                sendDateErrorLabel.setText("");
            }
        });

        // Transporter validation
        transporterCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            transporterErrorLabel.setText(newValue == null ? "" : "");
        });
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
        // Fetch client data when clientId is set
        if (clientId > 0 && userService != null) {
            User user = userService.getById(clientId);
            if (user instanceof Client) {
                this.client = (Client) user;
            }
        }
    }

    public void setClient(Client client) {
        this.client = client;
        this.clientId = client.getIdUser();
    }

    public void setParentController(ClientExpeditionsController parentController) {
        this.parentController = parentController;
    }

    private void loadTransporters() {
        // In a real app, we would filter users to get only transporters
        List<User> allUsers = userService.getAll();
        List<Transporter> transporters = new ArrayList<>();

        // Filter users to get only transporters
        for (User user : allUsers) {
            if (user instanceof Transporter) {
                transporters.add((Transporter) user);
            }
        }

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

    private void handleCreateExpedition(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            // Verify client exists
            if (client == null) {
                // Try to fetch client if it wasn't set earlier
                User user = userService.getById(clientId);
                if (user instanceof Client) {
                    client = (Client) user;
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Client not found or invalid.");
                    return;
                }
            }

            // Create a new expedition
            Transporter transporter = transporterCombo.getValue();
            double weight = Double.parseDouble(weightField.getText());
            PackageType packageType = packageTypeCombo.getValue();
            String departureCity = departureCityField.getText();
            String arrivalCity = arrivalCityField.getText();
            Date sendDate = Date.from(sendDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Calculate estimated delivery date (simple: send date + 5 days)
            LocalDate estimatedDeliveryLocalDate = sendDatePicker.getValue().plusDays(5);
            Date estimatedDeliveryDate = Date.from(estimatedDeliveryLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Calculate shipping cost (simple: $10 per kg with multiplier based on package type)
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

            // Current location starts as departure city
            String currentLocation = departureCity;

            // Create expedition with client, current date as last updated
            Expedition newExpedition = new Expedition(
                    client,
                    transporter,
                    weight,
                    packageType,
                    PackageStatus.PENDING,
                    shippingCost,
                    sendDate,
                    estimatedDeliveryDate,
                    departureCity,
                    arrivalCity,
                    currentLocation,
                    new Date() // current date as last updated
            );

            // Save expedition
            expeditionService.add(newExpedition);

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Expedition created successfully!");

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

        if (clientId <= 0) {
            showAlert(Alert.AlertType.WARNING, "Client Error", "No client selected. Please select a client.");
            isValid = false;
        }

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