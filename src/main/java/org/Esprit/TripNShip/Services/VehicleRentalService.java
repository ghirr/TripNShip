package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleRentalService implements IService<VehicleRental> {

    private final Connection connection;

    public VehicleRentalService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(VehicleRental vehicleRental) {
        String req = "INSERT INTO vehiclerental (startDate, endDate, totalPrice, statusCircuit, idVehicle, idUser) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);

            // Dates
            if (vehicleRental.getStartDate() != null) {
                pst.setTimestamp(1, Timestamp.valueOf(vehicleRental.getStartDate()));
            } else {
                pst.setNull(1, java.sql.Types.TIMESTAMP);
            }

            if (vehicleRental.getEndDate() != null) {
                pst.setTimestamp(2, Timestamp.valueOf(vehicleRental.getEndDate()));
            } else {
                pst.setNull(2, java.sql.Types.TIMESTAMP);
            }

            // Prix
            pst.setFloat(3, vehicleRental.getTotalPrice());

            // StatusCircuit
            if (vehicleRental.getStatusCircuit() != null) {
                pst.setInt(4, vehicleRental.getStatusCircuit().ordinal());
            } else {
                pst.setNull(4, java.sql.Types.INTEGER);
            }

            // Vehicle ID
            pst.setInt(5, vehicleRental.getVehicle().getIdVehicle());

            // User ID
            pst.setInt(6, vehicleRental.getUser().getIdUser());

            pst.executeUpdate();
            System.out.println("Vehicle Rental added!");

        } catch (SQLException e) {
            System.out.println("SQL Error while adding VehicleRental: " + e.getMessage());
        }
    }


    @Override
    public void update(VehicleRental vehicleRental) {
        String req = "UPDATE vehiclerental SET startDate=?, endDate=?, totalPrice=?, statusCircuit=?, idVehicle=?, idUser=? WHERE idRental=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setTimestamp(1, Timestamp.valueOf(vehicleRental.getStartDate()));
            pst.setTimestamp(2, Timestamp.valueOf(vehicleRental.getEndDate()));
            pst.setFloat(3, vehicleRental.getTotalPrice());
            pst.setInt(4, vehicleRental.getStatusCircuit().ordinal());
            pst.setInt(5, vehicleRental.getVehicle().getIdVehicle());
            pst.setInt(6, vehicleRental.getUser().getIdUser());
            pst.setInt(7, vehicleRental.getIdRental());
            pst.executeUpdate();
            System.out.println("Vehicle Rental updated!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(VehicleRental vehicleRental) {
        String req = "DELETE FROM vehiclerental WHERE idRental=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, vehicleRental.getIdRental());
            pst.executeUpdate();
            System.out.println("Vehicle Rental deleted!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<VehicleRental> getAll() {
        List<VehicleRental> vehicleRentals = new ArrayList<>();
        String req = "SELECT * FROM vehiclerental";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {

                VehicleRental vehicleRental = new VehicleRental(
                        rs.getInt("idRental"),
                        rs.getTimestamp("startDate").toLocalDateTime(),
                        rs.getTimestamp("endDate").toLocalDateTime(),
                        rs.getFloat("totalPrice"),
                        StautCircuit.values()[rs.getInt("statusCircuit")],
                        new Vehicle(rs.getInt("idVehicle")),
                        new User(rs.getInt("idUser"))

                );
                vehicleRentals.add(vehicleRental);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return vehicleRentals;
    }

    public List<VehicleRental> getVehicleRentalsByUserId(int userId) {
        List<VehicleRental> vehicleRentals = new ArrayList<>();
        String req = "SELECT vr.*, v.brand FROM vehiclerental vr " +
                "JOIN vehicle v ON vr.idVehicle = v.idVehicle " +
                "WHERE vr.idUser = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Vehicle vehicle = new Vehicle(
                        rs.getInt("idVehicle"),
                        rs.getString("brand")
                );

                VehicleRental vehicleRental = new VehicleRental(
                        rs.getInt("idRental"),
                        rs.getTimestamp("startDate").toLocalDateTime(),
                        rs.getTimestamp("endDate").toLocalDateTime(),
                        rs.getFloat("totalPrice"),
                        StautCircuit.values()[rs.getInt("statusCircuit")],
                        vehicle,
                        new User(rs.getInt("idUser"))
                );

                vehicleRentals.add(vehicleRental);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error while fetching VehicleRentals by userId: " + e.getMessage());
        }
        return vehicleRentals;
    }


}
