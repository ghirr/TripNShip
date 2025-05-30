package org.Esprit.TripNShip.Controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import org.Esprit.TripNShip.Entities.Role;
import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Services.AuthService;
import org.Esprit.TripNShip.Utils.Shared;
import org.Esprit.TripNShip.Utils.UserSession;

import java.io.IOException;

import static org.Esprit.TripNShip.Utils.Shared.showAlert;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordText;
    @FXML
    private Button toggleButton;
    @FXML
    private Button loginButton;
    @FXML
    private Hyperlink forgotPasswordLink;
    @FXML
    private ImageView eyeIcon;

    private final AuthService utilisateurService = new AuthService();
    private boolean isPasswordVisible ;

    @FXML
    private void togglePasswordVisibility() {
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

    @FXML
    private void updateEyeIcon() {
        String iconPath = isPasswordVisible ? "/images/eye_closed.png" : "/images/eye_open.png";
        eyeIcon.setImage((new Image(getClass().getResourceAsStream(iconPath))));
        isPasswordVisible = !isPasswordVisible;
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.isVisible() ? passwordField.getText() : passwordText.getText();

        // Input validation
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        try {
            User user = utilisateurService.login(email,password);
            if (user != null) {
                if(user.getRole()== Role.ADMIN){
                    UserSession.initSession(user);
                    Shared.switchScene(event,getClass().getResource("/fxml/adminNavigation.fxml"),"Main");
                }
                else{
                    showAlert(Alert.AlertType.CONFIRMATION,"Sucess","login correct");
                }

            } else {
                // Password is incorrect
                showAlert(Alert.AlertType.ERROR, "Erreur", "Invalid credentials");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Problème de connexion à la base de données.");
        }
    }

    public void handleGoogleLogin(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_with_google.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/GoogleLogo.png")));
            stage.setTitle("Login with Google");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToForgetPassword(ActionEvent actionEvent) {
        Shared.switchScene(actionEvent,getClass().getResource("/fxml/forgotPwd.fxml"),"Forgot Password");
    }

    public void switchToSignUp(ActionEvent actionEvent) {
        Shared.switchScene(actionEvent,getClass().getResource("/fxml/signUp.fxml"),"Sign Up");
    }
}
