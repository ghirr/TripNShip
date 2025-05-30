package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
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
        TourCircuitService tourCircuitService = new TourCircuitService();
        List<TourCircuit> circuits = tourCircuitService.getAll();

        for (TourCircuit c : circuits) {
            VBox card = createCircuitCard(c);
            circuitContainer.getChildren().add(card);
        }
    }

    private VBox createCircuitCard(TourCircuit circuit) {
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

        // Infos
        Label nameLabel = new Label(circuit.getNameCircuit());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label destinationLabel = new Label("Destination: " + circuit.getDestination());
        destinationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        Label durationLabel = new Label("Duration: " + circuit.getDuration());
        durationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        Label priceLabel = new Label("Price: $" + circuit.getPriceCircuit());
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888;");

        // Boutons
        Button bookButton = new Button("Book");


        bookButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-background-radius: 6;");

        bookButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/CircuitBookingForm.fxml"));
                Parent root = loader.load();

                CircuitBookingController controller = loader.getController();
                controller.setCircuit(circuit); // <-- on passe l'objet circuit ici

                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setTitle("Add Circuit Booking");
                popupStage.setScene(new Scene(root));
                popupStage.showAndWait();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });



        VBox buttonBox = new VBox(6, bookButton);
        buttonBox.setStyle("-fx-alignment: center;");

        card.getChildren().addAll(nameLabel, destinationLabel, durationLabel, priceLabel, buttonBox);
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
            popupStage.setTitle("Add Circuit Booking");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
