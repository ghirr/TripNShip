package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleRentalService implements IService<VehicleRental> {

    private final Connection connection;
    private final VehicleService vehicleService;
    private final RentalAgencyService rentalAgencyService;

    public VehicleRentalService() {
        connection = MyDataBase.getInstance().getConnection();
        vehicleService = new VehicleService();
        rentalAgencyService = new RentalAgencyService();
    }

    @Override
    public void add(VehicleRental vehicleRental) {
        String req = "INSERT INTO vehiclerental (startDate, endDate, totalPrice, statusCircuit, idVehicle, idAgency) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setTimestamp(1, Timestamp.valueOf(vehicleRental.getStartDate()));
            pst.setTimestamp(2, Timestamp.valueOf(vehicleRental.getEndDate()));
            pst.setFloat(3, vehicleRental.getTotalPrice());
            pst.setInt(4, vehicleRental.getStatusCircuit().ordinal());
            pst.setInt(5, vehicleRental.getVehicle().getIdVehicle());
            pst.setInt(6, vehicleRental.getRentalAgency().getIdAgency());
            pst.executeUpdate();
            System.out.println("Vehicle Rental added!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(VehicleRental vehicleRental) {
        String req = "UPDATE vehiclerental SET startDate=?, endDate=?, totalPrice=?, statusCircuit=?, idVehicle=?, idAgency=? WHERE idRental=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setTimestamp(1, Timestamp.valueOf(vehicleRental.getStartDate()));
            pst.setTimestamp(2, Timestamp.valueOf(vehicleRental.getEndDate()));
            pst.setFloat(3, vehicleRental.getTotalPrice());
            pst.setInt(4, vehicleRental.getStatusCircuit().ordinal());
            pst.setInt(5, vehicleRental.getVehicle().getIdVehicle());
            pst.setInt(6, vehicleRental.getRentalAgency().getIdAgency());
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
                Vehicle vehicle = new Vehicle(rs.getInt("idVehicle"));
                RentalAgency rentalAgency = new RentalAgency(rs.getInt("idAgency"));

                VehicleRental vehicleRental = new VehicleRental(
                        rs.getInt("idRental"),
                        rs.getTimestamp("startDate").toLocalDateTime(),
                        rs.getTimestamp("endDate").toLocalDateTime(),
                        rs.getFloat("totalPrice"),
                        StautCircuit.values()[rs.getInt("statusCircuit")],
                        vehicle,
                        rentalAgency

                );
                vehicleRentals.add(vehicleRental);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return vehicleRentals;
    }

}
