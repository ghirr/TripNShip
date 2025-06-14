package org.Esprit.TripNShip.Controllers;

import com.google.api.client.auth.oauth2.Credential;
import javafx.concurrent.Task;
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
import javafx.stage.StageStyle;

import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Services.AuthService;
import org.Esprit.TripNShip.Services.GoogleOAuthService;
import org.Esprit.TripNShip.Utils.Shared;

import java.io.IOException;

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
            User utilisateur = utilisateurService.login(email,password);
            if (utilisateur != null) {
                // Verify the password using BCrypt
                showAlert(Alert.AlertType.CONFIRMATION,"Sucess","login correct");

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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // Remove header text
        alert.setContentText(message);

        // Apply CSS
        DialogPane dialogPane = alert.getDialogPane();
//        dialogPane.getStylesheets().add(getClass().getResource("/Styles/custom-alert.css").toExternalForm());

        // Make window draggable
        alert.initStyle(StageStyle.TRANSPARENT);

        // The OK button will already be styled by the CSS

        alert.showAndWait();
    }


    public void switchToForgetPassword(ActionEvent actionEvent) {
        Shared.switchScene(actionEvent,getClass().getResource("/fxml/forgotPwd.fxml"),"Forgot Password");
    }

    public void switchToSignUp(ActionEvent actionEvent) {
        Shared.switchScene(actionEvent,getClass().getResource("/fxml/signUp.fxml"),"Sign Up");
    }
}
