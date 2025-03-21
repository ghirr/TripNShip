package org.esprit.tripnship.Services;
import org.esprit.tripnship.Entities.Room;
import org.esprit.tripnship.Entities.TypeRoom;
import org.esprit.tripnship.Utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomService implements IService<Room> {
    private final Connection connection;
    public RoomService() {
        connection = MyDataBase.getInstance().getConnection();
    }
    @Override
    public void add(Room r) {
        String query = "INSERT INTO room (idAccommodation, type, price, availability) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, r.getIdAccommodation());
            ps.setString(2, r.getType().name()); // Convert enum to String
            ps.setDouble(3, r.getPrice());
            ps.setBoolean(4, r.isAvailability());
            ps.executeUpdate();
            System.out.println("Room added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding room: " + e.getMessage());
        }
    }

    @Override
    public void update(Room r) {
        String query = "UPDATE room SET idAccommodation=?, type=?, price=?, availability=? WHERE idRoom=?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, r.getIdAccommodation());
            ps.setString(2, r.getType().name()); // Convert enum to String
            ps.setDouble(3, r.getPrice());
            ps.setBoolean(4, r.isAvailability());
            ps.setInt(5, r.getIdRoom());
            ps.executeUpdate();
            System.out.println("Room updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating room: " + e.getMessage());
        }
    }

    @Override
    public void delete(Room r) {
        String query = "DELETE FROM room WHERE idRoom=?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, r.getIdRoom());
            ps.executeUpdate();
            System.out.println("Room deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error deleting room: " + e.getMessage());
        }
    }

    @Override
    public List<Room> getAll() {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM room";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rooms.add(new Room(
                        rs.getInt("idRoom"),
                        TypeRoom.valueOf(rs.getString("type")), // Convert String to enum
                        rs.getDouble("price"),
                        rs.getBoolean("availability"),
                        rs.getInt("idAccommodation")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving rooms: " + e.getMessage());
        }
        return rooms;
    }
}

