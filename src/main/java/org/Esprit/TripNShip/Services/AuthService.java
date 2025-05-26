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

import static org.Esprit.TripNShip.Utils.Shared.getEnumOrNull;

public class AuthService {
    private final Connection cnx = MyDataBase.getInstance().getConnection();

    public User login(String email, String password) {
        String query = "SELECT id, password, role,firstName,lastName,profilePhoto FROM user WHERE email = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, email);

            ResultSet resultSet = pst.executeQuery();

            if (resultSet.next()) {
                String passwordBD = resultSet.getString("password");
                if (passwordBD == null || passwordBD.isEmpty() || !BCrypt.checkpw(password, passwordBD)) {
                    return null;
                }

                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        getEnumOrNull(Role.class, resultSet.getString("role")),
                        email,
                        resultSet.getString("profilePhoto")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

                return new User(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        Role.valueOf(resultSet.getString(4)),
                        email,
                        resultSet.getString(5)
                );

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

    public boolean verifiePhoneNumber(String phoneNumber) {
        String query = "SELECT id FROM user WHERE phoneNumber = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, phoneNumber);

            ResultSet resultSet = pst.executeQuery();

            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Si aucun utilisateur trouvé
    }

    public boolean updatePasswordByPhoneNumber(String phoneNumber, String password) {
        String query = "UPDATE user SET password = ? WHERE phoneNumber = ?";

        password = BCrypt.hashpw(password, BCrypt.gensalt(10));

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, password);
            pst.setString(2, phoneNumber);

            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;

    }

    public boolean updatePassword(int id, String password) {
        String query = "UPDATE user SET password = ? WHERE id = ?";

        password = BCrypt.hashpw(password, BCrypt.gensalt(10));

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, password);
            pst.setInt(2, id);

            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;

    }

    public Boolean checkPassword(int id, String password) {
        String query = "SELECT password FROM user WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);

            ResultSet resultSet = pst.executeQuery();

            if (resultSet.next()) {
                String passwordBD = resultSet.getString("password");
                if (passwordBD != null && !passwordBD.isEmpty() && BCrypt.checkpw(password, passwordBD)) {
                    return true;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
