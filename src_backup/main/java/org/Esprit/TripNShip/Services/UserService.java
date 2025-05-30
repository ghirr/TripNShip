package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class UserService implements IService<User> {

    private final Connection connection ;

    public UserService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(User user) {
        if(user instanceof Employee){
            this.addEmployee((Employee) user);
        }
        else if(user instanceof Client){
            this.addClient((Client) user);
        }
        else if (user instanceof Transporter) {
            addTransporter((Transporter) user);
        }
    }

    @Override
    public void update(User user) {
        if(user instanceof Employee){
            this.updateEmployee((Employee) user);
        }
        else if(user instanceof Client){
            this.updateClient((Client) user);
        }
        else if (user instanceof Transporter) {
            updateTransporter((Transporter) user);
        }

    }

    @Override
    public void delete(User user) {

    String req = "DELETE FROM user WHERE idUser=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, user.getIdUser());
            pst.executeUpdate();
            System.out.println("User deleted successfully !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<User> getAll() {

        List<User> users = new ArrayList<>();

        String req = "SELECT * FROM user";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                users.add(new User(rs.getInt("idUser"), rs.getString("firstName"), rs.getString("lastName"),
                                    Gender.valueOf(rs.getString("gender")), Role.valueOf(rs.getString("role")),
                        rs.getString("email"),rs.getString("profilePhoto"),
                        rs.getDate("birthdayDate").toLocalDate().atStartOfDay(),
                        rs.getString("phoneNumber")));

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return users;
    }


    private void addClient(Client client) {
        String req = "INSERT INTO User (\n" +
                "    firstName, lastName, gender, role, email, password, profilePhoto," +
                "birthdayDate, phoneNumber )"+
                " VALUES (?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1, client.getFirstName());
            ps.setString(2, client.getLastName());
            ps.setString(3, client.getGender().toString());
            ps.setString(4, client.getRole().toString());
            ps.setString(5, client.getEmail());
            ps.setString(6, client.getPassword());
            ps.setString(7, client.getProfilePhoto());
            ps.setString(8,client.getBirthdayDate().toString());
            ps.setString(9,client.getPhoneNumber());
            ps.executeUpdate();
            System.out.println("Client added !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public User getById(int id) {
        String req = "SELECT * FROM user WHERE idUser = ?";  // Note: using idUser, not id
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

                if ("CLIENT".equals(role)) {
                    // Return a Client object
                    return new Client(
                            rs.getInt("idUser"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            Gender.valueOf(rs.getString("gender")),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("profilePhoto"),
                            rs.getTimestamp("birthdayDate").toLocalDateTime(),
                            rs.getString("phoneNumber")
                    );
                } else if ("TRANSPORTER".equals(role)) {
                    // Return a Transporter object
                    return new Transporter(
                            rs.getInt("idUser"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            Gender.valueOf(rs.getString("gender")),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("profilePhoto"),
                            rs.getTimestamp("birthdayDate").toLocalDateTime(),
                            rs.getString("phoneNumber"),
                            TransportType.valueOf(rs.getString("transportType")),
                            rs.getString("website")
                    );
                } else {
                    // For any other role, return a base User object
                    return new User(
                            rs.getInt("idUser"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            Gender.valueOf(rs.getString("gender")),
                            Role.valueOf(rs.getString("role")),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("profilePhoto"),
                            rs.getTimestamp("birthdayDate").toLocalDateTime(),
                            rs.getString("phoneNumber")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in getById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void addEmployee(Employee employee) {
        String req = "INSERT INTO User (\n" +
                "    firstName, lastName, gender, role, email, password, profilePhoto" +
                ",birthdayDate, phoneNumber ,address,salary,hireDate  )"+
                " VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1, employee.getFirstName());
            ps.setString(2, employee.getLastName());
            ps.setString(3, employee.getGender().toString());
            ps.setString(4, employee.getRole().toString());
            ps.setString(5, employee.getEmail());
            ps.setString(6, employee.getPassword());
            ps.setString(7, employee.getProfilePhoto());
            ps.setString(8,employee.getBirthdayDate().toString());
            ps.setString(9,employee.getPhoneNumber());
            ps.setString(10,employee.getAddress());
            ps.setDouble(11,employee.getSalary());
            ps.setString(12,employee.getHireDate().toString());
            ps.executeUpdate();
            System.out.println("Employee ajoutée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private void addTransporter(Transporter transporter) {
        String req = "INSERT INTO user (firstName, lastName, gender, role, email, password, profilePhoto, birthdayDate, phoneNumber, transportType, website) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, transporter.getFirstName());
            ps.setString(2, transporter.getLastName());
            ps.setString(3, transporter.getGender().name());
            ps.setString(4, transporter.getRole().name());
            ps.setString(5, transporter.getEmail());
            ps.setString(6, transporter.getPassword());
            ps.setString(7, transporter.getProfilePhoto());
            ps.setString(8, transporter.getBirthdayDate().toString());
            ps.setString(9, transporter.getPhoneNumber());
            ps.setString(10, transporter.getTransportType().name());
            ps.setString(11, transporter.getWebsite());
            ps.executeUpdate();
            System.out.println("Transporter added!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateClient(Client client) {

    String req = "UPDATE User SET " +
                    "firstName = ?, " +
                    "lastName = ?, " +
                    "gender = ?, " +
                    "role = ?, " +
                    "email = ?, " +
                    "password = ?, " +
                    "profilePhoto = ?, " +
                    "birthdayDate = ?, " +
                    "phoneNumber = ? " +
                    "WHERE idUser = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1, client.getFirstName());
            ps.setString(2, client.getLastName());
            ps.setString(3, client.getGender().toString());
            ps.setString(4, client.getRole().toString());
            ps.setString(5, client.getEmail());
            ps.setString(6, client.getPassword());
            ps.setString(7, client.getProfilePhoto());
            ps.setString(8,client.getBirthdayDate().toString());
            ps.setString(9,client.getPhoneNumber());
            ps.setInt(10,client.getIdUser());
            ps.executeUpdate();
            System.out.println("Client updated !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateEmployee(Employee employee) {
        String req = "UPDATE User SET " +
                "firstName = ?, " +
                "lastName = ?, " +
                "gender = ?, " +
                "role = ?, " +
                "email = ?, " +
                "password = ?, " +
                "profilePhoto = ?, " +
                "birthdayDate = ?, " +
                "phoneNumber = ? ," +
                "address = ? ," +
                "salary = ? ," +
                "hireDate = ? "+
                "WHERE idUser = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1, employee.getFirstName());
            ps.setString(2, employee.getLastName());
            ps.setString(3, employee.getGender().toString());
            ps.setString(4, employee.getRole().toString());
            ps.setString(5, employee.getEmail());
            ps.setString(6, employee.getPassword());
            ps.setString(7, employee.getProfilePhoto());
            ps.setString(8,employee.getBirthdayDate().toString());
            ps.setString(9,employee.getPhoneNumber());
            ps.setString(10,employee.getAddress());
            ps.setDouble(11,employee.getSalary());
            ps.setString(12,employee.getHireDate().toString());
            ps.setInt(13,employee.getIdUser());
            ps.executeUpdate();
            System.out.println("Employee updated !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private void updateTransporter(Transporter transporter) {
        String req = "UPDATE user SET firstName=?, lastName=?, gender=?, role=?, email=?, password=?, profilePhoto=?, birthdayDate=?, phoneNumber=?, transportType=?, website=? WHERE idUser=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, transporter.getFirstName());
            ps.setString(2, transporter.getLastName());
            ps.setString(3, transporter.getGender().name());
            ps.setString(4, transporter.getRole().name());
            ps.setString(5, transporter.getEmail());
            ps.setString(6, transporter.getPassword());
            ps.setString(7, transporter.getProfilePhoto());
            ps.setString(8, transporter.getBirthdayDate().toString());
            ps.setString(9, transporter.getPhoneNumber());
            ps.setString(10, transporter.getTransportType().name());
            ps.setString(11, transporter.getWebsite());
            ps.setInt(12, transporter.getIdUser());
            ps.executeUpdate();
            System.out.println("Transporter updated!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public List<Transporter> getAllTransporters() {
        List<Transporter> transporters = new ArrayList<>();
        String req = "SELECT * FROM user WHERE role = 'TRANSPORTER'";

        try (PreparedStatement pst = connection.prepareStatement(req)) {
            ResultSet rs = pst.executeQuery();
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
                        TransportType.valueOf(rs.getString("transportType")),
                        rs.getString("website")
                );
                transporters.add(transporter);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching transporters: " + e.getMessage());
        }

        return transporters;
    }

}
