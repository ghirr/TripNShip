package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.Esprit.TripNShip.Entities.AccommodationBooking;
import org.Esprit.TripNShip.Services.AccommodationBookingService;

import java.util.List;

public class TableAccommodationBookingController {

    @FXML private ComboBox<String> entriesComboBox;
    @FXML private TextField searchField;
    @FXML private Button addBookingButton;
    @FXML private TableView<AccommodationBooking> bookingTable;
    @FXML private TableColumn<AccommodationBooking, String> startDateColumn;
    @FXML private TableColumn<AccommodationBooking, String> endDateColumn;
    @FXML private TableColumn<AccommodationBooking, String> roomNameColumn;
    @FXML private TableColumn<AccommodationBooking, String> usernameColumn;
    @FXML private TableColumn<AccommodationBooking, String> statusColumn;
    @FXML private TableColumn<AccommodationBooking, Void> actionColumn;

    private final AccommodationBookingService bookingService = new AccommodationBookingService();
    private ObservableList<AccommodationBooking> bookingList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        roomNameColumn.setCellValueFactory(new PropertyValueFactory<>("roomId")); // à remplacer si nécessaire
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("idUser")); // à remplacer si nécessaire
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadBookings();

        entriesComboBox.getItems().addAll("10", "25", "50", "100");
        entriesComboBox.getSelectionModel().selectFirst();

        addBookingButton.setOnAction(event -> openAddBookingForm());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterBookings(newValue));

        setupActionButtons();
    }

    private void loadBookings() {
        List<AccommodationBooking> bookings = bookingService.getAll();
        bookingList.setAll(bookings);
        bookingTable.setItems(bookingList);
    }

    private void filterBookings(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            bookingTable.setItems(bookingList);
        } else {
            ObservableList<AccommodationBooking> filtered = FXCollections.observableArrayList();
            for (AccommodationBooking booking : bookingList) {
                if (String.valueOf(booking.getIdUser()).contains(searchText) ||
                        String.valueOf(booking.getRoomId()).contains(searchText) ||
                        booking.getStatus().toString().toLowerCase().contains(searchText.toLowerCase())) {
                    filtered.add(booking);
                }
            }
            bookingTable.setItems(filtered);
        }
    }

    private void setupActionButtons() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox box = new HBox(5, editButton, deleteButton);

            {
                editButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 5;");
                deleteButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 5;");

                editButton.setOnAction(event -> {
                    AccommodationBooking booking = getTableView().getItems().get(getIndex());
                    handleEditBooking(booking);
                });

                deleteButton.setOnAction(event -> {
                    AccommodationBooking booking = getTableView().getItems().get(getIndex());
                    bookingService.delete(booking);
                    loadBookings();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void handleEditBooking(AccommodationBooking booking) {
        // Implémente la logique d’édition ici
    }

    private void openAddBookingForm() {
        // Implémente la logique pour ouvrir un formulaire de réservation
    }
}
