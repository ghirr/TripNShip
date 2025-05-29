package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Services.RoomService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class RoomListViewController {

    @FXML
    private GridPane roomGridPane;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button filterButton;

    @FXML
    private Button clearFilterButton;

    @FXML
    private Label statusLabel;

    private final RoomService roomService = new RoomService();

    @FXML
    public void initialize() {
        setupDatePicker();
        loadAllRooms();
    }

    private void setupDatePicker() {
        // Set minimum date to today
        datePicker.setValue(LocalDate.now());

        // Disable past dates
        datePicker.setDayCellFactory(new Callback<DatePicker, javafx.scene.control.DateCell>() {
            @Override
            public javafx.scene.control.DateCell call(DatePicker datePicker) {
                return new javafx.scene.control.DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        // Disable dates before today
                        if (item.isBefore(LocalDate.now())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;"); // Light red for disabled dates
                        }
                    }
                };
            }
        });
    }

    @FXML
    private void filterRoomsByDate() {
        LocalDate selectedDate = datePicker.getValue();

        if (selectedDate == null) {
            showAlert("Please select a date", "You must select a date to filter available rooms.");
            return;
        }

        if (selectedDate.isBefore(LocalDate.now())) {
            showAlert("Invalid Date", "Please select today's date or a future date.");
            return;
        }

        // Convert LocalDate to Date
        Date filterDate = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Show loading status
        statusLabel.setText("Loading available rooms for " + selectedDate + "...");
        statusLabel.setVisible(true);

        // Get available rooms for the selected date
        List<Room> availableRooms = roomService.getAvailableRoom(filterDate);

        // Update status
        if (availableRooms.isEmpty()) {
            statusLabel.setText("No rooms available for " + selectedDate);
        } else {
            statusLabel.setText("Found " + availableRooms.size() + " available room(s) for " + selectedDate);
        }

        // Load the filtered rooms
        loadRoomsToGrid(availableRooms);
    }

    @FXML
    private void showAllRooms() {
        statusLabel.setText("Showing all rooms");
        statusLabel.setVisible(true);
        loadAllRooms();

        // Reset date picker to today
        datePicker.setValue(LocalDate.now());
    }

    private void loadAllRooms() {
        List<Room> rooms = roomService.getAll();
        loadRoomsToGrid(rooms);

        if (statusLabel.isVisible()) {
            statusLabel.setText("Showing all " + rooms.size() + " room(s)");
        }
    }

    private void loadRoomsToGrid(List<Room> rooms) {
        // Clear existing content
        roomGridPane.getChildren().clear();

        if (rooms.isEmpty()) {
            // Show no rooms message
            Label noRoomsLabel = new Label("No rooms found");
            noRoomsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #7f8c8d; -fx-padding: 50;");
            roomGridPane.add(noRoomsLabel, 1, 0); // Center column
            return;
        }

        int columns = 3;
        int row = 0;
        int col = 0;

        for (Room room : rooms) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagementFXML/RoomCard.fxml"));
                AnchorPane card = loader.load();

                RoomCardController controller = loader.getController();
                controller.setRoom(room);

                controller.detailButton.setOnAction(e -> openRoomDetails(room));

                controller.bookingButton.setOnAction(e -> {
                    openBookingWindow(room);
                });

                roomGridPane.add(card, col, row);

                col++;
                if (col == columns) {
                    col = 0;
                    row++;
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error loading room card for room: " + room.getNameRoom());
            }
        }
    }

    private void openBookingWindow(Room room) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagementFXML/AccommodationBooking.fxml"));
            Parent root = loader.load();
            AccommodationBookingController controller = loader.getController();
            controller.setRoom(room);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une réservation pour : " + room.getNameRoom());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh the view after booking (in case availability changed)
            if (datePicker.getValue() != null && !datePicker.getValue().equals(LocalDate.now())) {
                filterRoomsByDate();
            } else {
                loadAllRooms();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openRoomDetails(Room room) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagementFXML/RoomDetails.fxml"));
            Parent root = loader.load();

            RoomDetailsController controller = loader.getController();
            controller.setRoom(room);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Room Details");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}