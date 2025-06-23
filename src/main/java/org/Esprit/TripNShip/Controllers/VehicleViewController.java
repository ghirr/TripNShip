package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
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
        // Configuration du FlowPane premium
        vehicleContainer.setHgap(35);
        vehicleContainer.setVgap(35);
        vehicleContainer.setPadding(new Insets(20));
        vehicleContainer.setAlignment(Pos.CENTER);

        VehicleService vehicleService = new VehicleService();
        List<Vehicle> vehicles = vehicleService.getAll();

        for (Vehicle v : vehicles) {
            VBox card = createPremiumVehicleCard(v);
            vehicleContainer.getChildren().add(card);
        }
    }

    private VBox createPremiumVehicleCard(Vehicle vehicle) {
        VBox card = new VBox();
        card.setSpacing(18);
        card.setPrefWidth(320);
        card.setPrefHeight(480);
        card.setPadding(new Insets(25));
        card.setAlignment(Pos.TOP_CENTER);

        // Style de carte premium avec gradient violet
        card.setStyle("""
            -fx-background-color: linear-gradient(135deg, rgba(255,255,255,0.95) 0%, rgba(248,243,255,0.98) 100%);
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-border-color: rgba(121, 45, 153, 0.2);
            -fx-border-width: 2;
            -fx-effect: dropshadow(gaussian, rgba(121, 45, 153, 0.15), 20, 0.3, 0, 8);
        """);

        // Effet hover pour la carte
        card.setOnMouseEntered(e -> {
            card.setStyle("""
                -fx-background-color: linear-gradient(135deg, rgba(255,255,255,0.98) 0%, rgba(248,243,255,1.0) 100%);
                -fx-background-radius: 20;
                -fx-border-radius: 20;
                -fx-border-color: rgba(160, 82, 196, 0.4);
                -fx-border-width: 2;
                -fx-effect: dropshadow(gaussian, rgba(121, 45, 153, 0.25), 25, 0.4, 0, 12);
                -fx-scale-x: 1.02;
                -fx-scale-y: 1.02;
            """);
        });

        card.setOnMouseExited(e -> {
            card.setStyle("""
                -fx-background-color: linear-gradient(135deg, rgba(255,255,255,0.95) 0%, rgba(248,243,255,0.98) 100%);
                -fx-background-radius: 20;
                -fx-border-radius: 20;
                -fx-border-color: rgba(121, 45, 153, 0.2);
                -fx-border-width: 2;
                -fx-effect: dropshadow(gaussian, rgba(121, 45, 153, 0.15), 20, 0.3, 0, 8);
                -fx-scale-x: 1.0;
                -fx-scale-y: 1.0;
            """);
        });

        // Container pour l'image avec badge premium
        VBox imageContainer = new VBox();
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setSpacing(5);

        // Badge Premium en haut
        Label premiumBadge = new Label("✨ PREMIUM");
        premiumBadge.setStyle("""
            -fx-background-color: linear-gradient(45deg, #792d99, #a052c4);
            -fx-text-fill: white;
            -fx-padding: 6 15;
            -fx-background-radius: 15;
            -fx-font-size: 11px;
            -fx-font-weight: bold;
            -fx-effect: dropshadow(gaussian, rgba(121, 45, 153, 0.3), 8, 0.3, 0, 2);
        """);

        // Image du véhicule avec style amélioré
        ImageView imageView = new ImageView();
        imageView.setFitWidth(270);
        imageView.setFitHeight(170);
        imageView.setPreserveRatio(true);
        imageView.setStyle("""
            -fx-background-radius: 15;
            -fx-border-radius: 15;
            -fx-effect: dropshadow(gaussian, rgba(121, 45, 153, 0.1), 10, 0.2, 0, 3);
        """);

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

        imageContainer.getChildren().addAll(premiumBadge, imageView);

        // Nom du véhicule avec style premium
        Label vehicleName = new Label(vehicle.getBrand() + " " + vehicle.getModel());
        vehicleName.setStyle("""
            -fx-font-size: 20px;
            -fx-font-weight: bold;
            -fx-text-fill: #792d99;
            -fx-effect: dropshadow(gaussian, rgba(121, 45, 153, 0.2), 3, 0.2, 0, 1);
        """);

        // Plaque d'immatriculation en badge violet
        Label licenseBadge = new Label("🚗 " + vehicle.getLicensePlate());
        licenseBadge.setStyle("""
            -fx-background-color: rgba(121, 45, 153, 0.1);
            -fx-text-fill: #792d99;
            -fx-padding: 8 16;
            -fx-background-radius: 18;
            -fx-border-color: rgba(121, 45, 153, 0.3);
            -fx-border-width: 1;
            -fx-border-radius: 18;
            -fx-font-size: 13px;
            -fx-font-weight: bold;
        """);

        // Conteneur pour le badge (aligné au centre)
        HBox badgeContainer = new HBox();
        badgeContainer.setAlignment(Pos.CENTER);
        badgeContainer.getChildren().add(licenseBadge);

        // Section d'informations avec style premium
        VBox infoSection = new VBox(12);
        infoSection.setAlignment(Pos.CENTER_LEFT);
        infoSection.setStyle("""
            -fx-background-color: rgba(240, 230, 247, 0.3);
            -fx-background-radius: 12;
            -fx-padding: 15;
            -fx-border-color: rgba(121, 45, 153, 0.1);
            -fx-border-width: 1;
            -fx-border-radius: 12;
        """);

        // Informations du véhicule avec icônes premium
        HBox fuelInfo = createPremiumInfoLine("⛽", "Premium Gasoline");
        HBox brandInfo = createPremiumInfoLine("🏷️", vehicle.getBrand() + " Series");
        HBox transmissionInfo = createPremiumInfoLine("⚙️", "Premium Automatic");

        infoSection.getChildren().addAll(fuelInfo, brandInfo, transmissionInfo);

        // Prix avec style premium
        VBox priceContainer = new VBox(5);
        priceContainer.setAlignment(Pos.CENTER);

        Label priceLabel = new Label("$" + vehicle.getDailyPrice());
        priceLabel.setStyle("""
            -fx-font-size: 24px;
            -fx-font-weight: bold;
            -fx-text-fill: #792d99;
        """);

        Label periodLabel = new Label("per day");
        periodLabel.setStyle("""
            -fx-font-size: 14px;
            -fx-text-fill: #a052c4;
            -fx-font-weight: bold;
        """);

        priceContainer.getChildren().addAll(priceLabel, periodLabel);

        // Bouton de location premium avec gradient violet
        Button rentButton = new Button("🚀 Rent Premium Vehicle");
        rentButton.setPrefWidth(250);
        rentButton.setPrefHeight(45);
        rentButton.setStyle("""
            -fx-background-color: linear-gradient(45deg, #792d99, #a052c4);
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 15px;
            -fx-background-radius: 22;
            -fx-border-radius: 22;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, rgba(121, 45, 153, 0.3), 12, 0.4, 0, 4);
        """);

        // Effets hover pour le bouton premium
        rentButton.setOnMouseEntered(e -> {
            rentButton.setStyle("""
                -fx-background-color: linear-gradient(45deg, #5e1a70, #792d99);
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-font-size: 15px;
                -fx-background-radius: 22;
                -fx-border-radius: 22;
                -fx-cursor: hand;
                -fx-effect: dropshadow(gaussian, rgba(121, 45, 153, 0.4), 15, 0.5, 0, 6);
                -fx-scale-x: 1.05;
                -fx-scale-y: 1.05;
            """);
        });

        rentButton.setOnMouseExited(e -> {
            rentButton.setStyle("""
                -fx-background-color: linear-gradient(45deg, #792d99, #a052c4);
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-font-size: 15px;
                -fx-background-radius: 22;
                -fx-border-radius: 22;
                -fx-cursor: hand;
                -fx-effect: dropshadow(gaussian, rgba(121, 45, 153, 0.3), 12, 0.4, 0, 4);
                -fx-scale-x: 1.0;
                -fx-scale-y: 1.0;
            """);
        });

        rentButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/VehicleRentalForm.fxml"));
                Parent root = loader.load();

                VehicleRentalFormController controller = loader.getController();
                controller.setSelectedVehicle(vehicle);

                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setTitle("✨ Premium Vehicle Booking");
                popupStage.setScene(new Scene(root));
                popupStage.showAndWait();

            } catch (IOException ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to open premium booking form.");
                alert.setTitle("Booking Error");
                alert.setHeaderText("Premium Service Unavailable");
                alert.showAndWait();
            }
        });

        // Conteneur pour le bouton centré
        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().add(rentButton);

        // Assemblage de la carte premium
        card.getChildren().addAll(
                imageContainer,
                vehicleName,
                badgeContainer,
                infoSection,
                priceContainer,
                buttonContainer
        );

        return card;
    }

    private HBox createPremiumInfoLine(String icon, String text) {
        HBox infoLine = new HBox(12);
        infoLine.setAlignment(Pos.CENTER_LEFT);

        // Container pour l'icône avec background violet
        Label iconContainer = new Label(icon);
        iconContainer.setStyle("""
            -fx-font-size: 16px;
            -fx-background-color: rgba(121, 45, 153, 0.1);
 ddd           -fx-background-radius: 8;
            -fx-padding: 4 8;
            -fx-border-color: rgba(121, 45, 153, 0.2);
            -fx-border-width: 1;
            -fx-border-radius: 8;
        """);

        Label textLabel = new Label(text);
        textLabel.setStyle("""
            -fx-font-size: 14px;
            -fx-text-fill: #5e1a70;
            -fx-font-weight: bold;
        """);

        infoLine.getChildren().addAll(iconContainer, textLabel);
        return infoLine;
    }
}