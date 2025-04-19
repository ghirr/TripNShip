package org.Esprit.TripNShip.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Client;
import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Services.AuthService;
import org.Esprit.TripNShip.Utils.Shared;

import java.io.IOException;

import static org.Esprit.TripNShip.Utils.Shared.showAlert;

public class SignUpController {

    public TextField firstNameField,lastNameField,emailField;
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

    private int strength;


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

    public void handleGoogleSignUp(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_with_google.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/GoogleLogo.png")));
            stage.setTitle("SignUp with Google");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void signUp(ActionEvent actionEvent) {
        String email = emailField.getText().trim();
        String password = passwordField.isVisible() ? passwordField.getText() : passwordText.getText();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();

        if(email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() ) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        if (strength <=3){
            showAlert(Alert.AlertType.ERROR, "PasswordStrength", "Kindly use stronger password");
            return;
        }

        AuthService authService = new AuthService();
        authService.signUp(new Client(firstName, lastName,email , password));
        User user = authService.login(email,password);
        System.out.println(user.getEmail());
        showAlert(Alert.AlertType.CONFIRMATION,"Sucess","Sign Up correct");

    }
}
