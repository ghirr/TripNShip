package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Utils.MyDataBase;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
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
                LocalDateTime hireDateTime = null;
                Date birthDate = rs.getDate("birthDayDate");
                Date hireDate = rs.getDate("hireDate");
                if (birthDate != null) {
                    birthDateTime = rs.getDate("birthDayDate").toLocalDate().atStartOfDay();
                }
                if (hireDate != null) {
                    hireDateTime = rs.getDate("hireDate").toLocalDate().atStartOfDay();
                }
                users.add(new User(
                        rs.getInt("id"),
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

    public List<Employee> getAllUsers() {

        List<Employee> users = new ArrayList<>();

        String req = "SELECT * FROM user";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                LocalDateTime birthDateTime = null;
                LocalDateTime hireDateTime = null;
                Date birthDate = rs.getDate("birthDayDate");
                Date hireDate = rs.getDate("hireDate");
                if (birthDate != null) {
                    birthDateTime = rs.getDate("birthDayDate").toLocalDate().atStartOfDay();
                }
                if (hireDate != null) {
                    hireDateTime = rs.getDate("hireDate").toLocalDate().atStartOfDay();
                }
                users.add(
                        new Employee(
                                rs.getInt("id"),
                                rs.getString("firstName"),
                                rs.getString("lastName"),
                                getEnumOrNull(Gender.class, rs.getString("gender")),
                                getEnumOrNull(Role.class, rs.getString("role")),
                                rs.getString("email"),
                                rs.getString("profilePhoto"),
                                birthDateTime,
                                rs.getString("phoneNumber"),
                                rs.getString("address"),
                                rs.getDouble("salary"),
                                hireDateTime));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return users;
    }

    public boolean getUserByEmail(String email) {
        String req = "SELECT * FROM user WHERE email = ?";

        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
        String req = "SELECT * FROM user WHERE id = ?";  // Note: using idUser, not id
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

                if ("CLIENT".equals(role)) {
                    // Return a Client object
                    return new Client(
                            rs.getInt("id"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            getEnumOrNull(Gender.class, rs.getString("gender")),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("profilePhoto"),
                            getLocalDateTimeOrNull(rs, "birthdayDate"),
                            rs.getString("phoneNumber")
                    );

                } else if ("TRANSPORTER".equals(role)) {
                    // Return a Transporter object
                    return new Transporter(
                            rs.getInt("id"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            getEnumOrNull(Gender.class, rs.getString("gender")),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("profilePhoto"),
                            rs.getTimestamp("birthdayDate").toLocalDateTime(),
                            rs.getString("phoneNumber"),
                            TransportEXType.valueOf(rs.getString("transportType"))
                    );
                } else {
                    // For any other role, return a base User object
                    return new User(
                            rs.getInt("id"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            getEnumOrNull(Gender.class, rs.getString("gender")),
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
                "    firstName, lastName, role, email, password" +
                ",address,salary,hireDate  )"+
                " VALUES (?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1, employee.getFirstName());
            ps.setString(2, employee.getLastName());
            ps.setString(3, employee.getRole().toString());
            ps.setString(4, employee.getEmail());
            ps.setString(5, BCrypt.hashpw(employee.getPassword(), BCrypt.gensalt()));
            ps.setString(6,employee.getAddress());
            ps.setDouble(7,employee.getSalary());
            ps.setString(8,employee.getHireDate().toString());
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
                "role = ?, " +
                "email = ?, " +
                "address = ? ," +
                "salary = ? ," +
                "gender = ?," +
                "profilePhoto = ?, " +
                "birthdayDate = ?, " +
                "phoneNumber = ? ," +
                "hireDate = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1, employee.getFirstName());
            ps.setString(2, employee.getLastName());
            ps.setString(3, employee.getRole().toString());
            ps.setString(4, employee.getEmail());
            ps.setString(5,employee.getAddress());
            ps.setDouble(6,employee.getSalary());
            if (employee.getGender() != null) {
                ps.setString(7, employee.getGender().toString());
            } else {
                ps.setNull(7, java.sql.Types.VARCHAR);
            }
            ps.setString(8,employee.getProfilePhoto());
            if (employee.getBirthdayDate() != null) {
                ps.setString(9, employee.getBirthdayDate().toString());
            } else {
                ps.setNull(9, java.sql.Types.VARCHAR);
            }
            ps.setString(10,employee.getPhoneNumber());
            if (employee.getHireDate() != null) {
                ps.setString(11, employee.getHireDate().toString());
            } else {
                ps.setNull(11, java.sql.Types.VARCHAR);
            }
            ps.setInt(12,employee.getIdUser());
            ps.executeUpdate();
            System.out.println("Employee updated !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private void updateTransporter(Transporter transporter) {
        String req = "UPDATE user SET firstName=?, lastName=?, gender=?, role=?, email=?, password=?, profilePhoto=?, birthdayDate=?, phoneNumber=?, transportType=?, website=? WHERE id=?";
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
                        rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        getEnumOrNull(Gender.class, rs.getString("gender")),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("profilePhoto"),
                        rs.getTimestamp("birthdayDate").toLocalDateTime(),
                        rs.getString("phoneNumber"),
                        TransportEXType.valueOf(rs.getString("transportType"))
                );
                transporters.add(transporter);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching transporters: " + e.getMessage());
        }

        return transporters;
    }


    public Employee getEmployeeById(int id) {
        String req = "SELECT * FROM User WHERE id = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, Integer.toString(id));
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                LocalDateTime birthDateTime = null;
                LocalDateTime hireDateTime = null;
                Date birthDate = rs.getDate("birthDayDate");
                Date hireDate = rs.getDate("hireDate");
                if (birthDate != null) {
                    birthDateTime = rs.getDate("birthDayDate").toLocalDate().atStartOfDay();
                }
                if (hireDate != null) {
                    hireDateTime = rs.getDate("hireDate").toLocalDate().atStartOfDay();
                }
                return new Employee(id,
                                    rs.getString("firstName"),
                                    rs.getString("lastName"),
                                    getEnumOrNull(Gender.class, rs.getString("gender")),
                                    getEnumOrNull(Role.class, rs.getString("role")),
                                    rs.getString("email"),
                                    rs.getString("profilePhoto"),
                                    birthDateTime,
                                    rs.getString("phoneNumber"),
                                    rs.getString("address"),
                                    rs.getDouble("salary"),
                                    hireDateTime
                            );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private LocalDateTime getLocalDateTimeOrNull(ResultSet rs, String columnLabel) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnLabel);
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}
