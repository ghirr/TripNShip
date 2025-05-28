package org.Esprit.TripNShip.Controllers.ExpeditionManagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.*;

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
    private ComboBox<String> departureCityCombo;

    @FXML
    private Label departureCityErrorLabel;

    @FXML
    private ComboBox<String> arrivalCityCombo;

    @FXML
    private Label arrivalCityErrorLabel;

    @FXML
    private DatePicker sendDatePicker;

    @FXML
    private Label sendDateErrorLabel;

    @FXML
    private Button createBtn;

    @FXML
    private Button cancelBtn;

    private int clientId;
    private Client client;
    private ServiceExpedition expeditionService;
    private UserService userService;
    private ClientExpeditionsController parentController;

    // List of countries for the dropdowns
    private final String[] countries = {
            "Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antigua and Barbuda", "Argentina", "Armenia",
            "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus",
            "Belgium", "Belize", "Benin", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil",
            "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cabo Verde", "Cambodia", "Cameroon", "Canada",
            "Central African Republic", "Chad", "Chile", "China", "Colombia", "Comoros", "Congo",
            "Costa Rica", "Côte d'Ivoire", "Croatia", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti",
            "Dominica", "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea",
            "Estonia", "Eswatini", "Ethiopia", "Fiji", "Finland", "France", "Gabon", "Gambia", "Georgia", "Germany",
            "Ghana", "Greece", "Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Honduras",
            "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica",
            "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, North", "Korea, South", "Kosovo",
            "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein",
            "Lithuania", "Luxembourg", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta",
            "Marshall Islands", "Mauritania", "Mauritius", "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia",
            "Montenegro", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands",
            "New Zealand", "Nicaragua", "Niger", "Nigeria", "North Macedonia", "Norway", "Oman", "Pakistan", "Palau",
            "Palestine", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal",
            "Qatar", "Romania", "Russia", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia",
            "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia",
            "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia",
            "Solomon Islands", "Somalia", "South Africa", "South Sudan", "Spain", "Sri Lanka", "Sudan", "Suriname",
            "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor-Leste", "Togo",
            "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Tuvalu", "Uganda", "Ukraine",
            "United Arab Emirates", "United Kingdom", "United States", "Uruguay", "Uzbekistan", "Vanuatu",
            "Vatican City", "Venezuela", "Vietnam", "Yemen", "Zambia", "Zimbabwe"
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        expeditionService = new ServiceExpedition();
        userService = new UserService();

        // Initialize package types
        packageTypeCombo.setItems(FXCollections.observableArrayList(PackageType.values()));

        // Initialize countries for departure and arrival
        ObservableList<String> countryList = FXCollections.observableArrayList(countries);
        departureCityCombo.setItems(countryList);
        arrivalCityCombo.setItems(countryList);

        // Add search capability to the country dropdowns
        setupComboBoxSearch(departureCityCombo);
        setupComboBoxSearch(arrivalCityCombo);

        // Set default date to today
        sendDatePicker.setValue(LocalDate.now());

        // Clear error labels initially
        clearErrorLabels();

        // Add listeners for real-time validation
        setupValidationListeners();

        // Set button handlers
        createBtn.setOnAction(this::handleCreateExpedition);
        cancelBtn.setOnAction(event -> closeWindow());
    }

    // Helper method to make ComboBoxes searchable
    private void setupComboBoxSearch(ComboBox<String> comboBox) {
        // Store the original list
        final ObservableList<String> originalItems = comboBox.getItems();

        // Add listener to enable searching
        comboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            // If the ComboBox isn't showing its popup, return
            if (!comboBox.isShowing()) {
                return;
            }

            // Text field is empty, show all values
            if (newValue == null || newValue.isEmpty()) {
                comboBox.setItems(originalItems);
                comboBox.show();
                return;
            }

            // Filter items by the entered text
            ObservableList<String> filteredItems = FXCollections.observableArrayList();
            String lowerCaseFilter = newValue.toLowerCase();

            for (String item : originalItems) {
                if (item.toLowerCase().contains(lowerCaseFilter)) {
                    filteredItems.add(item);
                }
            }

            comboBox.setItems(filteredItems);
            comboBox.show();
        });

        // Make ComboBox editable to allow searching but prevent custom values
        comboBox.setEditable(true);

        // Add listener to validate selection
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // When a valid item is selected, display it in the editor
                comboBox.getEditor().setText(newValue);
            }
        });
    }

    private void clearErrorLabels() {
        weightErrorLabel.setText("");
        packageTypeErrorLabel.setText("");
        departureCityErrorLabel.setText("");
        arrivalCityErrorLabel.setText("");
        sendDateErrorLabel.setText("");
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
        departureCityCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            departureCityErrorLabel.setText(newValue == null ? "" : "");
        });

        // Arrival city validation
        arrivalCityCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            arrivalCityErrorLabel.setText(newValue == null ? "" : "");
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

            // Create a new expedition - without a transporter
            double weight = Double.parseDouble(weightField.getText());
            PackageType packageType = packageTypeCombo.getValue();
            String departureCity = departureCityCombo.getValue();
            String arrivalCity = arrivalCityCombo.getValue();
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

            // Create expedition with client, current date as last updated, but NO transporter
            Expedition newExpedition = new Expedition(
                    client,
                    null, // No transporter assigned yet
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
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Expedition created successfully! Waiting for transporter assignment.");

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

        if (departureCityCombo.getValue() == null) {
            departureCityErrorLabel.setText("Please select a departure country");
            isValid = false;
        }

        if (arrivalCityCombo.getValue() == null) {
            arrivalCityErrorLabel.setText("Please select an arrival country");
            isValid = false;
        }

        if (sendDatePicker.getValue() == null) {
            sendDateErrorLabel.setText("Please select a send date");
            isValid = false;
        } else if (sendDatePicker.getValue().isBefore(LocalDate.now())) {
            sendDateErrorLabel.setText("Send date cannot be in the past");
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