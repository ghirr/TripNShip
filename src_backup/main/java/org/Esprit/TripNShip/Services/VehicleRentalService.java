package org.Esprit.TripNShip.Services;


import org.Esprit.TripNShip.Entities.StautCircuit;
import org.Esprit.TripNShip.Entities.VehicleRental;
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
        String req = "INSERT INTO vehiclerental (startDate, endDate, totalPrice, statusCircuit, idVehicle) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setTimestamp(1, Timestamp.valueOf(vehicleRental.getStartDate()));
            pst.setTimestamp(2, Timestamp.valueOf(vehicleRental.getEndDate()));
            pst.setFloat(3, vehicleRental.getTotalPrice());
            pst.setInt(4, vehicleRental.getStatus().ordinal());
            pst.setInt(5, vehicleRental.getIdVehicle());
            pst.executeUpdate();
            System.out.println("Vehicle Rental added!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(VehicleRental vehicleRental) {
        String req = "UPDATE vehiclerental SET startDate=?, endDate=?, totalPrice=?, statusCircuit=?, idVehicle=? WHERE idRental=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setTimestamp(1, Timestamp.valueOf(vehicleRental.getStartDate()));
            pst.setTimestamp(2, Timestamp.valueOf(vehicleRental.getEndDate()));
            pst.setFloat(3, vehicleRental.getTotalPrice());
            pst.setInt(4, vehicleRental.getStatus().ordinal());
            pst.setInt(5, vehicleRental.getIdVehicle());
            pst.setInt(6, vehicleRental.getIdRental());
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
                        rs.getInt("idVehicle")
                );
                vehicleRentals.add(vehicleRental);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return vehicleRentals;
    }
}
