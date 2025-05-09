package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.TypeAccommodation;
import org.Esprit.TripNShip.Services.AccommodationService;

public class AccommodationController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private Button saveButton;

    @FXML
    private TableView<Accommodation> accommodationsTable;

    @FXML
    private ChoiceBox<String> typeChoiceBox;

    @FXML
    private TextField priceField;

    @FXML
    private TextField capacityField;

    private Accommodation accommodation;

    private final AccommodationService accommodationService = new AccommodationService();


    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
        if (accommodation != null) {
            nameField.setText(accommodation.getName());
            addressField.setText(accommodation.getAddress());
            priceField.setText(String.valueOf(accommodation.getPriceNight()));
            capacityField.setText(String.valueOf(accommodation.getCapacity()));
            typeChoiceBox.setValue(accommodation.getType().name());
        }
    }

    @FXML
    private void initialize() {
        typeChoiceBox.setItems(FXCollections.observableArrayList("HOTEL", "AIRBNB", "GUESTHOUSE"));
        typeChoiceBox.setValue("HOTEL");
    }

    private void closeWindow() {
        ((Stage) priceField.getScene().getWindow()).close();
    }

    public void saveAccommodation(ActionEvent actionEvent) {
        String name = nameField.getText();
        String address = addressField.getText();
        String typeValue = typeChoiceBox.getValue();
        TypeAccommodation typeEnum = TypeAccommodation.valueOf(typeValue);
        float price = Float.parseFloat(priceField.getText());
        int capacity = Integer.parseInt(capacityField.getText());

        if (accommodation == null) {
            accommodation = new Accommodation();
        }

        accommodation.setName(name);
        accommodation.setAddress(address);
        accommodation.setType(typeEnum);
        accommodation.setPriceNight(price);
        accommodation.setCapacity(capacity);

        if (accommodation.getIdAccommodation() == 0) {
            accommodationService.add(accommodation);
            TableAccommodationController.getInstance().loadAccommodationsFromDatabase();
            showSuccessMessage("Accommodation added successfully!");
        } else {
            accommodationService.update(accommodation);
//            tableController.updateAccommodation();
            showSuccessMessage("Accommodation updated successfully!");
        }

        closeWindow();
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message); // ici 'message' est bien défini comme paramètre
        alert.showAndWait();
    }
}
