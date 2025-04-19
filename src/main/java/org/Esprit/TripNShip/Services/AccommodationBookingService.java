package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.AccommodationBooking;
import org.Esprit.TripNShip.Entities.BookingStatus;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            ps.setInt(1, booking.getIdUser());
            ps.setInt(2, booking.getRoomId());
            ps.setDate(3, new java.sql.Date(booking.getStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(booking.getEndDate().getTime()));
            ps.setString(5, booking.getStatus().name());  // Convert enum to String

            ps.executeUpdate();
            System.out.println("AccommodationBooking added!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(AccommodationBooking booking) {
        String req = "UPDATE AccommodationBooking SET idUser=?, roomId=?, startDate=?, endDate=?, status=? WHERE idBooking=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, booking.getIdUser());
            ps.setInt(2, booking.getRoomId());
            ps.setDate(3, new java.sql.Date(booking.getStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(booking.getEndDate().getTime()));
            ps.setString(5, booking.getStatus().name());  // Convert enum to String
            ps.setInt(6, booking.getIdReservation());

            ps.executeUpdate();
            System.out.println("AccommodationBooking updated!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(AccommodationBooking booking) {
        String req = "DELETE FROM AccommodationBooking WHERE idBooking=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, booking.getIdReservation());
            ps.executeUpdate();
            System.out.println("AccommodationBooking deleted!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<AccommodationBooking> getAll() {
        List<AccommodationBooking> booking = new ArrayList<>();
        String req = "SELECT * FROM AccommodationBooking";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                booking.add(new AccommodationBooking(
                        rs.getInt("idBooking"),
                        rs.getInt("idUser"),
                        rs.getInt("roomId"),
                        rs.getDate("startDate"),
                        rs.getDate("endDate"),
                        BookingStatus.valueOf(rs.getString("status"))  // Convert String to enum
                ));
            }
        } catch (SQLException e) {
            //System.out.println(e.getMessage());
            System.out.println("Error while retrieving accommodations: " + e.getMessage());
        }
        return booking;
    }
}
