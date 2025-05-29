package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class HomeController {

    // Layout principal
    @FXML private BorderPane mainBorderPane;
    @FXML private VBox defaultContent; // contenu d'accueil avec les cartes

    // Images
    @FXML private ImageView logoImage;
    @FXML private ImageView card1;
    @FXML private ImageView card2;
    @FXML private ImageView card3;
    @FXML private ImageView card4;

    // Labels de navigation
    @FXML private Label homeLabel;
    @FXML private Label accommodationLabel;
    @FXML private Label transportLabel;
    @FXML private Label expeditionLabel;
    @FXML private Label circuitLabel;
    @FXML private Label vehicleLabel;

    @FXML
    public void initialize() {
        // Chargement des images
        loadImage(logoImage, "/images/logo.png");
        loadImage(card1, "/images/plane.png");
        loadImage(card2, "/images/circuit.jpg");
        loadImage(card3, "/images/hotel.jpg");
        loadImage(card4, "/images/expedition.jpg");

        // Écouteurs de clic pour la navigation
        homeLabel.setOnMouseClicked(event -> mainBorderPane.setCenter(defaultContent));
        accommodationLabel.setOnMouseClicked(event -> navigateTo("#"));
        transportLabel.setOnMouseClicked(event -> navigateTo("#"));
        expeditionLabel.setOnMouseClicked(event -> navigateTo("#"));
        circuitLabel.setOnMouseClicked(event -> navigateTo("/fxml/CircuitManagementFXML/CircuitView.fxml"));
        vehicleLabel.setOnMouseClicked(event -> navigateTo("/fxml/CircuitManagementFXML/VehicleView.fxml"));
    }

    private void loadImage(ImageView imageView, String path) {
        try {
            var resource = getClass().getResource(path);
            if (resource != null) {
                imageView.setImage(new Image(resource.toExternalForm()));
            } else {
                System.err.println("Image not found: " + path);
            }
        } catch (Exception e) {
            System.err.println("Failed to load image: " + path);
            e.printStackTrace();
        }
    }

    private void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            mainBorderPane.setCenter(content); // On change uniquement le center
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
