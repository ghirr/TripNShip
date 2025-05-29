package org.Esprit.TripNShip.Services;


import org.Esprit.TripNShip.Entities.RentalAgency;
import org.Esprit.TripNShip.Entities.Type;
import org.Esprit.TripNShip.Entities.Vehicle;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehicleService implements IService<Vehicle> {

    private final Connection connection;

    public VehicleService() {
        connection = MyDataBase.getInstance().getConnection();
    }


    @Override
    public void add(Vehicle vehicle) {

        String req = "INSERT INTO vehicle (brand, model, licensePlate, dailyPrice, availability, type, idAgency, imageURL) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, vehicle.getBrand());
            pst.setString(2, vehicle.getModel());
            pst.setString(3, vehicle.getLicensePlate());
            pst.setFloat(4, vehicle.getDailyPrice());
            pst.setBoolean(5, vehicle.isAvailability());
            pst.setInt(6, vehicle.getType().ordinal());
            pst.setInt(7, vehicle.getRentalAgency().getIdAgency());
            pst.setString(8, vehicle.getImageURL());
            pst.executeUpdate();
            System.out.println("Vehicle added!");
        } catch (SQLException e) {
                System.out.println("Add vehicle error: " + e.getMessage());
        }
    }

    @Override
    public void update(Vehicle vehicle) {
        String req = "UPDATE vehicle SET brand=?, model=?, licensePlate=?, dailyPrice=?, availability=?, type=?, idAgency=?, imageURL=? WHERE idVehicle=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, vehicle.getBrand());
            pst.setString(2, vehicle.getModel());
            pst.setString(3, vehicle.getLicensePlate());
            pst.setFloat(4, vehicle.getDailyPrice());
            pst.setBoolean(5, vehicle.isAvailability());
            pst.setInt(6, vehicle.getType().ordinal());
            pst.setInt(7, vehicle.getRentalAgency().getIdAgency());
            pst.setString(8, vehicle.getImageURL());
            pst.setInt(9, vehicle.getIdVehicle());
            pst.executeUpdate();
            System.out.println("Vehicle updated!");
        } catch (SQLException e) {
            System.out.println("Update vehicle error: " + e.getMessage());
        }
    }

    @Override
    public void delete(Vehicle vehicle) {
        String req = "DELETE FROM vehicle WHERE idVehicle=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, vehicle.getIdVehicle());
            pst.executeUpdate();
            System.out.println("Vehicle deleted!");
        } catch (SQLException e) {
            System.out.println("Delete vehicle error: " + e.getMessage());
        }
    }

    @Override
    public List<Vehicle> getAll() {
        List<Vehicle> vehicles = new ArrayList<>();
        String req = "SELECT * FROM vehicle";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {

                Vehicle vehicle = new Vehicle(
                        rs.getInt("idVehicle"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getString("licensePlate"),
                        rs.getFloat("dailyPrice"),
                        rs.getBoolean("availability"),
                        Type.values()[rs.getInt("type")],
                        new RentalAgency(rs.getInt("idAgency")),
                        rs.getString("imageURL")
                );

                vehicles.add(vehicle);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return vehicles;
    }
}
