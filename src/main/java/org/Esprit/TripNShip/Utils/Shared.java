package org.Esprit.TripNShip.Utils;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Optional;

public class Shared {

    private static final String CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";

    public static void switchScene(Event event, URL fxmlFile , String title) {
        try {

            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(fxmlFile);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setMaximized(false);
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.setTitle(title);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void switchScene(Event event, Parent root , String title) {
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            primaryStage.setMaximized(false);
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.setTitle(title);
            primaryStage.show();
        
    }

    public static void switchScene(Button btn, URL fxmlFile , String title) {
        try {

            Stage primaryStage = (Stage)  btn.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(fxmlFile);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setMaximized(false);
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.setTitle(title);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        // Apply CSS
        DialogPane dialogPane = alert.getDialogPane();
        // Make window draggable
        alert.initStyle(StageStyle.TRANSPARENT);
        // The OK button will already be styled by the CSS
        alert.showAndWait();
    }

    public static Optional<ButtonType> deletePopUP(String title){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(title);
        alert.getDialogPane().getStylesheets().add(Shared.class.getResource("/Styles/styleDeleteAlert.css").toExternalForm());
        ImageView image = new ImageView(new Image(Shared.class.getResourceAsStream("/images/dangerIcon.png")));
        image.setFitHeight(30);
        image.setFitWidth(30);
        alert.setGraphic(image);
        // Set custom icon
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Shared.class.getResourceAsStream("/images/deleteIcon.png")));
        return alert.showAndWait();
    }

    public static <T extends Enum<T>> T getEnumOrNull(Class<T> enumType, String value) {
        try {
            return value != null ? Enum.valueOf(enumType, value) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static String generateRandomPassword() {

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            password.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return password.toString();
    }

}
