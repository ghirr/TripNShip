package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Entities.TypeRoom;
import org.Esprit.TripNShip.Utils.MyDataBase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomService implements IService<Room> {

    private final Connection connection;
    private final Gson gson;

    public RoomService() {
        connection = MyDataBase.getInstance().getConnection();
        gson = new Gson();
    }

    @Override
    public void add(Room room) {
        String req = "INSERT INTO Room (idAccommodation, type, nameRoom, price, availability, photosRoom) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, room.getAccommodation().getIdAccommodation());
            ps.setString(2, room.getType().name());
            ps.setString(3, room.getNameRoom());
            ps.setDouble(4, room.getPrice());
            ps.setBoolean(5, room.isAvailability());

            // Sérialiser la liste de photos en JSON
            String photosJson = gson.toJson(room.getPhotosRoom());
            ps.setString(6, photosJson);

            ps.executeUpdate();
            System.out.println("✅ Room added!");
        } catch (SQLException e) {
            System.out.println("❌ Error while adding room: " + e.getMessage());
        }
    }

    @Override
    public void update(Room room) {
        String req = "UPDATE Room SET idAccommodation=?, type=?, nameRoom=?, price=?, availability=?, photosRoom=? WHERE idRoom=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            if (connection == null || connection.isClosed()) {
                throw new SQLException("Database connection is not valid.");
            }
            ps.setInt(1, room.getAccommodation().getIdAccommodation());
            ps.setString(2, room.getType().name());
            ps.setString(3, room.getNameRoom());
            ps.setDouble(4, room.getPrice());
            ps.setBoolean(5, room.isAvailability());

            String photosJson = gson.toJson(room.getPhotosRoom());
            ps.setString(6, photosJson);

            ps.setInt(7, room.getIdRoom());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Room updated successfully!");
            } else {
                System.out.println("⚠️ No room found with the given id.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error while updating room: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean delete(Room room) {
        String req = "DELETE FROM Room WHERE idRoom=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, room.getIdRoom());
            ps.executeUpdate();
            System.out.println("✅ Room deleted!");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Error while deleting room: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Room> getAll() {
        List<Room> rooms = new ArrayList<>();
        String req = "SELECT * FROM Room";

        try (PreparedStatement ps = connection.prepareStatement(req);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int idAccommodation = rs.getInt("idAccommodation");
                Accommodation accommodation = getAccommodationById(idAccommodation);
                boolean availability = rs.getBoolean("availability");

                // Désérialiser la liste JSON de photos
                String photosJson = rs.getString("photosRoom");
                Type listType = new TypeToken<List<String>>() {}.getType();
                List<String> photosRoom = gson.fromJson(photosJson, listType);
                if (photosRoom == null) photosRoom = new ArrayList<>();

                Room room = new Room(
                        rs.getInt("idRoom"),
                        accommodation,
                        TypeRoom.valueOf(rs.getString("type").toUpperCase()),
                        rs.getString("nameRoom"),
                        rs.getDouble("price"),
                        availability,
                        photosRoom
                );
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error while retrieving rooms: " + e.getMessage());
        }

        return rooms;
    }

    private Accommodation getAccommodationById(int idAccommodation) {
        AccommodationService accommodationService = new AccommodationService();
        List<Accommodation> accommodations = accommodationService.getAll();
        for (Accommodation accommodation : accommodations) {
            if (accommodation.getIdAccommodation() == idAccommodation) {
                return accommodation;
            }
        }
        return null;
    }

    // ✅ Méthode pour récupérer les chemins des photos à partir d’un dossier
    public List<String> getPhotosFromFolder(String folderPath) {
        List<String> photos = new ArrayList<>();
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("❌ Le dossier spécifié n'existe pas ou n'est pas un dossier : " + folderPath);
            return photos;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            System.out.println("❌ Impossible de lire le contenu du dossier : " + folderPath);
            return photos;
        }

        for (File file : files) {
            if (file.isFile() && isImageFile(file.getName())) {
                photos.add(file.getAbsolutePath());
            }
        }

        return photos;
    }

    private boolean isImageFile(String fileName) {
        String name = fileName.toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")
                || name.endsWith(".gif") || name.endsWith(".bmp");
    }

//    // ✅ Méthode pour récupérer les photos depuis les 3 dossiers en même temps
//    public List<String> getAllPhotosFromDefinedFolders() {
//        List<String> allPhotos = new ArrayList<>();
//        allPhotos.addAll(getPhotosFromFolder("C:\\Users\\YJAZIRI\\Desktop\\ho"));
//        allPhotos.addAll(getPhotosFromFolder("C:\\Users\\YJAZIRI\\Desktop\\AR"));
//        allPhotos.addAll(getPhotosFromFolder("C:\\Users\\YJAZIRI\\Desktop\\GU"));
//        return allPhotos;
//    }
}
