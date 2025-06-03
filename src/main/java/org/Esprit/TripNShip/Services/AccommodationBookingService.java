package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Utils.MyDataBase;
import org.Esprit.TripNShip.Utils.UserSession;

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
            ps.setInt(2, booking.getRoom().getIdRoom());
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
            ps.setInt(2, booking.getRoom().getIdRoom());
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
    public void delete(AccommodationBooking booking) {
        String req = "DELETE FROM AccommodationBooking WHERE idBooking=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, booking.getIdBooking());
            ps.executeUpdate();
            System.out.println("AccommodationBooking deleted!");
        } catch (SQLException e) {
            System.out.println("Error while deleting booking: " + e.getMessage());
        }
    }

    @Override
    public List<AccommodationBooking> getAll() {
        List<AccommodationBooking> bookings = new ArrayList<>();
        String req = "SELECT ab.*, r.nameRoom, r.type FROM AccommodationBooking ab JOIN Room r ON ab.roomId = r.idRoom";
        try (PreparedStatement ps = connection.prepareStatement(req);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setIdRoom(rs.getInt("roomId"));
                room.setNameRoom(rs.getString("nameRoom"));
                room.setType(TypeRoom.valueOf(rs.getString("type")));

                User user = getUserById(rs.getInt("idUser"));

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

    private User getUserById(int id) {
        User user = null;

        String query = "SELECT * FROM User WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String genderStr = rs.getString("gender").toUpperCase();
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
                        birthdayDate,
                        rs.getString("phoneNumber")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public List<AccommodationBooking> getBookingsByUserId(int userId) {
        List<AccommodationBooking> userBookings = new ArrayList<>();
        String req = "SELECT ab.*, r.nameRoom, r.type , r.price FROM AccommodationBooking ab " +
                "JOIN Room r ON ab.roomId = r.idRoom " +
                "WHERE ab.idUser = ? " +
                "ORDER BY ab.startDate DESC";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Room room = new Room();
                room.setIdRoom(rs.getInt("roomId"));
                room.setNameRoom(rs.getString("nameRoom"));
                room.setType(TypeRoom.valueOf(rs.getString("type")));
                room.setPrice(rs.getDouble("price"));
                User user = new User(UserSession.getInstance().getUserId(), UserSession.getInstance().getUserFirstName(), UserSession.getInstance().getUserLastName(), UserSession.getInstance().getUserRole(), UserSession.getInstance().getUserEmail());


                AccommodationBooking booking = new AccommodationBooking(
                        rs.getInt("idBooking"),
                        user,
                        room,
                        rs.getDate("startDate"),
                        rs.getDate("endDate"),
                        BookingStatus.valueOf(rs.getString("status"))
                );

                userBookings.add(booking);
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving user bookings: " + e.getMessage());
        }
        return userBookings;
    }


}
