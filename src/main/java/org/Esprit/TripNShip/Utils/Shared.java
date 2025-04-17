package org.Esprit.TripNShip.Utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class Shared {

    public static void switchScene(ActionEvent event, URL fxmlFile , String title) {
        try {

            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(fxmlFile);
            Parent root = loader.load();
            Scene scene = new Scene(root);
//            primaryStage.setMaximized(true);
//            primaryStage.setFullScreen(true);
            primaryStage.setMaximized(false);
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
//            primaryStage.setMaximized(true);
            primaryStage.setTitle(title);
//            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            //showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page : " + fxmlFile);
        }
    }

    public static void showAlert(Alert.AlertType type, String title, String message) {
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

    public static <T extends Enum<T>> T getEnumOrNull(Class<T> enumType, String value) {
        try {
            return value != null ? Enum.valueOf(enumType, value) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
