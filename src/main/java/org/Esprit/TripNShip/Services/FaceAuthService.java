package org.Esprit.TripNShip.Services;

import com.google.gson.Gson;
import javafx.application.Platform;
import org.Esprit.TripNShip.Entities.Role;
import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Utils.MyDataBase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.Esprit.TripNShip.Utils.Shared.getEnumOrNull;

public class FaceAuthService {

    private final Connection cnx = MyDataBase.getInstance().getConnection();


    public void addFaceRecognitionForUser(int currentUserId, List<String> capturedFaces){
        try {
            // Prepare JSON payload
            JSONObject payload = new JSONObject();
            payload.put("user_id", currentUserId);
            payload.put("face_images", new JSONArray(capturedFaces));

            // Send HTTP request to Python service
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5000/register_faces"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User authenticateWithFace(String capturedFaceData) {
        try {
            JSONObject payload = new JSONObject().put("face_image", capturedFaceData);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5000/authenticate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject result = new JSONObject(response.body());
                if (result.getBoolean("success")) {
                    int userId = result.getInt("user_id");
                    String query = "SELECT firstName, lastName, email, role, profilePhoto FROM user WHERE id = ?";
                    try (PreparedStatement ps = cnx.prepareStatement(query)) {
                        ps.setInt(1, userId);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            return new User(
                                    userId,
                                    rs.getString("firstName"),
                                    rs.getString("lastName"),
                                    getEnumOrNull(Role.class, rs.getString("role")),
                                    rs.getString("email"),
                                    rs.getString("profilePhoto")
                            );
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
