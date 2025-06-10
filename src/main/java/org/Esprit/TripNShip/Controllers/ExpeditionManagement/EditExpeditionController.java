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
import java.util.Date;
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
    private Button saveBtn;

    @FXML
    private Button cancelBtn;

    private Expedition expedition;
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

        // Clear error labels initially
        clearErrorLabels();

        // Add listeners for real-time validation
        setupValidationListeners();

        // Set button handlers
        saveBtn.setOnAction(this::handleSaveExpedition);
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
            packageTypeErrorLabel.setText(newValue == null ? "Please select a package type" : "");
        });

        // Departure city validation
        departureCityCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            departureCityErrorLabel.setText(newValue == null ? "Please select a departure country" : "");
        });

        // Arrival city validation
        arrivalCityCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            arrivalCityErrorLabel.setText(newValue == null ? "Please select an arrival country" : "");
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

        // Set departure and arrival countries (find closest match or select first for display)
        setCountryComboValue(departureCityCombo, expedition.getDepartureCity());
        setCountryComboValue(arrivalCityCombo, expedition.getArrivalCity());

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

        // Disable the form if expedition is not in PENDING status
        if (expedition.getPackageStatus() != PackageStatus.PENDING) {
            disableForm();
        }
    }

    // Helper method to find and set closest country match
    private void setCountryComboValue(ComboBox<String> comboBox, String cityOrCountry) {
        if (cityOrCountry == null || cityOrCountry.isEmpty()) {
            return;
        }

        // First try exact match
        for (String country : countries) {
            if (country.equalsIgnoreCase(cityOrCountry)) {
                comboBox.setValue(country);
                return;
            }
        }

        // If no exact match, try contains
        for (String country : countries) {
            if (cityOrCountry.contains(country) || country.contains(cityOrCountry)) {
                comboBox.setValue(country);
                return;
            }
        }

        // As a fallback, just set the text in the editor
        comboBox.getEditor().setText(cityOrCountry);
    }

    private void disableForm() {
        weightField.setDisable(true);
        packageTypeCombo.setDisable(true);
        departureCityCombo.setDisable(true);
        arrivalCityCombo.setDisable(true);
        sendDatePicker.setDisable(true);
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

    private void handleSaveExpedition(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            // Get values from form
            double weight = Double.parseDouble(weightField.getText());
            PackageType packageType = packageTypeCombo.getValue();
            String departureCity = departureCityCombo.getValue();
            String arrivalCity = arrivalCityCombo.getValue();
            Date sendDate = Date.from(sendDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Update expedition object with new values
            expedition.setWeight(weight);
            expedition.setPackageType(packageType);
            expedition.setDepartureCity(departureCity);
            expedition.setArrivalCity(arrivalCity);
            expedition.setSendDate(sendDate);
            // Maintain the existing transporter (or null)
            // Do not change transporter status

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

    private String getStatusStyleClass(PackageStatus status) {
        switch (status) {
            case PENDING:
                return "status-pending";
            case AWAITING_CLIENT_APPROVAL:
                return "status-awaiting-approval";
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
