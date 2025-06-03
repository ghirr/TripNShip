package org.Esprit.TripNShip.Services;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.TypeAccommodation;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccommodationService implements IService<Accommodation> {

    private final Connection connection;
    private final Gson gson = new Gson(); // Instance réutilisable

    public AccommodationService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Accommodation accommodation) {
        String req = "INSERT INTO Accommodation (type, name, address, latitude, longitude, capacity, photosAccommodation) VALUES (?, ?, ?, ?, ?,?,?)";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, accommodation.getType().name());
            ps.setString(2, accommodation.getName());
            ps.setString(3, accommodation.getAddress());
            ps.setDouble(4, accommodation.getLatitude());
            ps.setDouble(5, accommodation.getLongitude());
            ps.setInt(6 ,accommodation.getCapacity());
            ps.setString(7, accommodation.getPhotosAccommodation());

            ps.executeUpdate();
            System.out.println("✅ Accommodation added!");
        } catch (SQLException e) {
            System.out.println("❌ Error while adding accommodation: " + e.getMessage());
        }
    }

    @Override
    public void update(Accommodation accommodation) {
        String req = "UPDATE Accommodation SET type=?, name=?, address=?,latitude=?,longitude=?, capacity=?, photosAccommodation=? WHERE idAccommodation=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, accommodation.getType().name());
            ps.setString(2, accommodation.getName());
            ps.setString(3, accommodation.getAddress());
            ps.setDouble(4, accommodation.getLatitude());
            ps.setDouble(5, accommodation.getLongitude());
            ps.setInt(6 ,accommodation.getCapacity());

            //String photosJson = gson.toJson(accommodation.getPhotosAccommodation());
            ps.setString(7, accommodation.getPhotosAccommodation());

            ps.setInt(8, accommodation.getIdAccommodation());

            //ps.executeUpdate();
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Accommodation updated!");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error while updating accommodation: " + e.getMessage());
        }
    }

    @Override
    public void delete(Accommodation accommodation) {
        String req = "DELETE FROM Accommodation WHERE idAccommodation=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, accommodation.getIdAccommodation());
            ps.executeUpdate();
            System.out.println("✅ Accommodation deleted!");
        } catch (SQLException e) {
            System.out.println("❌ Error while deleting accommodation: " + e.getMessage());
        }
    }

    @Override
    public List<Accommodation> getAll() {
        List<Accommodation> accommodations = new ArrayList<>();
        String req = "SELECT * FROM Accommodation";
        String folderPath = "C:\\Users\\YJAZIRI\\Desktop\\AllRoomImage";
        String baseUrl = "http://localhost/tripnship/roomImages/";
        try (PreparedStatement ps = connection.prepareStatement(req);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Accommodation accommodation = new Accommodation(
                        rs.getInt("idAccommodation"),
                        TypeAccommodation.valueOf(rs.getString("type").toUpperCase()),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getInt("capacity"),
                        rs.getString("photosAccommodation") // simple String, pas de Gson
                );
                accommodations.add(accommodation);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error while retrieving accommodations: " + e.getMessage());
        }

        return accommodations;
    }

}
