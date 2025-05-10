package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.TypeAccommodation;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccommodationService implements IService<Accommodation> {

    private final Connection connection;

    public AccommodationService() {

        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Accommodation accommodation) {
        String req = "INSERT INTO Accommodation (type, name, address, priceNight, capacity) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1, accommodation.getType().name());
            ps.setString(2, accommodation.getName());
            ps.setString(3, accommodation.getAddress());
            ps.setDouble(4, accommodation.getPriceNight());
            ps.setInt(5, accommodation.getCapacity());
            ps.executeUpdate();
            System.out.println("Accommodation added!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Accommodation accommodation) {
        String req = "UPDATE Accommodation SET type=?, name=?, address=?, capacity=?, priceNight=? WHERE idAccommodation=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1, accommodation.getType().name()); // Convertir enum en String
            ps.setString(2, accommodation.getName());
            ps.setString(3, accommodation.getAddress());
            ps.setInt(4, accommodation.getCapacity());
            ps.setDouble(5, accommodation.getPriceNight());
            ps.setInt(6, accommodation.getIdAccommodation());

            ps.executeUpdate();
            System.out.println("Accommodation updated!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean delete(Accommodation accommodation) {
        String req = "DELETE FROM Accommodation WHERE idAccommodation=?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, accommodation.getIdAccommodation());
            ps.executeUpdate();
            System.out.println("Accommodation deleted!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public List<Accommodation> getAll() {
        List<Accommodation> accommodations = new ArrayList<>();
        String req = "SELECT * FROM Accommodation";

        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Accommodation accommodation = new Accommodation(
                        rs.getInt("idAccommodation"),
                        TypeAccommodation.valueOf(rs.getString("type").toUpperCase()), // safely map string to enum
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getFloat("priceNight"), // keep only what you need
                        rs.getInt("capacity")

                );
                accommodations.add(accommodation);
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving accommodations: " + e.getMessage());
        }

        return accommodations;
    }

}
