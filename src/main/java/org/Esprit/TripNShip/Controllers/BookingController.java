package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Room;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BookingController {

    @FXML
    private ImageView roomImageView;

    @FXML
    private Label roomNameLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Button detailsButton;

    @FXML
    private Button addBookingButton;
    @FXML
    private VBox roomContainer;

    private Room room;

    // Nouvelle liste d'URLs photos (ex: de Unsplash)
    private String photoUrls;

    public void setRoom(Room room) {
        this.room = room;
        this.photoUrls = room.getPhotosRoom();// par exemple, liste des URLs dans ta classe Room
        updateRoomCard();
    }

    private void updateRoomCard() {

        if (room == null) return;

        roomNameLabel.setText(room.getNameRoom());
        priceLabel.setText(String.format("%.2f DT", room.getPrice()));

        // Cherche la première photo valide parmi les URLs et fichiers locaux
        String firstPhoto = photoUrls;

        if (firstPhoto != null) {
            try {
                Image image;
                if (isUrl(firstPhoto)) {
                    image = new Image(firstPhoto, true);
                } else {
                    image = new Image(new File(firstPhoto).toURI().toString());
                }
                roomImageView.setImage(image);
            } catch (Exception e) {
                System.err.println("Erreur chargement image : " + e.getMessage());
                setDefaultImage();
            }
        } else {
            setDefaultImage();
        }

        detailsButton.setOnAction(e -> showDetails());
        addBookingButton.setOnAction(e -> addBooking());
    }

    private boolean isUrl(String path) {
        return path != null && (path.startsWith("http://") || path.startsWith("https://"));
    }

    // Si tu veux récupérer dans plusieurs dossiers, ou URLs, cette méthode reste générique
    private String getFirstValidPhotoPathOrUrl(List<String> photoPaths) {
        if (photoPaths != null) {
            for (String path : photoPaths) {
                if (isUrl(path)) {
                    System.out.println("✅ Image URL détectée : " + path);
                    return path;
                } else {
                    File file = new File(path);
                    if (file.exists()) {
                        System.out.println("✅ Fichier image local trouvé : " + file.getAbsolutePath());
                        return file.getAbsolutePath();
                    } else {
                        System.out.println("❌ Fichier introuvable : " + path);
                    }
                }
            }
        }

        // Recherche dans les dossiers par défaut
        String[] folders = {
                "C:\\Users\\YJAZIRI\\Desktop\\AllRoomImage",


        };

        for (String folderPath : folders) {
            File folder = new File(folderPath);
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles((dir, name) -> {
                    String lower = name.toLowerCase();
                    return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg");
                });

                if (files != null && files.length > 0) {
                    System.out.println("✅ Image trouvée par défaut : " + files[0].getAbsolutePath());
                    return files[0].getAbsolutePath();
                }
            }
        }

        System.out.println("❌ Aucune image trouvée.");
        return null;
    }


    private void setDefaultImage() {
        try {
            roomImageView.setImage(new Image(getClass().getResource("/images/no-image.png").toExternalForm()));
        } catch (Exception e) {
            System.err.println("Erreur chargement image par défaut: " + e.getMessage());
        }
    }

    @FXML
    private void showDetails() {
        System.out.println("📌 Show details for room: " + (room != null ? room.getNameRoom() : "Unknown"));
    }

    @FXML
    private void addBooking() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagementFXML/AccommodationBooking.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Booking");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
