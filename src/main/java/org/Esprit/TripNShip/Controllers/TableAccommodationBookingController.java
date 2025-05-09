package org.Esprit.TripNShip.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Services.AccommodationBookingService;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TableAccommodationBookingController {

    @FXML
    private TableView<AccommodationBooking> bookingTable;

    @FXML
    private TableColumn<AccommodationBooking, String> startDateColumn;

    @FXML
    private TableColumn<AccommodationBooking, String> endDateColumn;

    @FXML
    private TableColumn<AccommodationBooking, String> emailColumn;

    @FXML
    private TableColumn<AccommodationBooking, String> typeRoomColumn;

    @FXML
    private TableColumn<AccommodationBooking, String> roomNameColumn;

    @FXML
    private TableColumn<AccommodationBooking, String> statusColumn;

    @FXML
    private TableColumn<AccommodationBooking, Void> actionColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> entriesComboBox;

    @FXML
    private Button addBookingButton;

    private final AccommodationBookingService bookingService = new AccommodationBookingService();
    private FilteredList<AccommodationBooking> filteredBookings;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void initialize() {
        loadBookings();

        startDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStartDate() != null
                        ? cellData.getValue().getStartDate().toString()
                        : "")
        );

        endDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEndDate() != null
                        ? cellData.getValue().getEndDate().toString()
                        : "")
        );


        emailColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getUser() != null ? cellData.getValue().getUser().getEmail() : ""
                )
        );

        typeRoomColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getRoom() != null ? cellData.getValue().getRoom().getType().name() : ""
                )
        );

        roomNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getRoom() != null ? cellData.getValue().getRoom().getNameRoom() : ""
                )
        );

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        addActionsToTable();
        setupSearch();
    }

    private void loadBookings() {
        List<AccommodationBooking> bookings = bookingService.getAll();
        filteredBookings = new FilteredList<>(FXCollections.observableArrayList(bookings), p -> true);
        bookingTable.setItems(filteredBookings);
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredBookings.setPredicate(booking -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return booking.getUser() != null && booking.getUser().getEmail().toLowerCase().contains(lowerCaseFilter)
                        || booking.getRoom() != null && booking.getRoom().getNameRoom().toLowerCase().contains(lowerCaseFilter)
                        || booking.getStatus().name().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private void addActionsToTable() {
        Callback<TableColumn<AccommodationBooking, Void>, TableCell<AccommodationBooking, Void>> cellFactory = param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white;");
                editButton.setOnAction(event -> {
                    AccommodationBooking booking = getTableView().getItems().get(getIndex());
                    handleEditAccommodationBooking(booking);
                });

                deleteButton.setOnAction(event -> {
                    AccommodationBooking booking = getTableView().getItems().get(getIndex());
                    bookingService.delete(booking);
                    bookingTable.getItems().remove(booking);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10, editButton, deleteButton);
                    setGraphic(hbox);
                }
            }
        };

        actionColumn.setCellFactory(cellFactory);
    }

    @FXML
    private void handleEditAccommodationBooking(AccommodationBooking selectedBooking) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagementFXML/EditAccommodationBooking.fxml"));
            Parent root = loader.load();

            EditAccommodationBookingController controller = loader.getController();
            controller.setBooking(selectedBooking);

            Stage stage = new Stage();
            stage.setTitle("Edit Accommodation Booking");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openAddBookingForm(ActionEvent event) {
        // Code pour ouvrir le formulaire addBooking.fxml
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagementFXML/AccommodationBooking.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Booking");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

