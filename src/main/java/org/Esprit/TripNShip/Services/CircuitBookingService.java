package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CircuitBookingService implements IService<CircuitBooking> {

    private final Connection connection;

    public CircuitBookingService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(CircuitBooking circuitBooking) {
        String req = "INSERT INTO circuitbooking (bookingDate, statusBooking, idUser, idCircuit) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setTimestamp(1, Timestamp.valueOf(circuitBooking.getBookingDate()));
            pst.setInt(2, circuitBooking.getStatusBooking().ordinal());
            pst.setInt(3, circuitBooking.getUser().getIdUser());
            pst.setInt(4, circuitBooking.getTourCircuit().getIdCircuit());
            pst.executeUpdate();
            System.out.println("Circuit Booking added!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(CircuitBooking circuitBooking) {
        String req = "UPDATE circuitbooking SET bookingDate=?, statusBooking=?, idUser=?, idCircuit=? WHERE idBooking=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setTimestamp(1, Timestamp.valueOf(circuitBooking.getBookingDate()));
            pst.setInt(2, circuitBooking.getStatusBooking().ordinal());
            pst.setInt(3, circuitBooking.getUser().getIdUser());
            pst.setInt(4, circuitBooking.getTourCircuit().getIdCircuit());
            pst.setInt(5, circuitBooking.getIdBooking());
            pst.executeUpdate();
            System.out.println("Circuit Booking updated!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(CircuitBooking circuitBooking) {
        String req = "DELETE FROM circuitbooking WHERE idBooking=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, circuitBooking.getIdBooking());
            pst.executeUpdate();
            System.out.println("Circuit Booking deleted!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<CircuitBooking> getAll() {
        List<CircuitBooking> bookings = new ArrayList<>();
        String req = """
        SELECT cb.*, u.id, u.firstName, u.lastName, 
               tc.idCircuit, tc.nameCircuit
        FROM circuitbooking cb
        JOIN user u ON cb.idUser = u.id
        JOIN tourcircuit tc ON cb.idCircuit = tc.idCircuit
        """;

        try (PreparedStatement pst = connection.prepareStatement(req);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                CircuitBooking booking = new CircuitBooking();
                booking.setIdBooking(rs.getInt("idBooking"));
                booking.setBookingDate(rs.getTimestamp("bookingDate").toLocalDateTime());

                int statusIndex = rs.getInt("statusBooking");
                if (statusIndex >= 0 && statusIndex < StatusBooking.values().length) {
                    booking.setStatusBooking(StatusBooking.values()[statusIndex]);
                } else {
                    booking.setStatusBooking(StatusBooking.Confirmed);
                }

                // Récupérer les données utilisateur
                User user = new User();
                user.setIdUser(rs.getInt("idUser"));
                user.setFirstName(rs.getString("firstName"));
                user.setLastName(rs.getString("lastName"));
                booking.setUser(user);

                // Récupérer les données du circuit
                TourCircuit circuit = new TourCircuit();
                circuit.setIdCircuit(rs.getInt("idCircuit"));
                circuit.setNameCircuit(rs.getString("nameCircuit"));
                booking.setTourCircuit(circuit);

                bookings.add(booking);
            }

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        return bookings;
    }

}
