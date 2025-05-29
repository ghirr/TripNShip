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
        List<CircuitBooking> circuitBookings = new ArrayList<>();
        String req = "SELECT * FROM circuitbooking";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getInt("idUser"));
                TourCircuit circuit = new TourCircuit(rs.getInt("idCircuit"), rs.getString("nameCircuit"), null, 0f, null, null, null, null);

                CircuitBooking circuitBooking = new CircuitBooking(
                        rs.getInt("idBooking"),
                        rs.getTimestamp("bookingDate").toLocalDateTime(),
                        StatusBooking.values()[rs.getInt("statusBooking")],
                        user,
                        circuit
                );
                circuitBookings.add(circuitBooking);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return circuitBookings;
    }
}
