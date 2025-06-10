package org.Esprit.TripNShip.Controllers;

import com.gluonhq.maps.MapView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Utils.MapController;

import java.io.File;

public class RoomDetailsController {

    @FXML private ImageView accommodationImage;
    @FXML private Label accommodationNameLabel;
    @FXML private Label accommodationTypeLabel;
    @FXML private Label accommodationAddressLabel;
    @FXML private Label roomNameLabel;
    @FXML private Label roomTypeLabel;
    @FXML private Label availabilityLabel;
    @FXML private MapView mapView;

    private MapController mapController;

    @FXML
    private void initialize() {
        mapController = new MapController(mapView);
    }

    public void setRoom(Room room) {
        Accommodation accommodation = room.getAccommodation();

        // 🖼️ Load accommodation image
        String photoPath = accommodation.getPhotosAccommodation();
        if (photoPath != null && !photoPath.isEmpty()) {
            try {
                Image image;
                if (photoPath.startsWith("http")) {
                    image = new Image(photoPath, true);
                } else {
                    File file = new File(photoPath);
                    image = file.exists()
                            ? new Image(file.toURI().toString())
                            : new Image(getClass().getResource("/images/defaultAccommodation.png").toExternalForm());
                }
                accommodationImage.setImage(image);
            } catch (Exception e) {
                accommodationImage.setImage(new Image(getClass().getResource("/images/defaultAccommodation.png").toExternalForm()));
            }
        } else {
            accommodationImage.setImage(new Image(getClass().getResource("/images/defaultAccommodation.png").toExternalForm()));
        }

        // 🏷️ Labels
        accommodationNameLabel.setText("Accommodation Name: " + accommodation.getName());
        accommodationTypeLabel.setText("Type: " + accommodation.getType());
        accommodationAddressLabel.setText("Address: " + accommodation.getAddress());

        roomNameLabel.setText("Room Name: " + room.getNameRoom());
        roomTypeLabel.setText("Room Type: " + room.getType());
        availabilityLabel.setText("Available: " + (room.isAvailability() ? "Yes" : "No"));

        // 🗺️ Show location on map
        if (accommodation.getLatitude() != 0 && accommodation.getLongitude() != 0) {
            mapController.showAccommodationOnMap(
                    accommodation.getLatitude(),
                    accommodation.getLongitude(),
                    accommodation.getName(),
                    accommodation.getAddress()
            );
        }
    }
}
