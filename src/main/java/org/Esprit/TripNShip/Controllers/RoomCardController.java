package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.Esprit.TripNShip.Entities.Room;

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

    public void setRoom(Room room) {
        this.room = room;
        roomNameLabel.setText(room.getNameRoom());
        roomPriceLabel.setText(room.getPrice() + " DT");

        if (room.getPhotosRoom() != null && !room.getPhotosRoom().isEmpty()) {
            Image image = new Image(room.getPhotosRoom());
            roomImageView.setImage(image);
        }
    }

    public Room getRoom() {
        return room;
    }
}
