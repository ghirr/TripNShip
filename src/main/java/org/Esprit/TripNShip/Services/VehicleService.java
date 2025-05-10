package org.Esprit.TripNShip.Services;


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
        String req = "INSERT INTO vehicle (brand, model, licensePlate, dailyPrice, availability, type, idAgency) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, vehicle.getBrand());
            pst.setString(2, vehicle.getModel());
            pst.setString(3, vehicle.getLicensePlate());
            pst.setFloat(4, vehicle.getDailyPrice());
            pst.setBoolean(5, vehicle.isAvailability());
            pst.setInt(6, vehicle.getType().ordinal());
            pst.setInt(7, vehicle.getIdAgency());
            pst.executeUpdate();
            System.out.println("Vehicle added!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Vehicle vehicle) {
        String req = "UPDATE vehicle SET brand=?, model=?, licensePlate=?, dailyPrice=?, availability=?, type=?, idAgency=? WHERE idVehicle=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, vehicle.getBrand());
            pst.setString(2, vehicle.getModel());
            pst.setString(3, vehicle.getLicensePlate());
            pst.setFloat(4, vehicle.getDailyPrice());
            pst.setBoolean(5, vehicle.isAvailability());
            pst.setInt(6, vehicle.getType().ordinal());
            pst.setInt(7, vehicle.getIdAgency());
            pst.setInt(8, vehicle.getIdVehicle());
            pst.executeUpdate();
            System.out.println("Vehicle updated!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean delete(Vehicle vehicle) {
        String req = "DELETE FROM vehicle WHERE idVehicle=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, vehicle.getIdVehicle());
            pst.executeUpdate();
            System.out.println("Vehicle deleted!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
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
                        rs.getInt("idAgency")
                );
                vehicles.add(vehicle);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return vehicles;
    }
}
