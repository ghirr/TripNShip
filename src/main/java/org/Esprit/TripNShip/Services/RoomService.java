package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Entities.TypeRoom;
import org.Esprit.TripNShip.Utils.MyDataBase;

import com.google.gson.Gson;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoomService implements IService<Room> {

    private final Connection connection;

    public RoomService() {
        connection = MyDataBase.getInstance().getConnection();
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
            ps.setString(6, room.getPhotosRoom());

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
            ps.setString(6, room.getPhotosRoom());

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
    public void delete(Room room) {
        String req = "DELETE FROM Room WHERE idRoom=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, room.getIdRoom());
            ps.executeUpdate();
            System.out.println("✅ Room deleted!");
        } catch (SQLException e) {
            System.out.println("❌ Error while deleting room: " + e.getMessage());
        }
    }

    @Override
    public List<Room> getAll() {
        List<Room> rooms = new ArrayList<>();
        String req = "SELECT * FROM Room";
        String folderPath = "C:\\Users\\YJAZIRI\\Desktop\\AllRoomImage";
        String baseUrl = "http://localhost/tripnship/roomImages/";

        try (PreparedStatement ps = connection.prepareStatement(req);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int idAccommodation = rs.getInt("idAccommodation");
                Accommodation accommodation = getAccommodationById(idAccommodation);
                boolean availability = rs.getBoolean("availability");

                Room room = new Room(
                        rs.getInt("idRoom"),
                        accommodation,
                        TypeRoom.valueOf(rs.getString("type").toUpperCase()),
                        rs.getString("nameRoom"),
                        rs.getDouble("price"),
                        availability,
                        rs.getString("photosRoom")// photosRoom vide pour l'instant
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

    private String findMatchingImageFileName(String roomName, String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) return null;

        File[] files = folder.listFiles((dir, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")
                    || lower.endsWith(".bmp") || lower.endsWith(".gif");
        });

        if (files != null) {
            for (File file : files) {
                String fileNameWithoutExtension = file.getName().replaceFirst("[.][^.]+$", "");
                if (fileNameWithoutExtension.equalsIgnoreCase(roomName)) {
                    return file.getName();
                }
            }
        }

        return null;
    }



}
