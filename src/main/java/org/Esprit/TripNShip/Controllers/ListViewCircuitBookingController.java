package org.Esprit.TripNShip.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.Esprit.TripNShip.Entities.CircuitBooking;
import org.Esprit.TripNShip.Services.CircuitBookingService;


import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ListViewCircuitBookingController implements Initializable {

    @FXML private TableView<CircuitBooking> circuitBookingTable;
    @FXML private TableColumn<CircuitBooking, String> dateColumn;
    @FXML private TableColumn<CircuitBooking, String> statusColumn;
    @FXML private TableColumn<CircuitBooking, String> circuitColumn;
    @FXML private TableColumn<CircuitBooking, String> userColumn;
    @FXML private TableColumn<CircuitBooking, Void> actionsColumn;

    @FXML private Button addTourCircuitButton;
    @FXML private Button exportExcelButton;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> entriesComboBox;

    private final ObservableList<CircuitBooking> bookingList = FXCollections.observableArrayList();
    private final CircuitBookingService circuitBookingService = new CircuitBookingService();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private int currentEntriesLimit = 10;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureColumns();
        setupComboBox();
        setupSearch();
        setupButtons();
        loadBookings();
    }

    private void configureColumns() {
        dateColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getBookingDate().format(dateFormatter)
        ));
        statusColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getStatusBooking().toString()
        ));
        circuitColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getTourCircuit().getNameCircuit()
        ));
        userColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getUser().getFirstName()
        ));
     //   setupActionColumn();
    }

    private void setupComboBox() {
        entriesComboBox.setItems(FXCollections.observableArrayList("5", "10", "25", "50"));
        entriesComboBox.setValue("10");
        entriesComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentEntriesLimit = Integer.parseInt(newVal);
            filterBookings(searchField.getText());
        });
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterBookings(newValue));
    }

    private void setupButtons() {
        addTourCircuitButton.setOnAction(event -> handleAddBooking());
        exportExcelButton.setOnAction(event -> handleExportExcel());
    }

    private void loadBookings() {
        List<CircuitBooking> bookings = circuitBookingService.getAll();
        bookingList.setAll(bookings);
        filterBookings("");
    }

    private void filterBookings(String searchText) {
        String lowerText = searchText.toLowerCase();
        ObservableList<CircuitBooking> filtered = bookingList.filtered(booking ->
                booking.getBookingDate().format(dateFormatter).toLowerCase().contains(lowerText) ||
                        booking.getStatusBooking().toString().toLowerCase().contains(lowerText) ||
                        booking.getTourCircuit().getNameCircuit().toLowerCase().contains(lowerText) ||
                        booking.getUser().getFirstName().toLowerCase().contains(lowerText)
        );
        int limit = Math.min(currentEntriesLimit, filtered.size());
        circuitBookingTable.setItems(FXCollections.observableArrayList(filtered.subList(0, limit)));
    }

  /*  private void setupActionColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setOnAction(event -> {
                    CircuitBooking booking = getTableView().getItems().get(getIndex());
                    circuitBookingService.delete(booking.getIdBooking());
                    loadBookings();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    } */

    private void handleAddBooking() {
        // Redirection vers le formulaire d'ajout si nécessaire
    }

    private void handleExportExcel() {
        // À implémenter : Exporter bookingList au format Excel
    }
}
