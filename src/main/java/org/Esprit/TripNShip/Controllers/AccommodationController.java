package org.Esprit.TripNShip.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Accommodation;

public class AccommodationController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private Button saveButton;
    @FXML
    private TableView<Accommodation> accommodationsTable;

    private Accommodation accommodation;
    private TableAccommodationController tableController;

    public void setTableController(TableAccommodationController controller) {
        this.tableController = controller;
    }

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
        if (accommodation != null) {
            nameField.setText(accommodation.getName());
            addressField.setText(accommodation.getAddress());
        }
    }

    @FXML
    private void initialize() {
      //  saveButton.setOnAction(e -> saveAccommodation());
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    public void saveAccommodation(ActionEvent actionEvent) {
        if (accommodation == null) {
            accommodation = new Accommodation();
            accommodation.setName(nameField.getText());
            accommodation.setAddress(addressField.getText());
            tableController.addAccommodation(accommodation);  // Ajouter un nouvel hébergement
        } else {
            accommodation.setName(nameField.getText());
            accommodation.setAddress(addressField.getText());
            tableController.updateAccommodation();  // Mettre à jour l'hébergement existant
        }
        closeWindow();  // Fermer la fenêtre une fois l'hébergement ajouté
    }
}
