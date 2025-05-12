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
    public void goToVehicle(MouseEvent event) throws IOException {

        Parent vehiclePage = FXMLLoader.load(getClass().getResource("/fxml/CircuitManagementFXML/VehicleListView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/iconLogo.png")));
        stage.setScene(new Scene(vehiclePage));
        stage.setTitle("Vehicle");
        stage.show();
    }

    @FXML
    public void goToTour(MouseEvent event) throws IOException {
        Parent tourPage = FXMLLoader.load(getClass().getResource("/fxml/CircuitManagementFXML/TourCircuitListView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/iconLogo.png")));
        stage.setScene(new Scene(tourPage));
        stage.setTitle("Tour Circuit");
        stage.show();
    }

    @FXML
    public void goToCircuit(MouseEvent event) throws IOException {
        Parent circuitPage = FXMLLoader.load(getClass().getResource("/fxml/CircuitManagementFXML/CircuitBookingListView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/iconLogo.png")));
        stage.setScene(new Scene(circuitPage));
        stage.setTitle("Circuit Booking");
        stage.show();
    }

    @FXML
    public void goToAgency(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/AgencyListView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);

        // Récupérer le Stage à partir de l'événement
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/iconLogo.png")));
        stage.setTitle("Rental Agency");
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    public void goToVehicleRental(MouseEvent event) throws IOException {
        Parent rentalPage = FXMLLoader.load(getClass().getResource("/fxml/CircuitManagementFXML/VehicleRentalListView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/iconLogo.png")));
        stage.setScene(new Scene(rentalPage));
        stage.setTitle("Vehicle Rental");
        stage.show();
    }
    }

