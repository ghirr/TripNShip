package org.Esprit.TripNShip.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.Esprit.TripNShip.Services.AuthService;

import static org.Esprit.TripNShip.Utils.Shared.showAlert;
import static org.Esprit.TripNShip.Utils.Shared.switchScene;

public class ResetPasswordController {
    @FXML
    public PasswordField newPasswordField,confirmPasswordField;
    @FXML
    public TextField textNewPasswordField;
    @FXML
    public Button toggleNewPasswordButton,resetButton;
    @FXML
    public HBox strengthMeterContainer;
    @FXML
    public Region strengthSection1,strengthSection2,strengthSection3,strengthSection4,strengthSection5;
    @FXML
    public Label strengthLabel,errorLabel;
    @FXML
    public ImageView eyeIcon;

    private String phoneNumber;

    private boolean isPasswordVisible ;
    private int strength;

    private AuthService authService = new AuthService();



    @FXML
    private void resetPassword(ActionEvent event) {
        String newPassword = isPasswordVisible ? textNewPasswordField.getText().trim() : newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();


        // Perform validations
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill all fields !");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Check your passwords");
            return;
        }

        // Check if password is strong enough (at least medium strength)
        if (strength <=3) {
            showError("Kindly use stronger password");
            return;
        }

        System.out.println("mriguel");
        if(authService.updatePasswordByPhoneNumber(phoneNumber, newPassword)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Password successfully reset it");
            switchScene(event,getClass().getResource("/fxml/login.fxml"),"Login");
        }else {
            showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong !");
        }

    }

    @FXML
    public void togglePasswordVisibility(ActionEvent actionEvent) {
        if (!isPasswordVisible) {
            textNewPasswordField.setText(newPasswordField.getText());
            textNewPasswordField.setVisible(true);
            newPasswordField.setVisible(false);
            textNewPasswordField.requestFocus();
            textNewPasswordField.positionCaret(newPasswordField.getText().length());
        } else {
            newPasswordField.setText(textNewPasswordField.getText());
            newPasswordField.setVisible(true);
            textNewPasswordField.setVisible(false);
            newPasswordField.requestFocus();
            newPasswordField.positionCaret(textNewPasswordField.getText().length());
        }
        updateEyeIcon();
    }

    private void updateEyeIcon() {
        String iconPath = isPasswordVisible ? "/images/eye_closed.png" : "/images/eye_open.png";
        eyeIcon.setImage((new Image(getClass().getResourceAsStream(iconPath))));
        isPasswordVisible = !isPasswordVisible;
    }

    @FXML
    private void updatePasswordStrength(KeyEvent keyEvent) {

        String text = isPasswordVisible? textNewPasswordField.getText() : newPasswordField.getText();
        strength = calculatePasswordStrength(text);

        // Reset all to default with finer height
        strengthSection1.setStyle("-fx-background-color: #ddd; -fx-pref-height: 3; -fx-pref-width: 50;");
        strengthSection2.setStyle("-fx-background-color: #ddd; -fx-pref-height: 3; -fx-pref-width: 50;");
        strengthSection3.setStyle("-fx-background-color: #ddd; -fx-pref-height: 3; -fx-pref-width: 50;");
        strengthSection4.setStyle("-fx-background-color: #ddd; -fx-pref-height: 3; -fx-pref-width: 50;");
        strengthSection5.setStyle("-fx-background-color: #ddd; -fx-pref-height: 3; -fx-pref-width: 50;");


        if (strength >= 1) {
            strengthSection1.setStyle("-fx-background-color: #F93827; -fx-pref-height: 3; -fx-pref-width: 50;");
            strengthLabel.setText("Très faible!");
            strengthLabel.setStyle("-fx-text-fill: #FF4136; -fx-padding: 5 0 0 0;");
        }

        if (strength >= 2) {
            strengthSection2.setStyle("-fx-background-color: #FF9D23; -fx-pref-height: 3; -fx-pref-width: 50;");
            strengthLabel.setText("Faible!");
            strengthLabel.setStyle("-fx-text-fill: #FF851B; -fx-padding: 5 0 0 0;");
        }

        if (strength >= 3) {
            strengthSection3.setStyle("-fx-background-color: #FFC145; -fx-pref-height: 3; -fx-pref-width: 50;");
            strengthLabel.setText("Moyen!");
            strengthLabel.setStyle("-fx-text-fill: #2ECC40; -fx-padding: 5 0 0 0;");
        }

        if (strength >= 4) {
            strengthSection4.setStyle("-fx-background-color: #FFFC9B; -fx-pref-height: 3; -fx-pref-width: 50;");
            strengthLabel.setText("fort!");
            strengthLabel.setStyle("-fx-text-fill: #0074D9; -fx-padding: 5 0 0 0;");
        }
        if (strength >= 5) {
            strengthSection5.setStyle("-fx-background-color: #B7E5B4; -fx-pref-height: 3; -fx-pref-width: 50;");
            strengthLabel.setText("Très fort!");
            strengthLabel.setStyle("-fx-text-fill: #0074D9; -fx-padding: 5 0 0 0;");
        }
    }

    private int calculatePasswordStrength(String password) {

        if (password == null || password.isEmpty()) {
            return 0;
        }

        strength = 0;

        // Length check
        if (password.length() >= 8) {
            strength += 1;
        }

        // Contains lowercase
        if (password.matches(".*[a-z].*")) {
            strength += 1;
        }

        // Contains uppercase and numbers
        if (password.matches(".*[A-Z].*") ) {
            strength += 1;
        }
        if (password.matches(".*[0-9].*")) {
            strength += 1;
        }

        // Contains special characters
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            strength += 1;
        }

        return Math.min(5, strength);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
    }

    public void setPhoneNumber(String phone) {
        phoneNumber=phone;
    }


}
