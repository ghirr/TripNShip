package org.Esprit.TripNShip.Tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Controllers.SmsVerificationController;

public class ResendCodeTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file and initialize the controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/resetPwd.fxml"));

        // Let FXMLLoader handle controller instantiation and injection
        Parent root = loader.load();

//        // Get the controller from the loader
//        SmsVerificationController smsVerificationController = loader.getController();
//
//        // Now call reciveCode method after the controller is properly initialized
//        smsVerificationController.reciveCode(123456, "55092584");

        // Set up the stage
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/iconLogo.png")));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Reset PWD");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}