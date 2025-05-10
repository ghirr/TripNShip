package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccommodationBookingService implements IService<AccommodationBooking> {

    private final Connection connection;

    public AccommodationBookingService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(AccommodationBooking booking) {
        String req = "INSERT INTO AccommodationBooking (idUser, roomId, startDate, endDate, status) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, booking.getUser().getIdUser());
            ps.setInt(2, booking.getRoom().getIdRoom()); // ✅ Utiliser l'objet Room
            ps.setDate(3, new java.sql.Date(booking.getStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(booking.getEndDate().getTime()));
            ps.setString(5, booking.getStatus().name());

            ps.executeUpdate();
            System.out.println("AccommodationBooking added!");
        } catch (SQLException e) {
            System.out.println("Error while adding booking: " + e.getMessage());
        }
    }

    @Override
    public void update(AccommodationBooking booking) {
        String req = "UPDATE AccommodationBooking SET idUser=?, roomId=?, startDate=?, endDate=?, status=? WHERE idBooking=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, booking.getUser().getIdUser());
            ps.setInt(2, booking.getRoom().getIdRoom()); // ✅ Utiliser l'objet Room
            ps.setDate(3, new java.sql.Date(booking.getStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(booking.getEndDate().getTime()));
            ps.setString(5, booking.getStatus().name());
            ps.setInt(6, booking.getIdBooking());

            ps.executeUpdate();
            System.out.println("AccommodationBooking updated!");
        } catch (SQLException e) {
            System.out.println("Error while updating booking: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(AccommodationBooking booking) {
        String req = "DELETE FROM AccommodationBooking WHERE idBooking=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, booking.getIdBooking());
            ps.executeUpdate();
            System.out.println("AccommodationBooking deleted!");
        } catch (SQLException e) {
            System.out.println("Error while deleting booking: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<AccommodationBooking> getAll() {
        List<AccommodationBooking> bookings = new ArrayList<>();
        String req = "SELECT ab.*, r.nameRoom, r.type FROM AccommodationBooking ab JOIN Room r ON ab.roomId = r.idRoom";
        try (PreparedStatement ps = connection.prepareStatement(req);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Construire l’objet Room
                Room room = new Room();
                room.setIdRoom(rs.getInt("roomId"));
                room.setNameRoom(rs.getString("nameRoom"));
                room.setType(TypeRoom.valueOf(rs.getString("type")));

                // Récupérer l'utilisateur associé à cette réservation
                User user = getUserById(rs.getInt("idUser"));

                // Construire l’objet AccommodationBooking
                AccommodationBooking booking = new AccommodationBooking(
                        rs.getInt("idBooking"),
                        user,
                        room,
                        rs.getDate("startDate"),
                        rs.getDate("endDate"),
                        BookingStatus.valueOf(rs.getString("status"))
                );

                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving bookings: " + e.getMessage());
        }
        return bookings;
    }

    // Méthode pour récupérer l'utilisateur par ID
    private User getUserById(int id) {
        User user = null;

        String query = "SELECT * FROM User WHERE id = ?";  // Assurez-vous que la requête utilise le bon nom de colonne

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String genderStr = rs.getString("gender").toUpperCase();  // Convertir en majuscules
                Gender gender = Gender.valueOf(genderStr);

                Timestamp birthdayTimestamp = rs.getTimestamp("birthdayDate");
                LocalDateTime birthdayDate = (birthdayTimestamp != null) ? birthdayTimestamp.toLocalDateTime() : null;

                user = new User(
                        rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        gender,
                        Role.valueOf(rs.getString("role")),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("profilePhoto"),
                        birthdayDate, // Utiliser la valeur vérifiée
                        rs.getString("phoneNumber")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }





}


