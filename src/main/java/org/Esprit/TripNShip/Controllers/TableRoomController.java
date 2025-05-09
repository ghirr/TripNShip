package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Entities.TypeRoom;
import org.Esprit.TripNShip.Services.RoomService;

import java.io.IOException;
import java.util.List;

public class TableRoomController {

    @FXML private ComboBox<String> entriesComboBox;
    @FXML private TextField searchField;
    @FXML private Button addRoomButton;
    @FXML private Button exportExcelButton;
    @FXML private TableView<Room> roomTable;
    @FXML private TableColumn<Room, TypeRoom> typeColumn;
    @FXML private TableColumn<Room, String> nameColumn;
    @FXML private TableColumn<Room, String> accommodationColumn;
    @FXML private TableColumn<Room, String> accommodationNameColumn;
    @FXML private TableColumn<Room, Float> priceColumn;
    @FXML private TableColumn<Room, Boolean> availabilityColumn;
    @FXML private TableColumn<Room, Void> actionsColumn;

    private final RoomService roomService = new RoomService();
    private ObservableList<Room> roomList = FXCollections.observableArrayList();
    private static TableRoomController instance;
    public TableRoomController() {
        instance = this;
    }

    public static TableRoomController getInstance() {
        return instance;
    }

    @FXML
    private void initialize() {
        // Lier les colonnes aux propriétés
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nameRoom"));
        accommodationColumn.setCellValueFactory(new PropertyValueFactory<>("typeAccommodation"));
        accommodationNameColumn.setCellValueFactory(new PropertyValueFactory<>("accommodationName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));

        // Charger les données
        loadRoomsFromDatabase();

        // Configurer le ComboBox
        entriesComboBox.getItems().addAll("5", "10", "25", "50");
        entriesComboBox.getSelectionModel().selectFirst();

        // Configurer les boutons
        addRoomButton.setOnAction(this::openAddRoomForm);
        exportExcelButton.setOnAction(event -> handleExportExcel());

        // Recherche dynamique
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterRooms(newValue));

        // Colonne d'actions (modifier, supprimer)
        setupActionButtons();
    }

    public void loadRoomsFromDatabase() {
        List<Room> rooms = roomService.getAll();
        roomList.setAll(rooms);
        roomTable.setItems(roomList);
    }

    private void filterRooms(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            roomTable.setItems(roomList);
        } else {
            ObservableList<Room> filteredList = FXCollections.observableArrayList();
            for (Room room : roomList) {
                // Convert the enum to a string with name() and then use toLowerCase()
                if (room.getNameRoom().toLowerCase().contains(searchText.toLowerCase()) ||
                       // room.getAccommodationType().toLowerCase().contains(searchText.toLowerCase()) ||
                        room.getTypeAccommodation().name().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(room);
                }
            }
            roomTable.setItems(filteredList);
        }
    }

    private void setupActionButtons() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonsContainer = new HBox(5, editButton, deleteButton);

            {
                editButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 5;");
                deleteButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 5;");

                editButton.setOnAction(event -> {
                    Room room = getTableView().getItems().get(getIndex());
                    handleEditRoom(room);
                });

                deleteButton.setOnAction(event -> {
                    Room room = getTableView().getItems().get(getIndex());
                    roomService.delete(room);
                    loadRoomsFromDatabase();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonsContainer);
            }
        });
    }

    @FXML
    private void handleEditRoom(Room selectedRoom) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagementFXML/EditRoom.fxml"));
            Parent root = loader.load();

            EditRoomController controller = loader.getController();
            controller.setRoom(selectedRoom);

            Stage stage = new Stage();
            stage.setTitle("Edit Room");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleExportExcel() {
        // TODO: Implémenter l'export Excel
        System.out.println("Export to Excel triggered");
    }

    private void openAddRoomForm(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagementFXML/Room.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Room");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("⚠️ Erreur lors du chargement de Room.fxml : " + e.getMessage());
        }
    }
}
