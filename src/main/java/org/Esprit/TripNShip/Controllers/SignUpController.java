package org.Esprit.TripNShip.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import org.Esprit.TripNShip.Utils.Shared;

public class SignUpController {

    @FXML
    private Button togglePasswordButton;

    @FXML
    private ImageView eyeIcon;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordText;
    @FXML
    private Region strengthSection1,strengthSection2,strengthSection3,strengthSection4,strengthSection5;
    @FXML
    private Label strengthLabel;

    private boolean isPasswordVisible ;


    public void switchToLogin(ActionEvent actionEvent) {
        Shared.switchScene(actionEvent,getClass().getResource("/fxml/login.fxml"),"Login");
    }

    public void togglePasswordVisibility(ActionEvent actionEvent) {
        if (!isPasswordVisible) {
            passwordText.setText(passwordField.getText());
            passwordText.setVisible(true);
            passwordField.setVisible(false);
            passwordText.requestFocus();
            passwordText.positionCaret(passwordField.getText().length());
        } else {
            passwordField.setText(passwordText.getText());
            passwordField.setVisible(true);
            passwordText.setVisible(false);
            passwordField.requestFocus();
            passwordField.positionCaret(passwordText.getText().length());
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

        String text = isPasswordVisible? passwordText.getText() : passwordField.getText();
        int strength = calculatePasswordStrength(text);

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

        int strength = 0;

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
}
