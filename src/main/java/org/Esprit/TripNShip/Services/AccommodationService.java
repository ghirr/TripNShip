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
        String req = "INSERT INTO Accommodation (type, name, address, priceNight, capacity, photos) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, accommodation.getType().name());
            ps.setString(2, accommodation.getName());
            ps.setString(3, accommodation.getAddress());
            ps.setDouble(4, accommodation.getPriceNight());
            ps.setInt(5, accommodation.getCapacity());

            // JSON serialize list of photos
            String photosJson = gson.toJson(accommodation.getPhotos());
            ps.setString(6, photosJson);

            ps.executeUpdate();
            System.out.println("✅ Accommodation added!");
        } catch (SQLException e) {
            System.out.println("❌ Error while adding accommodation: " + e.getMessage());
        }
    }

    @Override
    public void update(Accommodation accommodation) {
        String req = "UPDATE Accommodation SET type=?, name=?, address=?, capacity=?, priceNight=?, photos=? WHERE idAccommodation=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, accommodation.getType().name());
            ps.setString(2, accommodation.getName());
            ps.setString(3, accommodation.getAddress());
            ps.setInt(4, accommodation.getCapacity());
            ps.setDouble(5, accommodation.getPriceNight());

            String photosJson = gson.toJson(accommodation.getPhotos());
            ps.setString(6, photosJson);

            ps.setInt(7, accommodation.getIdAccommodation());

            ps.executeUpdate();
            System.out.println("✅ Accommodation updated!");
        } catch (SQLException e) {
            System.out.println("❌ Error while updating accommodation: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(Accommodation accommodation) {
        String req = "DELETE FROM Accommodation WHERE idAccommodation=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, accommodation.getIdAccommodation());
            ps.executeUpdate();
            System.out.println("✅ Accommodation deleted!");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Error while deleting accommodation: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Accommodation> getAll() {
        List<Accommodation> accommodations = new ArrayList<>();
        String req = "SELECT * FROM Accommodation";

        try (PreparedStatement ps = connection.prepareStatement(req);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String photosJson = rs.getString("photos");

                Type listType = new TypeToken<List<String>>() {}.getType();
                List<String> photos = (photosJson != null && !photosJson.isEmpty())
                        ? gson.fromJson(photosJson, listType)
                        : new ArrayList<>();

                Accommodation accommodation = new Accommodation(
                        rs.getInt("idAccommodation"),
                        TypeAccommodation.valueOf(rs.getString("type").toUpperCase()),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getFloat("priceNight"),
                        rs.getInt("capacity"),
                        photos
                );
                accommodations.add(accommodation);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error while retrieving accommodations: " + e.getMessage());
        }

        return accommodations;
    }

    // ✅ Méthode pour récupérer les chemins des photos dans un dossier
    public List<String> getImagePathsFromDirectory(String directoryPath) {
        List<String> photos = new ArrayList<>();
        File dir = new File(directoryPath);

        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile() && isImage(file.getName())) {
                    photos.add(file.getAbsolutePath());
                }
            }
        }
        return photos;
    }

    // ✅ Vérifie si le fichier est une image
    private boolean isImage(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                || lower.endsWith(".png") || lower.endsWith(".gif");
    }
}
