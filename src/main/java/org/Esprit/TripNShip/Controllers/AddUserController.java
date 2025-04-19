package org.Esprit.TripNShip.Controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.Esprit.TripNShip.Entities.Role;
import org.Esprit.TripNShip.Services.UserService;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class AddUserController {
    @FXML
    private TextField firstNameField, lastNameField, emailField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Label firstNameErrorLabel, lastNameErrorLabel, emailErrorLabel;
    @FXML
    private Label successMessageLabel;
    @FXML
    private Button closeButton, submitButton;


    private final UserService utilisateurService = new UserService();
    private final Map<String, Role> roleMap = new HashMap<>();

    @FXML
    private void initialize() {
        roleMap.put("Employee", Role.ACCOMMODATION_SPECIALIST);
        roleMap.put("Admin", Role.ADMIN);
        roleMap.put("Shipping Coordinator", Role.SHIPPING_COORDINATOR);
        roleMap.put("Tour Coordinator", Role.TOUR_COORDINATOR);
        roleMap.put("Travel Organizer", Role.TRAVEL_ORGANIZER);
        roleComboBox.getItems().addAll(roleMap.keySet());
        roleComboBox.setValue("Employee");

        firstNameErrorLabel.setVisible(false);
        lastNameErrorLabel.setVisible(false);
        emailErrorLabel.setVisible(false);

        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> validateFirstName());
        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> validateLastName());
        emailField.textProperty().addListener((observable, oldValue, newValue) -> validateEmail());

        closeButton.setOnAction(e -> {
            // Close the current window
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });

        updateSubmitButtonState();
    }

    private void validateFirstName() {
        String firstName = firstNameField.getText().trim();
        if (firstName.isEmpty()) {
            showError(firstNameField, firstNameErrorLabel, "❌ Lastname required !");
        } else {
            showSuccess(firstNameField, firstNameErrorLabel, "✅ Lastname valid !");
        }
        updateSubmitButtonState();
    }

    private void validateLastName() {
        String lastName = lastNameField.getText().trim();
        if (lastName.isEmpty()) {
            showError(lastNameField, lastNameErrorLabel, "❌ firstName required !");
        } else {
            showSuccess(lastNameField, lastNameErrorLabel, "✅ firstName valid !");
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

    private void updateSubmitButtonState() {
        boolean isValid = firstNameErrorLabel.getText().contains("✅") &&
                lastNameErrorLabel.getText().contains("✅") &&
                emailErrorLabel.getText().contains("✅");

        submitButton.setDisable(!isValid);
    }

    @FXML
    private void handleSubmit() {
        if (submitButton.isDisabled()) {
            return;
        }

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        Role role = roleMap.get(roleComboBox.getValue());

//        try {
//            // 🚨 Vérifier si l'email existe déjà
//            if (utilisateurService.getUserByEmail(email) != null) {
//                showAlert(Alert.AlertType.ERROR, "Erreur", "Cet email est déjà utilisé !");
//                return;  // ❌ Annuler l'opération
//            }
//
//            // 1️⃣ Générer un mot de passe aléatoire depuis UtilisateurService
//            String rawPassword = utilisateurService.generateRandomPassword(8);
//
//            // 2️⃣ Hacher le mot de passe avec BCrypt
//            String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
//
//            // 3️⃣ Créer un nouvel utilisateur avec le mot de passe haché
//            Utilisateur newUser = new Utilisateur(firstName, lastName, email, hashedPassword,
//                    null, null, null, role, 0, 0, null, "", Gender.HOMME);
//
//            int result = utilisateurService.add(newUser);
//            if (result > 0) {
//                // 4️⃣ Envoyer un e-mail avec le mot de passe en clair
//                utilisateurService.sendConfirmationEmail(email, rawPassword);
//
//                showSuccessMessage("Employé ajouté avec succès ! Un e-mail contenant le mot de passe a été envoyé.");
//
//                // 🔄 Mettre à jour la liste principale après l'ajout
//                ListUsersController.getInstance().refreshUserList();
//
//                // ✅ Fermer la fenêtre
//                ((Stage) submitButton.getScene().getWindow()).close();
//            }
//        } catch (SQLException e) {
//            System.err.println("Erreur SQL : " + e.getMessage());
//        }
    }


//    private String generateRandomPassword() {
//        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
//        SecureRandom random = new SecureRandom();
//        StringBuilder password = new StringBuilder();
//        for (int i = 0; i < 10; i++) {
//            password.append(chars.charAt(random.nextInt(chars.length())));
//        }
//        return password.toString();
//    }


//    public String hashPassword(String password) {
//        return BCrypt.hashpw(password, BCrypt.gensalt());
//    }


//    private void sendConfirmationEmail(String toEmail, String generatedPassword) {
//        String subject = "Votre compte a été créé !";
//        String content = "<p>Bonjour,</p>"
//                + "<p>Votre compte a été créé avec succès.</p>"
//                + "<p>Voici vos informations de connexion :</p>"
//                + "<ul>"
//                + "<li><b>Email :</b> " + toEmail + "</li>"
//                + "<li><b>Mot de passe :</b> " + generatedPassword + "</li>"
//                + "</ul>"
//                + "<p>Nous vous conseillons de modifier votre mot de passe après votre première connexion.</p>"
//                + "<p>Ouvrez l'application et connectez-vous avec vos identifiants.</p>";
//
//        try {
//            Properties properties = new Properties();
//            properties.put("mail.smtp.auth", "true");
//            properties.put("mail.smtp.starttls.enable", "true");
//            properties.put("mail.smtp.host", "smtp.gmail.com");
//            properties.put("mail.smtp.port", "587");
//
//            Session session = Session.getInstance(properties, new Authenticator() {
//                protected PasswordAuthentication getPasswordAuthentication() {
//                    return new PasswordAuthentication("maryemsassi.dev@gmail.com", "jlej mknk aukk iqlx");
//                }
//            });
//
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress("emaryemsassi.dev@gmail.com"));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
//            message.setSubject(subject);
//            message.setContent(content, "text/html");
//
//            Transport.send(message);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }


    private void showError(TextField field, Label label, String message) {
        field.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-insets: 0; -fx-padding: 2px;");
        field.setFocusTraversable(false);
        label.setText(message);
        label.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-border-color: transparent; -fx-background-color: transparent; -fx-padding: 3px;");
        label.setVisible(true);
    }
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void showSuccess(TextField field, Label label, String message) {
        field.setStyle("-fx-border-color: green; -fx-border-width: 2px; -fx-border-insets: 0; -fx-padding: 2px;");
        field.setFocusTraversable(false);
        label.setText(message);
        label.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-border-color: transparent; -fx-background-color: transparent; -fx-padding: 3px;");
        label.setVisible(true);
    }

//    private void showSuccessMessage(String message) {
//        successMessageLabel.setText(message);
//        successMessageBox.setVisible(true);
//        PauseTransition delay = new PauseTransition(Duration.seconds(3));
//        delay.setOnFinished(event -> successMessageBox.setVisible(false));
//        delay.play();
//    }

//    @FXML
//    private void closeSuccessMessage() {
//        successMessageBox.setVisible(false);
//    }

    private void resetForm() {
        firstNameField.clear();
        firstNameField.setStyle("");

        lastNameField.clear();
        lastNameField.setStyle("");

        emailField.clear();
        emailField.setStyle("");

        roleComboBox.setValue("Employé");

        firstNameErrorLabel.setText("");
        firstNameErrorLabel.setVisible(false);

        lastNameErrorLabel.setText("");
        lastNameErrorLabel.setVisible(false);

        emailErrorLabel.setText("");
        emailErrorLabel.setVisible(false);

        submitButton.setDisable(true);
    }

//    @FXML
//    private void handleClose() {
//        ((Stage) closeButton.getScene().getWindow()).close();
//    }
//
//    @FXML
//    private Button closeSuccessButton;
//
//    @FXML
//    private void handleCloseSuccessMessage() {
//        successMessageBox.setVisible(false);
//    }


}
