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
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.Esprit.TripNShip.Entities.AccommodationBooking;
import org.Esprit.TripNShip.Services.AccommodationBookingService;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
    private ComboBox<String> entriesComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private Button addBookingButton;

    private final AccommodationBookingService bookingService = new AccommodationBookingService();
    private FilteredList<AccommodationBooking> filteredBookings;

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");

    @FXML
    private void initialize() {
        loadBookings();

        // Configurer les colonnes de la table
        startDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStartDate() != null
                        ? FORMATTER.format(cellData.getValue().getStartDate())
                        : "")
        );

        endDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEndDate() != null
                        ? FORMATTER.format(cellData.getValue().getEndDate())
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

        // Ajouter des options à la ComboBox pour filtrer par Type de Chambre
        entriesComboBox.getItems().addAll("All", "SINGLE", "DOUBLE", "SUITE"); // Ajoutez l'option "All" pour afficher toutes les réservations
        entriesComboBox.setValue("All"); // Valeur par défaut = "All"
        entriesComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();  // Appliquer les filtres
        });

        // Ajouter un événement de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();  // Appliquer les filtres de recherche
        });

        // Ajouter des actions de table (Edit, Delete)
        addActionsToTable();
    }

    private void loadBookings() {
        List<AccommodationBooking> bookings = bookingService.getAll();
        filteredBookings = new FilteredList<>(FXCollections.observableArrayList(bookings), p -> true);
        bookingTable.setItems(filteredBookings);
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();  // Récupérer le texte de recherche
        String typeRoomFilter = entriesComboBox.getValue();  // Récupérer la valeur du filtre par type de chambre

        // Appliquer les filtres
        filteredBookings.setPredicate(booking -> {
            // Filtrer par texte de recherche
            boolean matchesSearch = (searchText.isEmpty() ||
                    (booking.getUser() != null && booking.getUser().getEmail().toLowerCase().contains(searchText)) ||
                    (booking.getRoom() != null && booking.getRoom().getNameRoom().toLowerCase().contains(searchText)) ||
                    (booking.getStatus() != null && booking.getStatus().name().toLowerCase().contains(searchText)));

            // Filtrer par type de chambre
            boolean matchesTypeRoom = (typeRoomFilter.equals("All") ||
                    (booking.getRoom() != null && booking.getRoom().getType().name().equalsIgnoreCase(typeRoomFilter)));

            return matchesSearch && matchesTypeRoom;  // Appliquer les deux filtres
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
                    showDeleteConfirmation(booking);
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

    private void showDeleteConfirmation(AccommodationBooking booking) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this booking?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                bookingService.delete(booking);
                loadBookings(); // Rafraîchir après suppression
            }
        });
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
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait(); // wait until closed
            loadBookings(); // Refresh table after edit
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openAddBookingForm(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccommodationManagementFXML/AccommodationBooking.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Booking");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait(); // wait until closed
            loadBookings(); // Refresh table after addition
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
