package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.Room;

import java.io.File;

public class RoomDetailsController {

    @FXML
    private ImageView accommodationImage;
    @FXML
    private Label accommodationNameLabel;
    @FXML
    private Label accommodationTypeLabel;
    @FXML
    private Label accommodationAddressLabel;

    @FXML
    private Label roomNameLabel;
    @FXML
    private Label roomTypeLabel;
    @FXML
    private Label availabilityLabel;
    // @FXML private WebView mapWebView; // Désactivé

    public void setRoom(Room room) {
        Accommodation accommodation = room.getAccommodation();

        // 🖼️ Image de l'hébergement
        String photoPath = accommodation.getPhotosAccommodation();

        if (photoPath != null && !photoPath.isEmpty()) {
            try {
                Image image;
                if (photoPath.startsWith("http://") || photoPath.startsWith("https://")) {
                    // ✅ Chargement depuis une URL distante
                    image = new Image(photoPath, true);
                } else {
                    // ✅ Chargement depuis un chemin local
                    File imageFile = new File(photoPath);
                    if (imageFile.exists()) {
                        image = new Image(imageFile.toURI().toString());
                    } else {
                        // ❌ Fichier local introuvable, utiliser image par défaut
                        image = new Image(getClass().getResource("/images/defaultAccommodation.png").toExternalForm());
                    }
                }
                accommodationImage.setImage(image);
            } catch (Exception e) {
                // ❌ En cas d'erreur (URL invalide, etc.)
                accommodationImage.setImage(new Image(getClass().getResource("/images/defaultAccommodation.png").toExternalForm()));
            }
        } else {
            // ❌ Aucun chemin fourni → image par défaut
            accommodationImage.setImage(new Image(getClass().getResource("/images/defaultAccommodation.png").toExternalForm()));
        }

        // 🏷️ Texte des labels
        accommodationNameLabel.setText("Accommodation Name: " + accommodation.getName());
        accommodationTypeLabel.setText("Type: " + accommodation.getType());
        accommodationAddressLabel.setText("Address: " + accommodation.getAddress());

        roomNameLabel.setText("Room Name: " + room.getNameRoom());
        roomTypeLabel.setText("Room Type: " + room.getType().toString());
        availabilityLabel.setText("Available: " + (room.isAvailability() ? "Yes" : "No"));
    }
}

