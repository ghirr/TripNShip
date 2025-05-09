package org.Esprit.TripNShip.Utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

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
}
