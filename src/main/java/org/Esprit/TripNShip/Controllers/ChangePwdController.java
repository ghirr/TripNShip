package org.Esprit.TripNShip.Controllers;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.Esprit.TripNShip.Services.AuthService;
import org.Esprit.TripNShip.Utils.EmailSender;
import org.Esprit.TripNShip.Utils.Shared;
import org.Esprit.TripNShip.Utils.UserSession;

import java.util.Random;

import static org.Esprit.TripNShip.Utils.Shared.generateRandomCode;

public class ChangePwdController {


    public Button resendButton, verifyButton, changePwdBtn;
    public ProgressBar progressBar;
    public Label stepLabel, strengthLabel, passwordErrorLabel,verificationErrorLabel;
    public VBox step1Container, step2Container;
    public TextField verificationCodeField;
    public PasswordField currentPasswordField, newPasswordField, confirmPasswordField;
    public Region strengthSection1, strengthSection2, strengthSection3, strengthSection4, strengthSection5;
    private int strength;
    private AuthService authService;
    private int code;
    private PauseTransition resendTimer;
    private int timeLeft;

    @FXML
    private void initialize() {
        resendCode();
        authService = new AuthService();
        resendButton.setOnAction(event -> {resendCode();});
    }

    public void resendCode() {
        code = new Random().nextInt(900000) + 100000;
        if(UserSession.getInstance() != null) {
            new Thread(() -> EmailSender.accountEmail(UserSession.getInstance().getUserEmail(),
                    UserSession.getInstance().getUserFirstName() + " " + UserSession.getInstance().getUserLastName(),
                    String.valueOf(code),EmailSender.changePwdTemplate,
                    "Code verification",
                    "TripNShip Team")).start();
        }
        timeLeft = 60;
        startResendCooldown();
    }

    public void verifyCode(ActionEvent actionEvent) {
        if (verificationCodeField.getText().trim().isEmpty() || Integer.parseInt(verificationCodeField.getText()) != code) {
            verificationErrorLabel.setVisible(true);
        }
        else {
            step1Container.setVisible(false);
            progressBar.setProgress(1);
            stepLabel.setText("Step 2 of 2: Verification");
            step2Container.setVisible(true);
        }
    }

    public void changePassword(ActionEvent actionEvent) {

        if(newPasswordField.getText().trim().isEmpty() || confirmPasswordField.getText().trim().isEmpty()
                || confirmPasswordField.getText().trim().isEmpty() || strength<3 ||
                !newPasswordField.getText().trim().equals(confirmPasswordField.getText().trim())
                || !authService.checkPassword(UserSession.getInstance().getUserId(),currentPasswordField.getText().trim())) {
            passwordErrorLabel.setVisible(true);
        }
        else {
            authService.updatePassword(UserSession.getInstance().getUserId(),newPasswordField.getText().trim());
            Shared.showAlert(Alert.AlertType.INFORMATION, "Password Changed", "Password Changed successfully");
            ((Stage) verifyButton.getScene().getWindow()).close();
        }
    }

    private void startResendCooldown() {
        resendButton.setDisable(true);
        resendTimer = new PauseTransition(Duration.seconds(1));
        resendTimer.setOnFinished(event -> {
            timeLeft--;
            resendButton.setText(String.format("Resend in: %02d:%02d", timeLeft / 60, timeLeft % 60));
            if (timeLeft <= 0) {
                resendButton.setDisable(false);
                resendButton.setText("Resend Code");
            } else {
                resendTimer.play(); // Keep playing until time is up
            }
        });
        resendTimer.play();  // Start countdown
    }

    @FXML
    private void updatePasswordStrength(KeyEvent keyEvent) {

        String text = newPasswordField.getText();
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
}
