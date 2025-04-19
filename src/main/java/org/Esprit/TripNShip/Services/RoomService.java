package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Entities.TypeRoom;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomService implements IService<Room> {
    private final Connection connection;
    public RoomService() {
        connection = MyDataBase.getInstance().getConnection();
    }
    @Override
    public void add(Room r) {
        String query = "INSERT INTO room (idAccommodation, type, nameRoom, price, availability) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, r.getIdAccommodation());
            ps.setString(2, r.getType().name()); // Convert enum to String
            ps.setString(3, r.getNameRoom());
            ps.setDouble(4, r.getPrice());
            ps.setBoolean(5, r.isAvailability());
            ps.executeUpdate();
            System.out.println("Room added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding room: " + e.getMessage());
        }
    }

    @Override
    public void update(Room r) {
        String query = "UPDATE room SET idAccommodation=?, type=?, nameRoom=?, price=?, availability=? WHERE idRoom=?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, r.getIdAccommodation());
            ps.setString(2, r.getType().name()); // Convert enum to String
            ps.setString(3, r.getNameRoom());
            ps.setDouble(4, r.getPrice());
            ps.setBoolean(5, r.isAvailability());
            ps.setInt(6, r.getIdRoom());
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
                        rs.getInt("idAccommodation"),
                        TypeRoom.valueOf(rs.getString("type")),
                        rs.getString("nameRoom"),
                        rs.getBoolean("availability"),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving rooms: " + e.getMessage());
        }
        return rooms;
    }
}
