package org.Esprit.TripNShip.Controllers;

import com.google.api.client.googleapis.json.GoogleJsonError;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Room;

import java.io.IOException;

public class RoomCardController {

    @FXML
    private ImageView roomImageView;

    @FXML
    private Label roomNameLabel;

    @FXML
    private Label roomPriceLabel;

    @FXML
    public Button detailButton;

    @FXML
    public Button bookingButton;

    @FXML
    public Button editBookingButton;

    private Room room;

    @FXML
    private void initialize() {
        bookingButton.setOnAction(e -> openBookingWindow());
       detailButton.setOnAction(e -> openRoomDetailsWindow());
    }


    public void openBookingWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagementFXML/AccommodationBooking.fxml"));
            Parent root = loader.load();

            // ✅ Passer la Room sélectionnée
            AccommodationBookingController controller = loader.getController();
            controller.setRoom(this.room);  // <-- important

            Stage stage = new Stage();
            stage.setTitle("Réserver cette chambre");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement de la réservation.").showAndWait();
        }
    }

    public void setRoom(Room room) {
        this.room = room;
        roomNameLabel.setText(room.getNameRoom());
        roomPriceLabel.setText(room.getPrice() + " DT");

        // Charger l'image si elle existe
        if (room.getPhotosRoom() != null && !room.getPhotosRoom().isEmpty()) {
            try {
                Image image = new Image(room.getPhotosRoom(), true);
                roomImageView.setImage(image);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
            }
        }
    }

    public Room getRoom() {
        return room;
    }



    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void openRoomDetailsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagementFXML/RoomDetails.fxml"));
            Parent root = loader.load();

            RoomDetailsController controller = loader.getController();
            controller.setRoom(this.room);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Room Details - " + room.getNameRoom());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

