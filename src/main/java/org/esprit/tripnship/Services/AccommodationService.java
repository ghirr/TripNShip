package org.esprit.tripnship.Services;

import org.esprit.tripnship.Entities.Accommodation;
import org.esprit.tripnship.Entities.TypeAccommodation;
import org.esprit.tripnship.Utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccommodationService implements IService<Accommodation> {

    private final Connection connection;

    public AccommodationService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Accommodation accommodation) {
        String req = "INSERT INTO Accommodation (type, name, address, capacity, note, priceNight) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1, accommodation.getType().name());
            ps.setString(2, accommodation.getName());
            ps.setString(3, accommodation.getAddress());
            ps.setInt(4, accommodation.getCapacity());
            ps.setString(5, accommodation.getNote());
            ps.executeUpdate();
            System.out.println("Accommodation added!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Accommodation accommodation) {
        String req = "UPDATE Accommodation SET type=?, name=?, address=?, capacity=?, note=?, priceNight=? WHERE idAccommodation=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1, accommodation.getType().name()); // Convertir enum en String
            ps.setString(2, accommodation.getName());
            ps.setString(3, accommodation.getAddress());
            ps.setInt(4, accommodation.getCapacity());
            ps.setString(5, accommodation.getNote());
            ps.setInt(7, accommodation.getIdAccommodation());

            ps.executeUpdate();
            System.out.println("Accommodation updated!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Accommodation accommodation) {
        String req = "DELETE FROM Accommodation WHERE idAccommodation=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, accommodation.getIdAccommodation());
            ps.executeUpdate();
            System.out.println("Accommodation deleted!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Accommodation> getAll() {
        List<Accommodation> accommodations = new ArrayList<>();
        String req = "SELECT * FROM Accommodation";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                accommodations.add(new Accommodation(
                        rs.getInt("idAccommodation"),
                        TypeAccommodation.valueOf(rs.getString("type")), // Convertir String en enum
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getInt("capacity"),
                        rs.getString("note")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return accommodations;
    }
}
