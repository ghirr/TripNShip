package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Services.AccommodationBookingService;
import org.Esprit.TripNShip.Services.RoomService;
import org.Esprit.TripNShip.Services.UserService;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class AccommodationBookingController {

    @FXML
    private ComboBox<TypeRoom> comboTypeRoom;

    @FXML
    private ComboBox<String> comboRoom;

    @FXML
    private DatePicker startDatePicker, endDatePicker;

    @FXML
    private Button saveButton,addAccommodationButton;

    @FXML
    private Label userEmailLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private ComboBox<String> typeRoomComboBox;

    private AccommodationBooking booking;

    private final RoomService roomService = new RoomService();
    private final AccommodationBookingService bookingService = new AccommodationBookingService();
    private final UserService userService = new UserService();

    private List<Room> rooms;
    private List<User> users;

    @FXML
    private void initialize() {
        comboTypeRoom.setItems(FXCollections.observableArrayList(TypeRoom.values()));
        comboTypeRoom.valueProperty().addListener((obs, oldVal, newVal) -> updateRoomNamesByType(newVal));
        addAccommodationButton.setOnAction(event -> {saveBooking();});
        loadRooms();
    }

    private void loadRooms() {
        try {
            rooms = roomService.getAll();
            if (rooms == null || rooms.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Aucune chambre disponible.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "❌ Erreur lors du chargement des chambres : " + e.getMessage());
        }
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

    public void setBooking(AccommodationBooking booking) {
        this.booking = booking;

        if (booking != null) {
            if (booking.getStartDate() != null)
                startDatePicker.setValue(booking.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            if (booking.getEndDate() != null)
                endDatePicker.setValue(booking.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            if (rooms == null || rooms.isEmpty()) {
                loadRooms();
            }

            Room selectedRoom = booking.getRoom();
            if (selectedRoom != null) {
                comboTypeRoom.setValue(selectedRoom.getType());
                updateRoomNamesByType(selectedRoom.getType());
                comboRoom.setValue(selectedRoom.getNameRoom());
            }

            // Affichage email utilisateur
            User user = booking.getUser();
            if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
                if (userEmailLabel != null) {
                    userEmailLabel.setText("Email: " + user.getEmail());
                }
            }
        }
    }

    @FXML
    public void saveBooking() {
        TypeRoom selectedType = comboTypeRoom.getValue();
        String selectedRoomName = comboRoom.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String email = usernameField.getText();

        if (selectedType == null || selectedRoomName == null || startDate == null || endDate == null) {
            showAlert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs.");
            return;
        }

        if (endDate.isBefore(startDate)) {
            showAlert(Alert.AlertType.ERROR, "La date de fin ne peut pas être avant la date de début.");
            return;
        }

        Room selectedRoom = rooms.stream()
                .filter(room -> room.getType() == selectedType && room.getNameRoom().equals(selectedRoomName))
                .findFirst()
                .orElse(null);

        if (selectedRoom == null) {
            showAlert(Alert.AlertType.ERROR, "Chambre non trouvée.");
            return;
        }

        if (booking == null) {
            User defaultUser = new User(1,"Ahmed","hhhh",Role.CLIENT,"islemghariani.dev@gmail.com");
            booking = new AccommodationBooking(
                    0,
                    defaultUser,
                    selectedRoom,
                    Date.valueOf(startDate),
                    Date.valueOf(endDate),
                    BookingStatus.PENDING
            );
            bookingService.add(booking);
            showSuccessMessage("Réservation ajoutée avec succès !");
        } else {
            booking.setRoom(selectedRoom);
            booking.setStartDate(Date.valueOf(startDate));
            booking.setEndDate(Date.valueOf(endDate));
            booking.setStatus(BookingStatus.PENDING);
            bookingService.update(booking);
            showSuccessMessage("Réservation mise à jour avec succès !");
        }

        closeWindow();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessMessage(String message) {
        showAlert(Alert.AlertType.INFORMATION, message);
    }

    private void closeWindow() {
        Stage stage = (Stage) addAccommodationButton.getScene().getWindow();
        if (stage != null) stage.close();
    }
}
