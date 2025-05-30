package org.Esprit.TripNShip.Services;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.Esprit.TripNShip.Entities.Client;
import org.Esprit.TripNShip.Entities.Role;
import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Utils.MyDataBase;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private Connection cnx = MyDataBase.getInstance().getConnection();

    public User login(String email, String password) {
        String query = "SELECT id, email, password, role FROM user WHERE email = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, email);

            ResultSet resultSet = pst.executeQuery();

            if (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        Role.valueOf(resultSet.getString("role")) // Convertit la chaîne de la BD en Role ENUM
                );
                if(BCrypt.checkpw(password, user.getPassword())) {
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Si aucun utilisateur trouvé
    }

    public User googleLogin(String email) {
        String query = "SELECT firstName,lastName,role,profilePhoto FROM user WHERE email = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, email);

            ResultSet resultSet = pst.executeQuery();

            if (resultSet.next()) {
                User user = new User(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        Role.valueOf(resultSet.getString(3)),
                        email,
                        resultSet.getString(4)
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
