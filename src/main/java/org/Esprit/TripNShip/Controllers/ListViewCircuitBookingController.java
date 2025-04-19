package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.CircuitBooking;
import org.Esprit.TripNShip.Entities.StatusBooking;
import org.Esprit.TripNShip.Entities.TourCircuit;
import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Services.CircuitBookingService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListViewCircuitBookingController {

    @FXML private ComboBox<String> entriesComboBox;
    @FXML private TextField searchField;
    @FXML private Button addTourCircuitButton;
    @FXML private Button exportExcelButton;
    @FXML private TableView<CircuitBooking> circuitBookingTable;
    @FXML private TableColumn<CircuitBooking, String> dateColumn;
    @FXML private TableColumn<CircuitBooking, String> statusColumn;
    @FXML private TableColumn<CircuitBooking, String> tourColumn;
    @FXML private TableColumn<CircuitBooking, String> clientColumn;
    @FXML private TableColumn<CircuitBooking, Void> actionColumn;

    private final CircuitBookingService bookingService = new CircuitBookingService();
    private final ObservableList<CircuitBooking> bookingList = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        // Configuration des colonnes
        configureColumns();

        // Chargement des données
        loadBookings();

        // Configuration du ComboBox
        entriesComboBox.getSelectionModel().selectFirst();

        // Configuration des boutons
        addTourCircuitButton.setOnAction(event -> handleAddBooking());
        exportExcelButton.setOnAction(event -> handleExportExcel());

        // Configuration de la recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterBookings(newValue));
    }

    private void configureColumns() {
        dateColumn.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getBookingDate();
            return new javafx.beans.property.SimpleStringProperty(date != null ? date.format(dateFormatter) : "");
        });

        statusColumn.setCellValueFactory(cellData -> {
            StatusBooking status = cellData.getValue().getStatusBooking();
            return new javafx.beans.property.SimpleStringProperty(status != null ? status.toString() : "");
        });

        tourColumn.setCellValueFactory(cellData -> {
            TourCircuit tour = cellData.getValue().getTourCircuit();
            return new javafx.beans.property.SimpleStringProperty(tour != null ? tour.getNameCircuit() : "");
        });

        clientColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue().getUser();
            return new javafx.beans.property.SimpleStringProperty(user != null ? user.getFirstName() : "");
        });

        setupActionColumn();
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonsContainer = new HBox(5, editButton, deleteButton);

            {
                editButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 5;");
                deleteButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 5;");

                editButton.setOnAction(event -> {
                    CircuitBooking booking = getTableView().getItems().get(getIndex());

                });

                deleteButton.setOnAction(event -> {
                    CircuitBooking booking = getTableView().getItems().get(getIndex());
                    handleDeleteBooking(booking);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonsContainer);
            }
        });
    }

    private void loadBookings() {
        List<CircuitBooking> bookings = bookingService.getAll();
        bookingList.setAll(bookings);
        circuitBookingTable.setItems(bookingList);
    }

    private void filterBookings(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            circuitBookingTable.setItems(bookingList);
        } else {
            ObservableList<CircuitBooking> filteredList = FXCollections.observableArrayList();
            String searchLower = searchText.toLowerCase();

            for (CircuitBooking booking : bookingList) {
                if (matchesSearch(booking, searchLower)) {
                    filteredList.add(booking);
                }
            }
            circuitBookingTable.setItems(filteredList);
        }
    }

    private boolean matchesSearch(CircuitBooking booking, String searchLower) {
        if (booking.getBookingDate() != null &&
                booking.getBookingDate().format(dateFormatter).toLowerCase().contains(searchLower)) {
            return true;
        }

        if (booking.getStatusBooking() != null &&
                booking.getStatusBooking().toString().toLowerCase().contains(searchLower)) {
            return true;
        }

        if (booking.getTourCircuit() != null &&
                booking.getTourCircuit().getNameCircuit().toLowerCase().contains(searchLower)) {
            return true;
        }

        if (booking.getUser() != null &&
                booking.getUser().getFirstName().toLowerCase().contains(searchLower)) {
            return true;
        }

        return false;
    }

    private void handleAddBooking() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/AddCircuitBooking.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add New Booking");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadBookings(); // Rafraîchir après fermeture
        } catch (IOException e) {
            showAlert("Error", "Could not load the booking form: " + e.getMessage());
        }
    }



    private void handleDeleteBooking(CircuitBooking booking) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Booking");
        alert.setContentText("Are you sure you want to delete this booking?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                bookingService.delete(booking);
                loadBookings();
            }
        });
    }

    private void handleExportExcel() {
        // Implémentez l'export Excel ici
        showAlert("Info", "Excel export functionality will be implemented here");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}