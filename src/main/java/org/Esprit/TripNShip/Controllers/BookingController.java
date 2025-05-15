package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class BookingController {

    @FXML
    private GridPane gridPane;

    @FXML
    public void initialize() {
        // Tu peux parcourir les enfants du GridPane (les VBox contenant les images et boutons)
        for (var node : gridPane.getChildren()) {
            if (node instanceof VBox box) {
                Button viewHotelBtn = (Button) box.getChildren().get(1);
                Button detailBtn = (Button) box.getChildren().get(2);
                Button bookBtn = (Button) box.getChildren().get(3);
                ImageView imageView = (ImageView) box.getChildren().get(0);

                // Exemple d'action pour chaque bouton
                viewHotelBtn.setOnAction(event -> showAlert("View Hotel", "You clicked 'View Hotel' for image."));
                detailBtn.setOnAction(event -> showAlert("Details", "You clicked 'Details' for image."));
                bookBtn.setOnAction(event -> showAlert("Add Booking", "You clicked 'Add Booking' for image."));
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
