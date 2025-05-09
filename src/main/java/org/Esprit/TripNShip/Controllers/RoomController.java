package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Entities.TypeRoom;
import org.Esprit.TripNShip.Services.AccommodationService;
import org.Esprit.TripNShip.Services.RoomService;

import java.util.List;

public class RoomController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    @FXML
    private ChoiceBox<String> accommodationTypeChoiceBox;

    @FXML
    private ChoiceBox<String> typeRoomChoiceBox;

    @FXML
    private ChoiceBox<String> availabilityChoiceBox;

    @FXML
    private TableColumn<Room, String> colAccommodation;

    @FXML
    private Button addRoomButton;

    private Room room;

    private final RoomService roomService = new RoomService();
    private final AccommodationService accommodationService = new AccommodationService();
    private List<Accommodation> accommodations; // Pour récupérer idAccommodation à partir du choix

    public void setRoom(Room room) {
        this.room = room;
        if (room != null) {
            nameField.setText(room.getNameRoom());
            priceField.setText(String.valueOf(room.getPrice()));
            typeRoomChoiceBox.setValue(room.getType().name());
            availabilityChoiceBox.setValue(room.isAvailability() ? "AVAILABLE" : "UNAVAILABLE");

            // Sélectionner le bon hébergement
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
        // Charger les hébergements
        accommodations = accommodationService.getAll();
        //colAccommodation.setCellValueFactory(new PropertyValueFactory<>("accommodationType"));
        //colAccommodation.setCellValueFactory(new PropertyValueFactory<>("accommodationName"));


        // Remplir accommodationTypeChoiceBox
        accommodationTypeChoiceBox.setItems(FXCollections.observableArrayList(
                accommodations.stream()
                        .map(acc -> acc.getType().name() + " - " + acc.getName())
                        .toList()
        ));

        // Remplir typeRoomChoiceBox
        typeRoomChoiceBox.setItems(FXCollections.observableArrayList("SINGLE", "DOUBLE", "SUITE"));
        typeRoomChoiceBox.setValue("SINGLE");

        // Remplir availabilityChoiceBox
        availabilityChoiceBox.setItems(FXCollections.observableArrayList("AVAILABLE", "UNAVAILABLE"));
        availabilityChoiceBox.setValue("AVAILABLE");
    }

    private void closeWindow() {
        ((Stage) priceField.getScene().getWindow()).close();
    }

    @FXML
    public void saveRoom(ActionEvent actionEvent) {
        String nameRoom = nameField.getText();
        String typeRoomValue = typeRoomChoiceBox.getValue();
        TypeRoom typeRoomEnum = TypeRoom.valueOf(typeRoomValue);
        float price = Float.parseFloat(priceField.getText());
        boolean availability = "AVAILABLE".equals(availabilityChoiceBox.getValue());

        // Trouver l'Accommodation sélectionné
        String selectedAccommodationText = accommodationTypeChoiceBox.getValue();
        Accommodation selectedAccommodation = accommodations.stream()
                .filter(acc -> (acc.getType().name() + " - " + acc.getName()).equals(selectedAccommodationText))
                .findFirst()
                .orElse(null);

        if (selectedAccommodation == null) {
            showErrorMessage("Please select a valid Accommodation.");
            return;
        }

        if (room == null) {
            room = new Room();
        }

        room.setNameRoom(nameRoom);
        room.setType(typeRoomEnum);
        room.setPrice(price);
        room.setAvailability(availability);
        room.setAccommodation(selectedAccommodation);


        if (room.getIdRoom() == 0) {
            roomService.add(room);
            showSuccessMessage("Room added successfully!");
        } else {
            roomService.update(room);
            showSuccessMessage("Room updated successfully!");
        }

        closeWindow();
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setRoomToEdit(Room room) {
        setRoom(room);
    }
}
