package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Employee;
import org.Esprit.TripNShip.Entities.Role;
import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Services.UserService;
import org.Esprit.TripNShip.Utils.EmailSender;

import java.util.HashMap;
import java.util.Map;

import static org.Esprit.TripNShip.Utils.Shared.generateRandomCode;
import static org.Esprit.TripNShip.Utils.Shared.showAlert;


public class AddUserController {
    @FXML
    private TextField firstNameField, lastNameField, emailField, addressField,salaryField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Label firstNameErrorLabel, lastNameErrorLabel, emailErrorLabel, addressErrorLabel, salaryErrorLabel;

    @FXML
    private Button closeButton, submitButton;


    private final UserService userService = new UserService();
    private final Map<String, Role> roleMap = new HashMap<>();

    @FXML
    private void initialize() {
        roleMap.put("Accommodation Specialist", Role.ACCOMMODATION_SPECIALIST);
        roleMap.put("Admin", Role.ADMIN);
        roleMap.put("Shipping Coordinator", Role.SHIPPING_COORDINATOR);
        roleMap.put("Tour Coordinator", Role.TOUR_COORDINATOR);
        roleMap.put("Travel Organizer", Role.TRAVEL_ORGANIZER);
        roleComboBox.getItems().addAll(roleMap.keySet());
        roleComboBox.setValue("Accommodation Specialist");

        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> validateRequiredField("firstName",firstNameField,firstNameErrorLabel));
        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> validateRequiredField("lastName",lastNameField,lastNameErrorLabel));
        addressField.textProperty().addListener((observable, oldValue, newValue) -> validateRequiredField("adress", addressField, addressErrorLabel));
        emailField.textProperty().addListener((observable, oldValue, newValue) -> validateEmail());
        salaryField.textProperty().addListener((observable, oldValue, newValue) -> validateSalary());

        closeButton.setOnAction(e -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });

        updateSubmitButtonState();
    }

    @FXML
    private void handleSubmit() {
        if (submitButton.isDisabled()) {
            return;
        }

        String email = emailField.getText().trim();
        if (userService.getUserByEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Error", "This Email is already used !");
            return;
        }

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        Role role = roleMap.get(roleComboBox.getValue());
        String address = addressField.getText().trim();
        double salary = Double.parseDouble(salaryField.getText().trim());
        String password = generateRandomCode(15);

        User newUser = new Employee(firstName,lastName,role,email,address,password,salary);
        userService.add(newUser);
        new Thread(() -> EmailSender.accountEmail(email, firstName + " " + lastName, password,EmailSender.welcomeEmailTemplate,"Welcome To TripNShip","HR Team")).start();
        ListUsersController.getInstance().reloadUserList();
        ((Stage) submitButton.getScene().getWindow()).close();

//              Shared.showAlert(Alert.AlertType.INFORMATION,"Sucess",firstName+" "+lastName+" user added");


    }


    private void validateRequiredField(String fieldName,TextField field,Label labelError) {
        String firstName = field.getText().trim();
        if (firstName.isEmpty()) {
            showError(field, labelError, "❌ "+fieldName+ " required !");
        } else {
            showSuccess(field, labelError, "✅ "+fieldName+" valid !");
        }
        updateSubmitButtonState();
    }

    private void validateEmail() {
        String email = emailField.getText().trim();
        if (email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$")) {
            showError(emailField, emailErrorLabel, "❌ Email invalid !");
        } else {
            showSuccess(emailField, emailErrorLabel, "✅ Email valid !");
        }
        updateSubmitButtonState();
    }

    private void validateSalary() {
        String input = salaryField.getText().trim();
        try {
            Double.parseDouble(input);
            showSuccess(salaryField, salaryErrorLabel, "✅ Valid salary!");
        } catch (NumberFormatException e) {
            showError(salaryField, salaryErrorLabel, "❌ Invalid salary!");
        }
        updateSubmitButtonState();
    }


    private void updateSubmitButtonState() {
        boolean isValid = firstNameErrorLabel.getText().contains("✅") &&
                lastNameErrorLabel.getText().contains("✅") &&
                emailErrorLabel.getText().contains("✅") &&
                addressErrorLabel.getText().contains("✅") &&
                salaryErrorLabel.getText().contains("✅");

        submitButton.setDisable(!isValid);
    }

    private void showError(TextField field, Label label, String message) {
        field.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-insets: 0; -fx-padding: 2px;");
        field.setFocusTraversable(false);
        label.setText(message);
        label.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-border-color: transparent; -fx-background-color: transparent; -fx-padding: 3px;");
        label.setVisible(true);
    }


    private void showSuccess(TextField field, Label label, String message) {
        field.setStyle("-fx-border-color: green; -fx-border-width: 2px; -fx-border-insets: 0; -fx-padding: 2px;");
        field.setFocusTraversable(false);
        label.setText(message);
        label.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-border-color: transparent; -fx-background-color: transparent; -fx-padding: 3px;");
        label.setVisible(true);
    }
}
