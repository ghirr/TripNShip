package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.BookingFeedback;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingFeedbackService {

    private final Connection connection;

    public BookingFeedbackService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    // Vérifie si le feedback est autorisé (la réservation doit être terminée)
    public boolean canLeaveFeedback(int userId, int accommodationId) {
        String req = "SELECT endDate FROM Booking WHERE userId = ? AND accommodationId = ? ORDER BY endDate DESC LIMIT 1";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, userId);
            ps.setInt(2, accommodationId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Date endDate = rs.getDate("endDate");
                return endDate != null && endDate.toLocalDate().isBefore(LocalDate.now());
            }
        } catch (SQLException e) {
            System.out.println("Error checking booking end date: " + e.getMessage());
        }
        return false;
    }

    // Ajouter un feedback
    public void addFeedback(BookingFeedback feedback) {
        if (!canLeaveFeedback(feedback.getIdUser(), feedback.getIdAccommodation())) {
            System.out.println("You can only leave feedback after your stay is over.");
            return;
        }

        String req = "INSERT INTO BookingFeedback (userId, accommodationId, rating, comment) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, feedback.getIdUser());
            ps.setInt(2, feedback.getIdAccommodation());
            ps.setInt(3, feedback.getRating());
            ps.setString(4, feedback.getComment());

            ps.executeUpdate();
            System.out.println("Feedback added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding feedback: " + e.getMessage());
        }
    }

    // Modifier un feedback
    public void updateFeedback(BookingFeedback feedback) {
        String req = "UPDATE BookingFeedback SET rating = ?, comment = ? WHERE idFeedback = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, feedback.getRating());
            ps.setString(2, feedback.getComment());
            ps.setInt(3, feedback.getIdFeedback());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Feedback updated successfully!");
            } else {
                System.out.println("Feedback not found or not updated.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating feedback: " + e.getMessage());
        }
    }

    // Supprimer un feedback
    public void deleteFeedback(int idFeedback) {
        String req = "DELETE FROM BookingFeedback WHERE idFeedback = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, idFeedback);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Feedback deleted successfully!");
            } else {
                System.out.println("No feedback found with the given ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting feedback: " + e.getMessage());
        }
    }

    // Récupérer tous les feedbacks
    public List<BookingFeedback> getAllFeedbacks() {
        List<BookingFeedback> feedbacks = new ArrayList<>();
        String req = "SELECT * FROM BookingFeedback";

        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BookingFeedback feedback = new BookingFeedback(
                        rs.getInt("idFeedback"),
                        rs.getInt("userId"),
                        rs.getInt("accommodationId"),
                        rs.getInt("rating"),
                        rs.getString("comment")
                );
                feedbacks.add(feedback);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving feedbacks: " + e.getMessage());
        }

        return feedbacks;
    }
}

