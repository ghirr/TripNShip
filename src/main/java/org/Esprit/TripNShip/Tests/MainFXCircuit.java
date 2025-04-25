package org.Esprit.TripNShip.Tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainFXCircuit extends Application {



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/iconLogo.png")));
       // FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/CircuitBookingListView.fxml"));
     // FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/AgencyListView.fxml"));
    // FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/TourCircuitListView.fxml"));
         FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/VehicleListView.fxml"));
       // FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/VehicleRentalListView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Add circuit");
        stage.setScene(scene);
        stage.show();
    }
}