package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class sidebarController {

    @FXML
    public void goToDashboard(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/Dashboard.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/iconLogo.png")));
        stage.setTitle("Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void goToVehicle(MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/VehicleListView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/iconLogo.png")));
        stage.setTitle("Vehicle");
        stage.setScene(scene);
        stage.show();

    }

    @FXML
    public void goToTour(MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/TourCircuitListView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/iconLogo.png")));
        stage.setTitle("Tour Circuit");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void goToCircuit(MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/CircuitBookingListView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/iconLogo.png")));
        stage.setTitle("Circuit Booking");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void goToAgency(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/AgencyListView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/iconLogo.png")));
        stage.setTitle("Rental Agency");
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    public void goToVehicleRental(MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/VehicleRentalListView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/iconLogo.png")));
        stage.setTitle("Vehicle Rental");
        stage.setScene(scene);
        stage.show();
    }
    }

