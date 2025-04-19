package org.Esprit.TripNShip.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Entities.TypeAccommodation;
import org.Esprit.TripNShip.Entities.TypeRoom;
import org.Esprit.TripNShip.Services.RoomService;

public class TableRoomController {

    @FXML private TableView<Room> roomTable;
    @FXML private TableColumn<Room, String> typeColumn;
    @FXML private TableColumn<Room, String> nameRoomColumn;
    @FXML private TableColumn<Room, Double> priceColumn;
    @FXML private TableColumn<Room, String> availabilityColumn;
    @FXML private TableColumn<Room, String> typeAccommodationColumn;
    @FXML private TableColumn<Room, Void> actionColumn;

    private final RoomService roomService = new RoomService();

    public void initialize() {
        loadRooms();
        setupColumns();
    }

    private void loadRooms() {
        // Fetch the rooms from the service and populate the TableView
        ObservableList<Room> rooms = FXCollections.observableArrayList(roomService.getAll());
        roomTable.setItems(rooms);
    }

    private void setupColumns() {
        // Set up the columns to display room data
        typeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getType().toString()));

        nameRoomColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNameRoom()));

        priceColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPrice()));

        availabilityColumn.setCellValueFactory(cellData -> {
            boolean available = cellData.getValue().isAvailability();
            return new SimpleStringProperty(available ? "Available" : "Unavailable");
        });

        typeAccommodationColumn.setCellValueFactory(cellData -> {
            TypeAccommodation typeAcc = cellData.getValue().getTypeAccommodation();
            return new SimpleStringProperty(typeAcc != null ? typeAcc.toString() : "N/A");
        });

        addActionButtons();
    }

    private void addActionButtons() {
        // Add "Edit" and "Delete" buttons to the last column
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Room, Void> call(final TableColumn<Room, Void> param) {
                return new TableCell<Room, Void>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");

                    {
                        // Edit Button Action
                        editButton.setOnAction(event -> {
                            Room selectedRoom = getTableView().getItems().get(getIndex());
                            // Handle edit action (e.g., open a dialog to edit room details)
                            System.out.println("Edit: " + selectedRoom);
                        });

                        // Delete Button Action
                        deleteButton.setOnAction(event -> {
                            Room selectedRoom = getTableView().getItems().get(getIndex());
                            roomService.delete(selectedRoom);  // Delete room from the service

                            // Reload the rooms after deletion
                            loadRooms();
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            // Add buttons to each row
                            HBox hbox = new HBox(10, editButton, deleteButton);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }
}
