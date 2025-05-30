package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Services.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class VehicleRentalFormController {

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField totalPriceField;



    @FXML
    private Button addRentalButton;

    @FXML
    private ImageView closeIcon;

    private final VehicleRentalService vehicleRentalService = new VehicleRentalService();
    private final UserService userService = new UserService();

    private Vehicle selectedVehicle;

    @FXML
    private void initialize() {

        totalPriceField.setEditable(false);

        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updatePrice());
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updatePrice());

        addRentalButton.setOnAction(event -> addVehicleRental());
        closeIcon.setOnMouseClicked(e -> handleCloseForm());
    }

    public void setSelectedVehicle(Vehicle vehicle) {
        this.selectedVehicle = vehicle;
        updatePrice();
    }

    private void updatePrice() {
        if (startDatePicker.getValue() != null && endDatePicker.getValue() != null && selectedVehicle != null) {
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();

            if (!end.isBefore(start)) {
                long days = ChronoUnit.DAYS.between(start, end);
                if (days == 0) days = 1;

                float pricePerDay = selectedVehicle.getDailyPrice();
                float total = days * pricePerDay;
                totalPriceField.setText(String.format(Locale.US, "%.2f", total));
            } else {
                totalPriceField.clear();
            }
        }
    }

    private void addVehicleRental() {
        try {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String totalPriceText = totalPriceField.getText();


            if (startDate == null || endDate == null || totalPriceText == null || totalPriceText.isEmpty() || selectedVehicle == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
                return;
            }

            if (endDate.isBefore(startDate)) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "End date must be after start date.");
                return;
            }

            float totalPrice = Float.parseFloat(totalPriceText);
            User user = userService.getEmployeeById(7);

            VehicleRental rental = new VehicleRental();
            rental.setStartDate(startDate.atStartOfDay());
            rental.setEndDate(endDate.atStartOfDay());
            rental.setTotalPrice(totalPrice);
            rental.setVehicle(selectedVehicle);
            rental.setUser(user);

            vehicleRentalService.add(rental);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle rental booked successfully!");
            clearFields();

            // Fermer la fenêtre après succès
            Stage stage = (Stage) addRentalButton.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Total price must be a valid number.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void handleCloseForm() {
        Stage stage = (Stage) closeIcon.getScene().getWindow();
        stage.close();
    }

    private void clearFields() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        totalPriceField.clear();

    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
