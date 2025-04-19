package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.Esprit.TripNShip.Utils.Shared.getEnumOrNull;


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
    }

    @Override
    public void update(User user) {
        if(user instanceof Employee){
            this.updateEmployee((Employee) user);
        }
        else if(user instanceof Client){
            this.updateClient((Client) user);
        }

    }

    @Override
    public void delete(User user) {

    String req = "DELETE FROM user WHERE id=?";
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
                LocalDateTime birthDateTime = null;
                Date birthDate = rs.getDate("birthDayDate");
                if (birthDate != null) {
                    birthDateTime = rs.getDate("birthDayDate").toLocalDate().atStartOfDay();
                }
                users.add(new User(rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        getEnumOrNull(Gender.class, rs.getString("gender")),
                        getEnumOrNull(Role.class, rs.getString("role")),
                        rs.getString("email"),
                        rs.getString("profilePhoto"),
                        birthDateTime,
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
                    "WHERE id = ?";
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
                "WHERE id = ?";
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
}
