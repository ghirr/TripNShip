package org.Esprit.TripNShip.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.Esprit.TripNShip.Services.AuthService;
import org.Esprit.TripNShip.Utils.CaptchaVerifier;
import org.Esprit.TripNShip.Utils.Shared;
import org.Esprit.TripNShip.Utils.TwilioSMS;

import java.io.IOException;
import java.util.Random;

public class ForgotPasswordController {

    public Label errorLabel,statusLabel;
    @FXML
    private TextField phoneField;
    @FXML
    private Button sendCodeButton;


    @FXML
    private WebView recaptchaWebView;

    private String captchaToken = null;

    @FXML
    public void initialize() {

        recaptchaWebView.setPrefSize(300, 150);
        recaptchaWebView.setMinSize(300, 150);
        sendCodeButton.setDisable(true);
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> validatePhoneNumber());
        WebEngine webEngine = recaptchaWebView.getEngine();
        String url = "http://localhost/tripNship/recaptcha.html";
        webEngine.load(url);

        // Listen for alerts (this is how we catch the token)
        webEngine.setOnAlert(event -> {
            captchaToken = event.getData(); // Store the token
            System.out.println("Captcha Token received: " + captchaToken);
            validatePhoneNumber();
        });
    }

    private String getCaptchaToken() {
        return captchaToken;
    }

    private void validatePhoneNumber() {
        String phoneNumber = phoneField.getText().trim();
        boolean isPhoneValid = phoneNumber.matches("^[2459]\\d{7}$");
        boolean isCaptchaValid = captchaToken != null;

        if (isPhoneValid) {
            errorLabel.setText("");
        } else {
            errorLabel.setText("Invalid phone number");
        }

        // Only enable the button if BOTH phone and captcha are valid
        sendCodeButton.setDisable(!(isPhoneValid && isCaptchaValid));
    }


    public void sendSms(ActionEvent actionEvent) throws IOException {
        if(!CaptchaVerifier.verifyCaptcha(captchaToken)){
            captchaToken = null;
            validatePhoneNumber();
            return;
        }
        AuthService authService = new AuthService();
        if(!authService.verifiePhoneNumber(phoneField.getText())){
            statusLabel.setText("Invalid phone number");
            return;
        }
       int generatedCode = new Random().nextInt(900000) + 100000; // Code à 6 chiffres pour SMS
        TwilioSMS.sendSMSConfirmationCode(phoneField.getText(), generatedCode);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/smsVerification.fxml"));
        Parent root = loader.load();
        SmsVerificationController smsVerificationController = loader.getController();
        smsVerificationController.receiveCode(generatedCode, phoneField.getText());
        Shared.switchScene(actionEvent,root,"SMS Verification");

    }

    public void switchToLogin(ActionEvent actionEvent) {
        Shared.switchScene(actionEvent,getClass().getResource("/fxml/login.fxml"),"Login");
    }
}
