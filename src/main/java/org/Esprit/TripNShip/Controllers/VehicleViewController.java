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
import java.net.URI;
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
        VBox card = new VBox(12);
        card.setPrefWidth(240);
        card.setStyle("""
            -fx-background-color: #ffffff;
            -fx-border-color: #e0e0e0;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            -fx-padding: 16;
            -fx-alignment: center;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 4);
        """);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(130);
        imageView.setPreserveRatio(true);

        try {
            String imageURL = vehicle.getImageURL();
            if (imageURL != null && !imageURL.isBlank()) {
                URI uri = new URI(imageURL);
                File file = new File(uri);
                if (file.exists()) {
                    imageView.setImage(new Image(imageURL));
                } else {
                    imageView.setImage(new Image(getClass().getResource("/images/icons8-car-rental-64.png").toExternalForm()));
                }
            } else {
                imageView.setImage(new Image(getClass().getResource("/images/icons8-car-rental-64.png").toExternalForm()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            imageView.setImage(new Image(getClass().getResource("/images/icons8-car-rental-64.png").toExternalForm()));
        }

        Label brand = new Label(vehicle.getBrand());
        brand.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label model = new Label(vehicle.getModel());
        model.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        Label priceLabel = new Label("Price: $" + vehicle.getDailyPrice());
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888;");

        Button bookButton = new Button("Book");
        bookButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 6;");

        bookButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/VehicleRentalForm.fxml"));
                Parent root = loader.load();

                VehicleRentalFormController controller = loader.getController();
                controller.setSelectedVehicle(vehicle);

                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setTitle("Book Vehicle");
                popupStage.setScene(new Scene(root));
                popupStage.showAndWait();

            } catch (IOException ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to open booking form.");
                alert.showAndWait();
            }
        });

        VBox buttonBox = new VBox(bookButton);
        buttonBox.setStyle("-fx-alignment: center;");

        card.getChildren().addAll(imageView, brand, model, priceLabel, buttonBox);
        return card;
    }
}
