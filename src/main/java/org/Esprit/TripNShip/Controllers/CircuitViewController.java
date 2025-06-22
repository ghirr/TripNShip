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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.TourCircuit;
import org.Esprit.TripNShip.Services.TourCircuitService;

import java.io.IOException;
import java.util.List;

public class CircuitViewController {

    @FXML
    private FlowPane circuitContainer;

    @FXML
    public void initialize() {
        // Configuration du FlowPane premium
        circuitContainer.setHgap(35);
        circuitContainer.setVgap(35);
        circuitContainer.setPadding(new Insets(20));
        circuitContainer.setAlignment(Pos.CENTER);

        TourCircuitService tourCircuitService = new TourCircuitService();
        List<TourCircuit> circuits = tourCircuitService.getAll();

        for (TourCircuit c : circuits) {
            VBox card = createPremiumCircuitCard(c);
            circuitContainer.getChildren().add(card);
        }
    }

    private VBox createPremiumCircuitCard(TourCircuit circuit) {
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
        Label premiumBadge = new Label("✨ PREMIUM TOUR");
        premiumBadge.setStyle("""
            -fx-background-color: linear-gradient(45deg, #792d99, #a052c4);
            -fx-text-fill: white;
            -fx-padding: 6 15;
            -fx-background-radius: 15;
            -fx-font-size: 11px;
            -fx-font-weight: bold;
            -fx-effect: dropshadow(gaussian, rgba(121, 45, 153, 0.3), 8, 0.3, 0, 2);
        """);

        // Placeholder pour l'image du circuit avec style amélioré
        VBox circuitImage = new VBox();
        circuitImage.setPrefWidth(270);
        circuitImage.setPrefHeight(170);
        circuitImage.setAlignment(Pos.CENTER);
        circuitImage.setStyle("""
            -fx-background-color: linear-gradient(135deg, #f8f3ff 0%, #e8dcf0 100%);
            -fx-background-radius: 15;
            -fx-border-radius: 15;
            -fx-border-color: rgba(121, 45, 153, 0.1);
            -fx-border-width: 1;
            -fx-effect: dropshadow(gaussian, rgba(121, 45, 153, 0.1), 10, 0.2, 0, 3);
        """);

        // Icône de destination
        Label destinationIcon = new Label("🌍");
        destinationIcon.setStyle("-fx-font-size: 48px;");
        circuitImage.getChildren().add(destinationIcon);

        imageContainer.getChildren().addAll(premiumBadge, circuitImage);

        // Nom du circuit avec style premium
        Label circuitName = new Label(circuit.getNameCircuit());
        circuitName.setStyle("""
            -fx-font-size: 20px;
            -fx-font-weight: bold;
            -fx-text-fill: #792d99;
            -fx-effect: dropshadow(gaussian, rgba(121, 45, 153, 0.2), 3, 0.2, 0, 1);
            -fx-text-alignment: center;
            -fx-wrap-text: true;
        """);

        // Destination en badge violet
        Label destinationBadge = new Label("📍 " + circuit.getDestination());
        destinationBadge.setStyle("""
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
        badgeContainer.getChildren().add(destinationBadge);

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

        // Informations du circuit avec icônes premium
        HBox durationInfo = createPremiumInfoLine("⏰", circuit.getDuration() + " Days");
        HBox typeInfo = createPremiumInfoLine("🎯", "Premium Adventure");
        HBox serviceInfo = createPremiumInfoLine("⭐", "5-Star Experience");

        infoSection.getChildren().addAll(durationInfo, typeInfo, serviceInfo);

        // Prix avec style premium
        VBox priceContainer = new VBox(5);
        priceContainer.setAlignment(Pos.CENTER);

        Label priceLabel = new Label("$" + circuit.getPriceCircuit());
        priceLabel.setStyle("""
            -fx-font-size: 24px;
            -fx-font-weight: bold;
            -fx-text-fill: #792d99;
        """);

        Label periodLabel = new Label("per person");
        periodLabel.setStyle("""
            -fx-font-size: 14px;
            -fx-text-fill: #a052c4;
            -fx-font-weight: bold;
        """);

        priceContainer.getChildren().addAll(priceLabel, periodLabel);

        // Bouton de réservation premium avec gradient violet
        Button bookButton = new Button("🚀 Book Premium Tour");
        bookButton.setPrefWidth(250);
        bookButton.setPrefHeight(45);
        bookButton.setStyle("""
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
        bookButton.setOnMouseEntered(e -> {
            bookButton.setStyle("""
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

        bookButton.setOnMouseExited(e -> {
            bookButton.setStyle("""
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

        bookButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/CircuitBookingForm.fxml"));
                Parent root = loader.load();

                CircuitBookingController controller = loader.getController();
                controller.setCircuit(circuit);

                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setTitle("✨ Premium Tour Booking");
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
        buttonContainer.getChildren().add(bookButton);

        // Assemblage de la carte premium
        card.getChildren().addAll(
                imageContainer,
                circuitName,
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
            -fx-background-radius: 8;
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

    public void showPopup(String fxmlPath, Object controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (controller != null) {
                loader.setController(controller);
            }
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("✨ Premium Tour Booking");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to open premium booking form.");
            alert.setTitle("Booking Error");
            alert.setHeaderText("Premium Service Unavailable");
            alert.showAndWait();
        }
    }
}