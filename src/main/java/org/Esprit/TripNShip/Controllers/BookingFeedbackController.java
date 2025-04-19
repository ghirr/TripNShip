package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import org.Esprit.TripNShip.Entities.BookingFeedback;

public class BookingFeedbackController {

    @FXML
    private ToggleButton star1;
    @FXML
    private ToggleButton star2;
    @FXML
    private ToggleButton star3;
    @FXML
    private ToggleButton star4;
    @FXML
    private ToggleButton star5;

    @FXML
    private TextField commentField;

    @FXML
    private Button submitFeedbackButton;

    // ID de l'hébergement et de l'utilisateur (devrait être passé dynamiquement)
    private int idAccommodation = 1;  // Exemple d'ID d'hébergement
    private int idUser = 1;  // Exemple d'ID utilisateur

    @FXML
    private void handleSubmitFeedback() {
        // Récupérer la note sélectionnée
        int rating = getSelectedRating();

        // Récupérer le commentaire
        String comment = commentField.getText();

        // Créer un objet BookingFeedback
        BookingFeedback feedback = new BookingFeedback(0, idAccommodation, idUser, rating, comment);

        // Afficher ou traiter le feedback, par exemple enregistrer dans une base de données
        System.out.println(feedback);

        // Vous pouvez ajouter ici une logique pour enregistrer ce feedback dans une base de données
    }

    // Méthode pour obtenir la note sélectionnée
    private int getSelectedRating() {
        if (star1.isSelected()) return 1;
        if (star2.isSelected()) return 2;
        if (star3.isSelected()) return 3;
        if (star4.isSelected()) return 4;
        if (star5.isSelected()) return 5;
        return 0; // Pas de sélection
    }
}
