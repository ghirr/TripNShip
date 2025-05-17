package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Services.RoomService;

import java.io.IOException;
import java.util.List;

public class RoomListViewController {

    @FXML
    private GridPane roomGridPane;

    private final RoomService roomService = new RoomService();

    @FXML
    public void initialize() {

        List<Room> rooms = roomService.getAll();
        int columns = 3;
        int row = 0;
        int col = 0;

        for (Room room : rooms) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagementFXML/RoomCard.fxml"));
                AnchorPane card = loader.load();

                RoomCardController controller = loader.getController();
                controller.setRoom(room);

                controller.detailButton.setOnAction(e -> {
                    System.out.println("➡️ Détail de : " + room.getNameRoom());
                });

                controller.bookingButton.setOnAction(e -> {
                    openBookingWindow(room);
                });

                // Gestion du bouton Edit
                controller.editBookingButton.setOnAction(e -> {
                    openEditBookingWindow(room);
                });

                roomGridPane.add(card, col, row);

                col++;
                if (col == columns) {
                    col = 0;
                    row++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openBookingWindow(Room room) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagementFXML/AccommodationBooking.fxml"));
            Parent root = loader.load();

            // Si besoin, passer la room au contrôleur booking
            // AccommodationBookingController bookingController = loader.getController();
            // bookingController.setRoom(room);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une réservation pour : " + room.getNameRoom());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openEditBookingWindow(Room room) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagementFXML/EditAccommodationBooking.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et lui passer la room pour édition
            EditAccommodationBookingController editController = loader.getController();
            editController.setRoom(room);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier la réservation pour : " + room.getNameRoom());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
