package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceTransporter implements IService<Transporter> {

    private final Connection connection;

    public ServiceTransporter() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Transporter transporter) {
        String query = "INSERT INTO users (firstName, lastName, gender, role, email, password, profilePhoto, birthdayDate, phoneNumber, transportType, website) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, transporter.getFirstName());
            pst.setString(2, transporter.getLastName());
            pst.setString(3, transporter.getGender().name());
            pst.setString(4, Role.TRANSPORTER.name());
            pst.setString(5, transporter.getEmail());
            pst.setString(6, transporter.getPassword());
            pst.setString(7, transporter.getProfilePhoto());
            pst.setTimestamp(8, Timestamp.valueOf(transporter.getBirthdayDate()));
            pst.setString(9, transporter.getPhoneNumber());
            pst.setString(10, transporter.getTransportType().name());
            pst.setString(11, transporter.getWebsite());

            pst.executeUpdate();
            System.out.println("Transporter added!");
        } catch (SQLException e) {
            System.out.println("Error adding transporter: " + e.getMessage());
        }
    }

    @Override
    public void update(Transporter transporter) {
        String query = "UPDATE users SET firstName = ?, lastName = ?, gender = ?, email = ?, password = ?, profilePhoto = ?, birthdayDate = ?, phoneNumber = ?, transportType = ?, website = ? WHERE idUser = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, transporter.getFirstName());
            pst.setString(2, transporter.getLastName());
            pst.setString(3, transporter.getGender().name());
            pst.setString(4, transporter.getEmail());
            pst.setString(5, transporter.getPassword());
            pst.setString(6, transporter.getProfilePhoto());
            pst.setTimestamp(7, Timestamp.valueOf(transporter.getBirthdayDate()));
            pst.setString(8, transporter.getPhoneNumber());
            pst.setString(9, transporter.getTransportType().name());
            pst.setString(10, transporter.getWebsite());
            pst.setInt(11, transporter.getIdUser());

            pst.executeUpdate();
            System.out.println("Transporter updated!");
        } catch (SQLException e) {
            System.out.println("Error updating transporter: " + e.getMessage());
        }
    }

    @Override
    public void delete(Transporter transporter) {
        String query = "DELETE FROM users WHERE idUser = ? AND role = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, transporter.getIdUser());
            pst.setString(2, Role.TRANSPORTER.name());
            pst.executeUpdate();
            System.out.println("Transporter deleted!");
        } catch (SQLException e) {
            System.out.println("Error deleting transporter: " + e.getMessage());
        }
    }

    @Override
    public List<Transporter> getAll() {
        List<Transporter> transporters = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role = 'TRANSPORTER'";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Transporter transporter = new Transporter(
                        rs.getInt("idUser"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        Gender.valueOf(rs.getString("gender")),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("profilePhoto"),
                        rs.getTimestamp("birthdayDate").toLocalDateTime(),
                        rs.getString("phoneNumber"),
                        ShippingType.valueOf(rs.getString("transportType")),
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
