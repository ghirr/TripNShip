package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Entities.TypeRoom;
import org.Esprit.TripNShip.Services.AccommodationService;
import org.Esprit.TripNShip.Services.RoomService;

import java.util.List;

public class RoomController {

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private ChoiceBox<String> accommodationTypeChoiceBox;
    @FXML private ChoiceBox<String> typeRoomChoiceBox;
    @FXML private ChoiceBox<String> availabilityChoiceBox;
    @FXML private Button addRoomButton;

    private Room room;
    private final RoomService roomService = new RoomService();
    private final AccommodationService accommodationService = new AccommodationService();
    private List<Accommodation> accommodations;

    public void setRoom(Room room) {
        this.room = room;
        if (room != null) {
            nameField.setText(room.getNameRoom());
            priceField.setText(String.valueOf(room.getPrice()));
            typeRoomChoiceBox.setValue(room.getType().name());
            availabilityChoiceBox.setValue(room.isAvailability() ? "AVAILABLE" : "UNAVAILABLE");

            Accommodation selectedAccommodation = accommodations.stream()
                    .filter(acc -> acc.getIdAccommodation() == room.getAccommodation().getIdAccommodation())
                    .findFirst()
                    .orElse(null);

            if (selectedAccommodation != null) {
                String displayText = selectedAccommodation.getType().name() + " - " + selectedAccommodation.getName();
                accommodationTypeChoiceBox.setValue(displayText);
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
        typeRoomChoiceBox.setValue("SINGLE");

        availabilityChoiceBox.setItems(FXCollections.observableArrayList("AVAILABLE", "UNAVAILABLE"));
        availabilityChoiceBox.setValue("AVAILABLE");
    }

    @FXML
    public void saveRoom(ActionEvent actionEvent) {
        // Contrôle de saisie
        if (nameField.getText().isEmpty() ||
                priceField.getText().isEmpty() ||
                typeRoomChoiceBox.getValue() == null ||
                availabilityChoiceBox.getValue() == null ||
                accommodationTypeChoiceBox.getValue() == null) {

            showAlert(Alert.AlertType.ERROR, "All fields must be filled.");
            return;
        }

        float price;
        try {
            price = Float.parseFloat(priceField.getText());
            if (price < 0) throw new NumberFormatException(); // Optionnel
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid number format for price.");
            return;
        }

        TypeRoom typeRoomEnum;
        try {
            typeRoomEnum = TypeRoom.valueOf(typeRoomChoiceBox.getValue());
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid room type selected.");
            return;
        }

        boolean availability = "AVAILABLE".equals(availabilityChoiceBox.getValue());

        String selectedAccommodationText = accommodationTypeChoiceBox.getValue();
        Accommodation selectedAccommodation = accommodations.stream()
                .filter(acc -> (acc.getType().name() + " - " + acc.getName()).equals(selectedAccommodationText))
                .findFirst()
                .orElse(null);

        if (selectedAccommodation == null) {
            showAlert(Alert.AlertType.ERROR, "Please select a valid Accommodation.");
            return;
        }

        if (room == null) {
            room = new Room();
        }

        room.setNameRoom(nameField.getText());
        room.setType(typeRoomEnum);
        room.setPrice(price);
        room.setAvailability(availability);
        room.setAccommodation(selectedAccommodation);

        if (room.getIdRoom() == 0) {
            roomService.add(room);
            showAlert(Alert.AlertType.INFORMATION, "Room added successfully!");
        } else {
            roomService.update(room);
            showAlert(Alert.AlertType.INFORMATION, "Room updated successfully!");
        }

        closeWindow();
    }

    private void closeWindow() {
        ((Stage) priceField.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Room");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setRoomToEdit(Room room) {
        setRoom(room);
    }
}
