package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Services.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AddVehicleRentalController {

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField totalPriceField;

    @FXML
    private ComboBox<StautCircuit> statusComboBox;

    @FXML
    private ComboBox<Vehicle> vehicleComboBox;

    @FXML
    private ComboBox<RentalAgency> agencyComboBox;

    @FXML
    private Button addRentalButton;

    private final VehicleRentalService vehicleRentalService = new VehicleRentalService();
    private final RentalAgencyService rentalAgencyService = new RentalAgencyService();
    private final VehicleService vehicleService = new VehicleService();

    @FXML
    private void initialize() {
        // Status
        statusComboBox.getItems().addAll(StautCircuit.values());

        // Agencies
        agencyComboBox.getItems().addAll(rentalAgencyService.getAll());
        agencyComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(RentalAgency rentalAgency, boolean empty) {
                super.updateItem(rentalAgency, empty);
                setText(empty || rentalAgency == null ? null : rentalAgency.getNameAgency());
            }
        });
        agencyComboBox.setButtonCell(agencyComboBox.getCellFactory().call(null));

        // Vehicles
        vehicleComboBox.getItems().addAll(vehicleService.getAll());
        vehicleComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Vehicle vehicle, boolean empty) {
                super.updateItem(vehicle, empty);
                setText(empty || vehicle == null ? null : vehicle.getBrand());
            }
        });
        vehicleComboBox.setButtonCell(vehicleComboBox.getCellFactory().call(null));

        // Button action
        addRentalButton.setOnAction(event -> addVehicleRental());
    }

    private void addVehicleRental() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String totalPriceText = totalPriceField.getText();
        StautCircuit status = statusComboBox.getValue();
        RentalAgency selectedAgency = agencyComboBox.getValue();
        Vehicle selectedVehicle = vehicleComboBox.getValue();

        if (startDate == null || endDate == null || totalPriceText.isEmpty() || status == null || selectedAgency == null || selectedVehicle == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        try {
            float totalPrice = Float.parseFloat(totalPriceText);

            VehicleRental rental = new VehicleRental();
            rental.setStartDate(startDate.atStartOfDay());
            rental.setEndDate(endDate.atStartOfDay());
            rental.setTotalPrice(totalPrice);
            rental.setStatusCircuit(status);
            rental.setVehicle(selectedVehicle);

            vehicleRentalService.add(rental);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle rental added successfully!");

            clearFields();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Total price must be a valid number.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
        }
    }

    private void clearFields() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        totalPriceField.clear();
        statusComboBox.setValue(null);
        agencyComboBox.setValue(null);
        vehicleComboBox.setValue(null);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
