package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Entities.TypeRoom;
import org.Esprit.TripNShip.Services.AccommodationService;
import org.Esprit.TripNShip.Services.RoomService;

import java.util.List;

public class EditRoomController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    @FXML
    private ChoiceBox<String> typeRoomChoiceBox;

    @FXML
    private ChoiceBox<String> availabilityChoiceBox;

    @FXML
    private ChoiceBox<String> accommodationTypeChoiceBox;

    private Room room;

    private final RoomService roomService = new RoomService();
    private final AccommodationService accommodationService = new AccommodationService();
    private List<Accommodation> accommodations;

    public void setRoom(Room room) {
        this.room = room;

        // Charger les valeurs dans les champs
        nameField.setText(room.getNameRoom());
        priceField.setText(String.valueOf(room.getPrice()));
        typeRoomChoiceBox.setValue(room.getType().name());
        availabilityChoiceBox.setValue(room.isAvailability() ? "AVAILABLE" : "UNAVAILABLE");

        // Charger l’hébergement sélectionné
        if (accommodations != null) {
            Accommodation selectedAccommodation = accommodations.stream()
                    .filter(acc -> acc.getIdAccommodation() == room.getAccommodation().getIdAccommodation())
                    .findFirst()
                    .orElse(null);

            if (selectedAccommodation != null) {
                accommodationTypeChoiceBox.setValue(selectedAccommodation.getType().name() + " - " + selectedAccommodation.getName());
            }
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

            room.setAccommodation(selectedAccommodation);

            roomService.update(room);

            // 👉 Rafraîchir la table si le contrôleur Room est actif
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
