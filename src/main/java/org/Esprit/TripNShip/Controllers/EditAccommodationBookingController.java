package org.Esprit.TripNShip.Controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.AccommodationBooking;
import org.Esprit.TripNShip.Entities.BookingStatus;
import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Entities.TypeRoom;
import org.Esprit.TripNShip.Services.AccommodationBookingService;
import org.Esprit.TripNShip.Services.RoomService;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class EditAccommodationBookingController {

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<String> comboRoom;

    @FXML
    private Button updateButton;
    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<TypeRoom> typeRoomComboBox;



    private final RoomService roomService = new RoomService();
    private final AccommodationBookingService bookingService = new AccommodationBookingService();
    private List<Room> rooms;

    private AccommodationBooking booking;

    public void setBooking(AccommodationBooking booking) {
        this.booking = booking;

        // Charger les chambres une seule fois
        if (rooms == null || rooms.isEmpty()) {
            loadRooms();
        }

        // Préremplir les champs
        startDatePicker.setValue(((java.sql.Date) booking.getStartDate()).toLocalDate());


        endDatePicker.setValue(((java.sql.Date) booking.getEndDate()).toLocalDate());
        emailField.setText(booking.getUser().getEmail());

        Optional<Room> matchingRoom = rooms.stream()
                .filter(r -> r.getNameRoom().equalsIgnoreCase(booking.getRoom().getNameRoom())
                        && r.getType() == booking.getRoom().getType())
                .findFirst();

        matchingRoom.ifPresent(room ->{
            System.out.println(room.getNameRoom());
                comboRoom.setValue(room.getNameRoom());
                comboRoom.setPromptText(room.getNameRoom());
                typeRoomComboBox.setValue(room.getType());
            }
        );
    }

    @FXML
    private void initialize() {
        typeRoomComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateRoomNamesByType(newVal));
        if (rooms == null || rooms.isEmpty()) {
            loadRooms();
        }
    }

    private void loadRooms() {
        rooms = roomService.getAll();
        List<String> roomLabels = rooms.stream()
                .map(room -> room.getNameRoom())
                .toList();
        comboRoom.setItems(FXCollections.observableArrayList(roomLabels));
        typeRoomComboBox.setItems(FXCollections.observableArrayList(TypeRoom.values()));
    }

    @FXML
    private void updateBooking() {
        String selectedRoomLabel = comboRoom.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (selectedRoomLabel == null || startDate == null || endDate == null) {
            showAlert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs.");
            return;
        }

        Room selectedRoom = rooms.stream()
                .filter(room -> room.getNameRoom().equals(selectedRoomLabel))
                .findFirst()
                .orElse(null);

        if (selectedRoom == null) {
            showAlert(Alert.AlertType.ERROR, "Chambre sélectionnée introuvable.");
            return;
        }

        booking.setRoom(selectedRoom);
        booking.setStartDate(Date.valueOf(startDate));
        booking.setEndDate(Date.valueOf(endDate));
        booking.setStatus(BookingStatus.PENDING);

        // Mise à jour de la réservation
        bookingService.update(booking);

        // Affichage de l'alerte de succès
        showAlert(Alert.AlertType.INFORMATION, "Réservation mise à jour avec succès.");


    }



    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void closeWindow() {
        // Ferme la fenêtre actuelle
        Stage stage = (Stage) updateButton.getScene().getWindow(); // Utilise ton bouton "Update"
        stage.close();
    }
    private void updateRoomNamesByType(TypeRoom selectedType) {
        if (selectedType == null || rooms == null) return;

        List<String> roomNames = rooms.stream()
                .filter(room -> room.getType() == selectedType)
                .map(Room::getNameRoom)
                .collect(Collectors.toList());

        comboRoom.setItems(FXCollections.observableArrayList(roomNames));
        comboRoom.setValue(null);
    }
}
