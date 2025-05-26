package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Entities.TypeRoom;
import org.Esprit.TripNShip.Services.AccommodationService;
import org.Esprit.TripNShip.Services.RoomService;
import org.Esprit.TripNShip.Utils.Shared;

import java.io.File;
import java.util.List;

public class EditRoomController {

    @FXML private ImageView roomImageView;
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private ChoiceBox<String> typeRoomChoiceBox;
    @FXML private ChoiceBox<String> availabilityChoiceBox;
    @FXML private ChoiceBox<String> accommodationTypeChoiceBox;

    private Room room;
    private final RoomService roomService = new RoomService();
    private final AccommodationService accommodationService = new AccommodationService();
    private List<Accommodation> accommodations;
    private File selectedPhoto;

    public void setRoom(Room room) {
        this.room = room;

        // Mise à jour des champs texte et choix
        nameField.setText(room.getNameRoom());
        priceField.setText(String.valueOf(room.getPrice()));
        typeRoomChoiceBox.setValue(room.getType().name());
        availabilityChoiceBox.setValue(room.isAvailability() ? "AVAILABLE" : "UNAVAILABLE");

        // Mise à jour du choix de l'hébergement
        if (accommodations != null && room.getAccommodation() != null) {
            Accommodation selectedAccommodation = accommodations.stream()
                    .filter(acc -> acc.getIdAccommodation() == room.getAccommodation().getIdAccommodation())
                    .findFirst()
                    .orElse(null);

            if (selectedAccommodation != null) {
                accommodationTypeChoiceBox.setValue(
                        selectedAccommodation.getType().name() + " - " + selectedAccommodation.getName()
                );
            }
        }

        // Gestion de l'affichage de la photo dans un ImageView roomImageView (déclaré en @FXML)
        if (room.getPhotosRoom() != null && !room.getPhotosRoom().isEmpty()) {
            String photoPath = room.getPhotosRoom();
            Image image;
            try {
                if (photoPath.startsWith("http://") || photoPath.startsWith("https://")) {
                    // Charger depuis URL
                    image = new Image(photoPath, true);
                } else {
                    // Charger depuis fichier local
                    File file = new File(photoPath);
                    if (file.exists()) {
                        image = new Image(file.toURI().toString());
                    } else {
                        // Image par défaut si fichier non trouvé
                        image = new Image(getClass().getResourceAsStream("/images/default-room.png"));
                    }
                }
            } catch (Exception e) {
                // En cas d'erreur, image par défaut
                image = new Image(getClass().getResourceAsStream("/images/default-room.png"));
            }
            roomImageView.setImage(image);
        } else {
            // Pas de photo : afficher image par défaut
            roomImageView.setImage(new Image(getClass().getResourceAsStream("/images/default-room.png")));
        }
    }


    @FXML
    private void initialize() {
        accommodations = accommodationService.getAll();

        accommodationTypeChoiceBox.setItems(FXCollections.observableArrayList(
                accommodations.stream()
                        .map(acc -> acc.getType().name() + " - " + acc.getName())
                        .toList()
        ));

        typeRoomChoiceBox.setItems(FXCollections.observableArrayList("SINGLE", "DOUBLE", "SUITE"));
        availabilityChoiceBox.setItems(FXCollections.observableArrayList("AVAILABLE", "UNAVAILABLE"));
    }

    @FXML
    private void updateRoom() {
        try {
            room.setNameRoom(nameField.getText());
            room.setType(TypeRoom.valueOf(typeRoomChoiceBox.getValue()));
            room.setPrice(Float.parseFloat(priceField.getText()));
            room.setAvailability("AVAILABLE".equals(availabilityChoiceBox.getValue()));

            String selectedText = accommodationTypeChoiceBox.getValue();
            Accommodation selectedAccommodation = accommodations.stream()
                    .filter(acc -> (acc.getType().name() + " - " + acc.getName()).equals(selectedText))
                    .findFirst()
                    .orElse(null);

            if (selectedAccommodation == null) {
                showError("Accommodation selection is invalid.");
                return;
            }

            // 🔁 Gérer la photo : copier dans dossier ROOMS_PATH
            if (selectedPhoto != null) {
                String uploadedPath = Shared.uploadProfilePhoto(selectedPhoto, Shared.ROOMS_PATH);
                room.setPhotosRoom(uploadedPath);
            }

            room.setAccommodation(selectedAccommodation);
            roomService.update(room);

            TableRoomController controller = TableRoomController.getInstance();
            if (controller != null) {
                controller.loadRoomsFromDatabase();
            }

            showInfo("Room updated successfully!");
            closeWindow();

        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    @FXML
    private void choosePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Room Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) nameField.getScene().getWindow();
        selectedPhoto = fileChooser.showOpenDialog(stage);

        if (selectedPhoto != null) {
            try {
                Image image = new Image(selectedPhoto.toURI().toString());
                roomImageView.setImage(image);
            } catch (Exception e) {
                showError("Could not load the selected image.");
            }
        }
    }

    private void closeWindow() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
