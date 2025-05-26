package org.Esprit.TripNShip.Controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Entities.TypeRoom;
import org.Esprit.TripNShip.Services.RoomService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableRoomController {

    @FXML private TableColumn<Room, ImageView> photoColumn;
    @FXML private ComboBox<TypeRoom> typeRoomFilter;
    @FXML private TextField searchField;
    @FXML private Button addRoomButton;
    @FXML private TableView<Room> roomTable;
    @FXML private TableColumn<Room, TypeRoom> typeColumn;
    @FXML private TableColumn<Room, String> nameColumn;
    @FXML private TableColumn<Room, String> accommodationColumn;
    @FXML private TableColumn<Room, String> accommodationNameColumn;
    @FXML private TableColumn<Room, Float> priceColumn;
    @FXML private TableColumn<Room, Boolean> availabilityColumn;
    @FXML private TableColumn<Room, Void> actionsColumn;
    @FXML private Pagination pagination;

    private final RoomService roomService = new RoomService();
    private ObservableList<Room> roomList = FXCollections.observableArrayList();
    private List<Room> filteredRooms = new ArrayList<>();
    private static final int ROWS_PER_PAGE = 20;
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
        photoColumn.setCellValueFactory(cellData -> {
            String imagePath = cellData.getValue().getPhotosRoom();
            Image image = new Image(imagePath);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            return new SimpleObjectProperty<>(imageView);
        });



        // Configurer ComboBox pour le filtrage par TypeRoom
        typeRoomFilter.getItems().add(null);  // option pour "All types"
        typeRoomFilter.getItems().addAll(TypeRoom.values());
        typeRoomFilter.setPromptText("All Types");
        typeRoomFilter.setOnAction(event -> applyFilters());

        // Recherche dynamique sur le nom de la chambre
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Charger les données
        loadRoomsFromDatabase();

        // Configurer le bouton d'ajout
        addRoomButton.setOnAction(this::openAddRoomForm);

        // Configurer la pagination
        pagination.setPageFactory(this::createPage);

        // Configurer la colonne des actions (edit, delete)
        actionsColumn.setCellFactory(param -> new TableCell<Room, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonsContainer = new HBox(5, editButton, deleteButton);

            {
                // Set style for buttons
                editButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 5;");
                deleteButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 5;");

                   // Set action for "Edit" button
                editButton.setOnAction(event -> {
                    Room room = getTableView().getItems().get(getIndex());
                    handleEditRoom(room);
                });

                deleteButton.setOnAction(event -> {
                    Room room = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(room); // méthode identique à celle pour AccommodationBooking
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonsContainer);
            }
        });
    }

    public void loadRoomsFromDatabase() {
        List<Room> rooms = roomService.getAll();
        roomList.setAll(rooms);
        applyFilters();
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        TypeRoom selectedType = typeRoomFilter.getSelectionModel().getSelectedItem();

        filteredRooms = roomList.stream()
                .filter(room -> (selectedType == null || room.getType() == selectedType) &&
                        (searchText.isEmpty() || room.getNameRoom().toLowerCase().contains(searchText) ||
                                room.getTypeAccommodation().name().toLowerCase().contains(searchText)))
                .toList();

        int pageCount = (int) Math.ceil((double) filteredRooms.size() / ROWS_PER_PAGE);
        pagination.setPageCount(Math.max(pageCount, 1));  // Ajuster le nombre de pages
        pagination.setPageFactory(this::createPage);
        roomTable.refresh();  // This refreshes the table view itself after filtering.

    }

    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredRooms.size());

        ObservableList<Room> pageRooms = FXCollections.observableArrayList(filteredRooms.subList(fromIndex, toIndex));
        roomTable.setItems(pageRooms);
        return roomTable;
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
            loadRoomsFromDatabase();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("⚠️ Erreur lors du chargement de Room.fxml : " + e.getMessage());
        }
    }

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
            loadRoomsFromDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showDeleteConfirmation(Room room) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this room?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                roomService.delete(room);
                loadRoomsFromDatabase(); // Rafraîchir après suppression
            }
        });
    }



    private void showErrorMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
