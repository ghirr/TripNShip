package org.Esprit.TripNShip.Controllers;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import org.Esprit.TripNShip.Utils.Shared;
import org.Esprit.TripNShip.Utils.TwilioSMS;

import java.io.IOException;
import java.util.Random;

public class SmsVerificationController {

    @FXML
    private Label numberField;
    @FXML
    private TextField smsCodeField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button resendButton;

    private int code;
    private String phoneNumber;
    private PauseTransition resendTimer;
    private int timeLeft = 60; // Countdown in seconds (1 minute)

    @FXML
    public void initialize() {
        startResendCooldown();
    }

    public void verifyCode(ActionEvent actionEvent) throws IOException {
        if (code != Integer.parseInt(smsCodeField.getText())) {
            errorLabel.setText("Wrong code");
            errorLabel.setVisible(true);
        }
        else{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/resetPwd.fxml"));
            Parent root = loader.load();
            ResetPasswordController controller = loader.getController();
            controller.setPhoneNumber(phoneNumber);
            Shared.switchScene(actionEvent,root,"Reset Password");
        }
    }

    public void resendCode(ActionEvent actionEvent) {
        int generatedCode = new Random().nextInt(900000) + 100000;
        TwilioSMS.sendSMSConfirmationCode(phoneNumber, generatedCode);
        timeLeft = 60;
        startResendCooldown();
    }

    public void switchToLogin(ActionEvent actionEvent) {
        Shared.switchScene(actionEvent,getClass().getResource("/fxml/login.fxml"),"Login");
    }

    public void receiveCode(int code, String phone ) {
        this.phoneNumber = phone;
        this.code = code;
        numberField.setText("SMS Verification +216 XXXXXX"+ phoneNumber.substring(phoneNumber.length() - 2));
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
}
