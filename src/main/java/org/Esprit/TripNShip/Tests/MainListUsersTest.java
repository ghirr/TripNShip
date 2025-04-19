package org.Esprit.TripNShip.Tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainListUsersTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/iconLogo.png")));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/listUsers.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("List Users");
        primaryStage.setMaximized(true);
        primaryStage.show();
        }


    public static void main(String[] args) {
        launch(args);
    }
}
