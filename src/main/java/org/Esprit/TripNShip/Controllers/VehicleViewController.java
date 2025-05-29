package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Vehicle;
import org.Esprit.TripNShip.Services.VehicleService;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class VehicleViewController {

    @FXML
    private FlowPane vehicleContainer;

    @FXML
    public void initialize() {
        VehicleService vehicleService = new VehicleService();
        List<Vehicle> vehicles = vehicleService.getAll();

        for (Vehicle v : vehicles) {
            VBox card = createVehicleCard(v);
            vehicleContainer.getChildren().add(card);
        }
    }

    private VBox createVehicleCard(Vehicle vehicle) {
        VBox card = new VBox(10);
        card.setPrefWidth(220);
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-padding: 15; -fx-alignment: center; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        // Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(120);

        try {
            String imageURL = vehicle.getImageURL();
            if (imageURL != null && new File(new java.net.URI(imageURL)).exists()) {
                imageView.setImage(new Image(imageURL));
            } else {
                imageView.setImage(new Image(getClass().getResource("/images/icons8-car-rental-64.png").toExternalForm()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            imageView.setImage(new Image(getClass().getResource("/images/icons8-car-rental-64.png").toExternalForm()));
        }

        // Infos
        Label brand = new Label(vehicle.getBrand());
        Label nameLabel = new Label(vehicle.getModel());
        Label priceLabel = new Label("Price: $" + vehicle.getDailyPrice());

        // Boutons
        Button bookButton = new Button("Book");
        bookButton.setOnAction(e -> {
            showPopup("/fxml/CircuitManagementFXML/AddVehicleRental.fxml", null);
        });



        Button detailsButton = new Button("Details");
        detailsButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Vehicle Details");
            alert.setHeaderText(vehicle.getModel());
            alert.setContentText(vehicle.toString());
            alert.showAndWait();
        });

        card.getChildren().addAll(imageView, brand, nameLabel, priceLabel, bookButton, detailsButton);
        return card;
    }

    public void showPopup(String fxmlPath, Object controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (controller != null) {
                loader.setController(controller);
            }
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Add Vehicle Rental");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
