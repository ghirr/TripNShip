package org.Esprit.TripNShip.Services;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;

import org.Esprit.TripNShip.Entities.Client;
import org.Esprit.TripNShip.Entities.Gender;
import org.Esprit.TripNShip.Entities.Role;
import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Utils.MyDataBase;
import org.mindrot.jbcrypt.BCrypt;

import static org.Esprit.TripNShip.Utils.Shared.getEnumOrNull;

public class AuthService {
    private Connection cnx = MyDataBase.getInstance().getConnection();

    public User login(String email, String password) {
        String query = "SELECT id, email, password, role,firstName,lastName,gender,profilePhoto,birthDayDate,phoneNumber FROM user WHERE email = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, email);

            ResultSet resultSet = pst.executeQuery();

            if (resultSet.next()) {

                LocalDateTime birthDateTime = null;
                Date birthDate = resultSet.getDate("birthDayDate");
                if (birthDate != null) {
                    birthDateTime = resultSet.getDate("birthDayDate").toLocalDate().atStartOfDay();
                }


                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        getEnumOrNull(Gender.class, resultSet.getString("gender")),
                        getEnumOrNull(Role.class, resultSet.getString("role")),
                        resultSet.getString("email"),
                        resultSet.getString("profilePhoto"),
                        birthDateTime,
                        resultSet.getString("phoneNumber")// Convertit la chaîne de la BD en Role ENUM
                );
                String passwordBD = resultSet.getString("password");
                if (passwordBD == null || passwordBD.isEmpty()) {
                    return null;
                }
                if(BCrypt.checkpw(password, passwordBD)) {
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Si aucun utilisateur trouvé
    }

    public void signUp(Client client) {
        String req = "INSERT INTO User (\n" +
                "    firstName, lastName, role, email, password)"+
                " VALUES (?,?,?,?,?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, client.getFirstName());
            ps.setString(2, client.getLastName());
            ps.setString(3, client.getRole().toString());
            ps.setString(4, client.getEmail());
            System.out.println(client.getPassword());
            ps.setString(5, BCrypt.hashpw(client.getPassword(), BCrypt.gensalt(10)));
            ps.executeUpdate();
            System.out.println("Client signed up !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public User googleLogin(String email) {
        String query = "SELECT id,firstName,lastName,role,profilePhoto FROM user WHERE email = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, email);

            ResultSet resultSet = pst.executeQuery();

            if (resultSet.next()) {
                User user = new User(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        Role.valueOf(resultSet.getString(4)),
                        email,
                        resultSet.getString(5)
                );

                return user;

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Si aucun utilisateur trouvé
    }

    public void signUpGoogle(Client client) {
        String req = "INSERT INTO User (\n" +
                "    firstName, lastName, role, email, profilePhoto)"+
                " VALUES (?,?,?,?,?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, client.getFirstName());
            ps.setString(2, client.getLastName());
            ps.setString(3, Role.CLIENT.toString());
            ps.setString(4, client.getEmail());
            ps.setString(5, client.getProfilePhoto());
            ps.executeUpdate();
            System.out.println("Client added !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
