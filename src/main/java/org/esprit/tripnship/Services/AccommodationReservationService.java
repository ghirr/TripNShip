package org.esprit.tripnship.Services;

import org.esprit.tripnship.Entities.AccommodationReservation;
import org.esprit.tripnship.Entities.ReservationStatus;
import org.esprit.tripnship.Utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccommodationReservationService implements IService<AccommodationReservation> {

    private final Connection connection;

    public AccommodationReservationService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(AccommodationReservation reservation) {
        String req = "INSERT INTO AccommodationReservation (idUser, roomId, startDate, endDate, status) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, reservation.getIdUser());
            ps.setInt(2, reservation.getRoomId());
            ps.setDate(3, new java.sql.Date(reservation.getStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(reservation.getEndDate().getTime()));
            ps.setString(5, reservation.getStatus().name());  // Convert enum to String

            ps.executeUpdate();
            System.out.println("AccommodationReservation added!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(AccommodationReservation reservation) {
        String req = "UPDATE AccommodationReservation SET idUser=?, roomId=?, startDate=?, endDate=?, status=? WHERE idReservation=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, reservation.getIdUser());
            ps.setInt(2, reservation.getRoomId());
            ps.setDate(3, new java.sql.Date(reservation.getStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(reservation.getEndDate().getTime()));
            ps.setString(5, reservation.getStatus().name());  // Convert enum to String
            ps.setInt(6, reservation.getIdReservation());

            ps.executeUpdate();
            System.out.println("AccommodationReservation updated!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(AccommodationReservation reservation) {
        String req = "DELETE FROM AccommodationReservation WHERE idReservation=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, reservation.getIdReservation());
            ps.executeUpdate();
            System.out.println("AccommodationReservation deleted!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<AccommodationReservation> getAll() {
        List<AccommodationReservation> reservations = new ArrayList<>();
        String req = "SELECT * FROM AccommodationReservation";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reservations.add(new AccommodationReservation(
                        rs.getInt("idReservation"),
                        rs.getInt("idUser"),
                        rs.getInt("roomId"),
                        rs.getDate("startDate"),
                        rs.getDate("endDate"),
                        ReservationStatus.valueOf(rs.getString("status"))  // Convert String to enum
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return reservations;
    }
}
