package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Entities.TypeRoom;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomService implements IService<Room> {

    private final Connection connection;

    public RoomService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Room room) {
        String req = "INSERT INTO Room (idAccommodation, type, nameRoom, price, availability) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, room.getAccommodation().getIdAccommodation());

            ps.setString(2, room.getType().name());
            ps.setString(3, room.getNameRoom());
            ps.setDouble(4, room.getPrice());
            // Insertion de true ou false directement pour availability
            ps.setBoolean(5, room.isAvailability()); // utilisation de setBoolean
            ps.executeUpdate();
            System.out.println("Room added!");
        } catch (SQLException e) {
            System.out.println("Error while adding room: " + e.getMessage());
        }
    }


    @Override
    public void update(Room room) {
        String req = "UPDATE Room SET idAccommodation=?, type=?, nameRoom=?, price=?, availability=? WHERE idRoom=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {

            // Vérification si la connexion est valide avant de continuer
            if (connection == null || connection.isClosed()) {
                throw new SQLException("Database connection is not valid.");
            }

            // Configuration des paramètres de la requête
            ps.setInt(1, room.getAccommodation().getIdAccommodation());
            ps.setString(2, room.getType().name());
            ps.setString(3, room.getNameRoom());
            ps.setDouble(4, room.getPrice());
            ps.setBoolean(5, room.isAvailability());
            ps.setInt(6, room.getIdRoom());

            // Exécution de la mise à jour
            int affectedRows = ps.executeUpdate();

            // Vérification si des lignes ont été mises à jour
            if (affectedRows > 0) {
                System.out.println("Room updated successfully!");
            } else {
                System.out.println("No room found with the given id.");
            }

        } catch (SQLException e) {
            // Gestion détaillée des erreurs
            System.err.println("Error while updating room: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Room room) {
        String req = "DELETE FROM Room WHERE idRoom=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, room.getIdRoom());
            ps.executeUpdate();
            System.out.println("Room deleted!");
        } catch (SQLException e) {
            System.out.println("Error while deleting room: " + e.getMessage());
        }
    }

        @Override
        public List<Room> getAll() {
            List<Room> rooms = new ArrayList<>();
            String req = "SELECT * FROM Room";

            try (PreparedStatement ps = connection.prepareStatement(req);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    // Récupérer l'ID de l'Accommodation
                    int idAccommodation = rs.getInt("idAccommodation");

                    // Créer l'objet Accommodation (tu peux soit le récupérer dans une liste existante,
                    // soit effectuer une autre requête pour récupérer l'Accommodation complet)
                    Accommodation accommodation = getAccommodationById(idAccommodation);
                    boolean availability = rs.getBoolean("availability");

                    // Créer la Room
                    Room room = new Room(
                            rs.getInt("idRoom"),
                            accommodation,
                            TypeRoom.valueOf(rs.getString("type").toUpperCase()),
                            rs.getString("nameRoom"),
                            rs.getDouble("price"),
                            availability
                    );
                    rooms.add(room);
                }
            } catch (SQLException e) {
                System.out.println("Error while retrieving rooms: " + e.getMessage());
            }

            return rooms;
        }

    private Accommodation getAccommodationById(int idAccommodation) {
        AccommodationService accommodationService = new AccommodationService();
        List<Accommodation> accommodations = accommodationService.getAll();  // Récupère la liste des accommodations

        for (Accommodation accommodation : accommodations) {
            if (accommodation.getIdAccommodation() == idAccommodation) {
                return accommodation;
            }
        }
        return null;
    }

}
