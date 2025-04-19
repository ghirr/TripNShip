package org.Esprit.TripNShip.Tests;

import org.Esprit.TripNShip.Entities.BookingFeedback;

import java.util.ArrayList;
import java.util.List;

public class TestBookingFeedbackCRUD {

    // Stockage en mémoire des commentaires
    private static final List<BookingFeedback> feedbacks = new ArrayList<>();

    public static void main(String[] args) {
        // Test CRUD pour BookingFeedback
        BookingFeedback feedback1 = new BookingFeedback(0, 1, 2, 4, "Great stay, very clean and friendly staff.");
        BookingFeedback feedback2 = new BookingFeedback(0, 2, 3, 5, "Excellent service, highly recommend!");
        BookingFeedback feedback3 = new BookingFeedback(0, 3, 4, 3, "Average stay, could be better.");

        // Ajouter des commentaires
        addFeedback(feedback1);
        addFeedback(feedback2);
        addFeedback(feedback3);

        System.out.println("📌 Liste des commentaires après ajout:");
        feedbacks.forEach(System.out::println);

        // Modifier le feedback1
        feedback1.setRating(5);
        feedback1.setComment("Updated comment: Excellent service!");
        updateFeedback(feedback1);

        System.out.println("\n📌 Liste des commentaires après mise à jour:");
        feedbacks.forEach(System.out::println);

        // Supprimer le feedback2
        deleteFeedback(feedback2);

        System.out.println("\n📌 Liste des commentaires après suppression:");
        feedbacks.forEach(System.out::println);
    }

    // Ajouter un commentaire
    public static void addFeedback(BookingFeedback feedback) {
        feedback.setIdFeedback(feedbacks.size() + 1);  // Définir un ID unique (basé sur la taille de la liste)
        feedbacks.add(feedback);
    }

    // Mettre à jour un commentaire
    public static void updateFeedback(BookingFeedback feedback) {
        for (int i = 0; i < feedbacks.size(); i++) {
            if (feedbacks.get(i).getIdFeedback() == feedback.getIdFeedback()) {
                feedbacks.set(i, feedback);  // Remplacer le feedback avec le feedback mis à jour
                return;
            }
        }
    }

    // Supprimer un commentaire
    public static void deleteFeedback(BookingFeedback feedback) {
        feedbacks.removeIf(f -> f.getIdFeedback() == feedback.getIdFeedback());  // Supprimer le feedback par ID
    }
}
