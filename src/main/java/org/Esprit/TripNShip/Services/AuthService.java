package org.Esprit.TripNShip.Services;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import org.Esprit.TripNShip.Entities.Client;
import org.Esprit.TripNShip.Entities.Role;
import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Utils.MyDataBase;
import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.reflect.TypeToken;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.lang.reflect.Type;
import java.util.Base64;

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

    public void addFaceRecognitionForUser(int userId, List<String> capturedFaces) {
        String req = "UPDATE user set face_sample=? where id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            String jsonFaces = new Gson().toJson(capturedFaces); // Convert List to JSON
            ps.setString(1, jsonFaces);
            ps.setInt(2, userId);
            ps.executeUpdate();
            System.out.println("Transporter updated!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public User authenticateWithFace(String capturedFaceData) {
        String query = "SELECT id, firstName, lastName, email, face_sample FROM user WHERE face_sample IS NOT NULL AND face_sample != ''";

        try (PreparedStatement ps = cnx.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int userId = rs.getInt("id");
                String nom = rs.getString("firstName");
                String prenom = rs.getString("lastName");
                String email = rs.getString("email");
                String storedFacesJson = rs.getString("face_sample");

                // Parse stored faces from JSON
                List<String> storedFaces = parseFaceSamplesFromJson(storedFacesJson);

                // Compare captured face with stored faces
                if (compareFaces(capturedFaceData, storedFaces)) {
                    // Create and return user object
                    User user = new User();
                    user.setIdUser(userId);
                    user.setFirstName(nom);
                    user.setLastName(prenom);
                    user.setEmail(email);
                    System.out.println(user);
                    return user;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error during face authentication: " + e.getMessage());
            e.printStackTrace();
        }

        return null; // No matching face found
    }


    private List<String> parseFaceSamplesFromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>(){}.getType();
            return gson.fromJson(json, listType);
        } catch (Exception e) {
            System.err.println("Error parsing face samples JSON: " + e.getMessage());
            return null;
        }
    }

    private boolean compareFaces(String capturedFace, List<String> storedFaces) {
        if (storedFaces == null || storedFaces.isEmpty()) {
            return false;
        }

        try {
            // Convert captured face from base64 to Mat
            Mat capturedMat = base64ToMat(capturedFace);
            if (capturedMat.empty()) {
                return false;
            }

            // Resize captured face for comparison
            Mat capturedResized = new Mat();
            Imgproc.resize(capturedMat, capturedResized, new Size(100, 100));

            // Compare with each stored face
            for (String storedFace : storedFaces) {
                Mat storedMat = base64ToMat(storedFace);
                if (!storedMat.empty()) {
                    // Resize stored face
                    Mat storedResized = new Mat();
                    Imgproc.resize(storedMat, storedResized, new Size(100, 100));

                    // Calculate similarity using template matching
                    double similarity = calculateFaceSimilarity(capturedResized, storedResized);

                    // If similarity is above threshold, consider it a match
                    if (similarity > 0.7) { // Adjust threshold as needed
                        return true;
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error comparing faces: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private Mat base64ToMat(String base64) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64);
            Mat mat = Imgcodecs.imdecode(new MatOfByte(decodedBytes), Imgcodecs.IMREAD_COLOR);
            return mat;
        } catch (Exception e) {
            System.err.println("Error converting base64 to Mat: " + e.getMessage());
            return new Mat();
        }
    }

    private double calculateFaceSimilarity(Mat face1, Mat face2) {
        try {
            // Convert to grayscale for comparison
            Mat gray1 = new Mat();
            Mat gray2 = new Mat();
            Imgproc.cvtColor(face1, gray1, Imgproc.COLOR_BGR2GRAY);
            Imgproc.cvtColor(face2, gray2, Imgproc.COLOR_BGR2GRAY);

            // Calculate histogram correlation
            Mat hist1 = new Mat();
            Mat hist2 = new Mat();

            MatOfFloat ranges = new MatOfFloat(0f, 256f);
            MatOfInt histSize = new MatOfInt(256);
            MatOfInt channels = new MatOfInt(0);

            Imgproc.calcHist(List.of(gray1), channels, new Mat(), hist1, histSize, ranges);
            Imgproc.calcHist(List.of(gray2), channels, new Mat(), hist2, histSize, ranges);

            // Normalize histograms
            Core.normalize(hist1, hist1, 0, 1, Core.NORM_MINMAX);
            Core.normalize(hist2, hist2, 0, 1, Core.NORM_MINMAX);

            // Calculate correlation
            double correlation = Imgproc.compareHist(hist1, hist2, Imgproc.HISTCMP_CORREL);

            return correlation;

        } catch (Exception e) {
            System.err.println("Error calculating face similarity: " + e.getMessage());
            return 0.0;
        }
    }
}
