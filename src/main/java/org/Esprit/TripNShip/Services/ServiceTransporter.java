package org.Esprit.TripNShip.Services;



import org.Esprit.TripNShip.Entities.TransportType;
import org.Esprit.TripNShip.Entities.Transporter;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceTransporter implements IService<Transporter> {

    private Connection connection;

    public ServiceTransporter() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Transporter transporter) {
        String query = "INSERT INTO transporters (name, transportType, phone, email, website) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, transporter.getName());
            pst.setString(2, transporter.getTransportType().name()); // enum to String
            pst.setString(3, transporter.getPhone());
            pst.setString(4, transporter.getEmail());
            pst.setString(5, transporter.getWebsite());
            pst.executeUpdate();
            System.out.println("Transporter added!");
        } catch (SQLException e) {
            System.out.println("Error adding transporter: " + e.getMessage());
        }
    }

    @Override
    public void update(Transporter transporter) {
        String query = "UPDATE transporters SET name = ?, transportType = ?, phone = ?, email = ?, website = ? WHERE transporterId = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, transporter.getName());
            pst.setString(2, transporter.getTransportType().name()); // enum to String
            pst.setString(3, transporter.getPhone());
            pst.setString(4, transporter.getEmail());
            pst.setString(5, transporter.getWebsite());
            pst.setInt(6, transporter.getTransporterId());
            pst.executeUpdate();
            System.out.println("Transporter updated!");
        } catch (SQLException e) {
            System.out.println("Error updating transporter: " + e.getMessage());
        }
    }

    @Override
    public void delete(Transporter transporter) {
        String query = "DELETE FROM transporters WHERE transporterId = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, transporter.getTransporterId());
            pst.executeUpdate();
            System.out.println("Transporter deleted!");
        } catch (SQLException e) {
            System.out.println("Error deleting transporter: " + e.getMessage());
        }
    }

    @Override
    public List<Transporter> getAll() {
        List<Transporter> transporters = new ArrayList<>();
        String query = "SELECT * FROM transporters";

        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Transporter transporter = new Transporter(
                        rs.getInt("transporterId"),
                        rs.getString("name"),
                        TransportType.valueOf(rs.getString("transportType").toUpperCase()), // string to enum
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("website")
                );
                transporters.add(transporter);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving transporters: " + e.getMessage());
        }

        return transporters;
    }
}
