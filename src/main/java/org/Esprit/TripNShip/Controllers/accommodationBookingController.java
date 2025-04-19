package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class accommodationBookingController {

    @FXML
    private ComboBox<String> entriesComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<AccommodationBooking> roomTable;

    @FXML
    private TableColumn<AccommodationBooking, String> nameRoomColumn;

    @FXML
    private TableColumn<AccommodationBooking, String> typeAccommodationColumn;

    @FXML
    private TableColumn<AccommodationBooking, Double> priceColumn;

    @FXML
    private TableColumn<AccommodationBooking, String> availabilityColumn;

    @FXML
    private TableColumn<AccommodationBooking, String> statusColumn;

    @FXML
    private TableColumn<AccommodationBooking, String> actionsColumn;

    // Database connection
    private Connection connection;

    public void initialize() {
        // Initialize the table columns
        nameRoomColumn.setCellValueFactory(new PropertyValueFactory<>("nameRoom"));
        typeAccommodationColumn.setCellValueFactory(new PropertyValueFactory<>("typeAccommodation"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        actionsColumn.setCellValueFactory(new PropertyValueFactory<>("actions"));

        // Set the entriesComboBox values
        entriesComboBox.setItems(FXCollections.observableArrayList("5", "10", "25", "50"));
        entriesComboBox.getSelectionModel().select(0);  // Default value (5)

        // Load the initial data into the table
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        // This method should load the data from the database and populate the table
        ObservableList<AccommodationBooking> bookings = FXCollections.observableArrayList();

        try {
            String query = "SELECT * FROM accommodation_booking";  // Adjust the query according to your schema
            Statement stmt = getConnection().createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                bookings.add(new AccommodationBooking(
                        resultSet.getString("nameRoom"),
                        resultSet.getString("typeAccommodation"),
                        resultSet.getDouble("price"),
                        resultSet.getString("availability"),
                        resultSet.getString("status"),
                        resultSet.getString("actions")  // Assuming actions are stored in DB
                ));
            }

            roomTable.setItems(bookings);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exportToCSV(ActionEvent event) {
        BufferedWriter writer = null;
        try {
            // Create a new CSV file
            writer = new BufferedWriter(new FileWriter("AccommodationBooking.csv"));

            // Write the header row
            writer.write("Name Room,Type Accommodation,Price,Availability,Status,Actions");
            writer.newLine();

            // Write data rows
            ObservableList<AccommodationBooking> bookings = roomTable.getItems();
            for (AccommodationBooking booking : bookings) {
                writer.write(String.format("%s,%s,%.2f,%s,%s,%s",
                        booking.getNameRoom(),
                        booking.getTypeAccommodation(),
                        booking.getPrice(),
                        booking.getAvailability(),
                        booking.getStatus(),
                        booking.getActions()));
                writer.newLine();
            }

            // Show success message
            System.out.println("Export successful to AccommodationBooking.csv");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Connection getConnection() {
        if (connection == null) {
            try {
                // Replace with your actual database credentials and URL
                String url = "jdbc:oracle:thin:@localhost:1521:orcl";
                String user = "username";
                String password = "password";
                connection = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    // AccommodationBooking class (model)
    public static class AccommodationBooking {
        private String nameRoom;
        private String typeAccommodation;
        private double price;
        private String availability;
        private String status;
        private String actions;

        public AccommodationBooking(String nameRoom, String typeAccommodation, double price, String availability, String status, String actions) {
            this.nameRoom = nameRoom;
            this.typeAccommodation = typeAccommodation;
            this.price = price;
            this.availability = availability;
            this.status = status;
            this.actions = actions;
        }

        // Getters and Setters
        public String getNameRoom() { return nameRoom; }
        public String getTypeAccommodation() { return typeAccommodation; }
        public double getPrice() { return price; }
        public String getAvailability() { return availability; }
        public String getStatus() { return status; }
        public String getActions() { return actions; }
    }
}
