package org.Esprit.TripNShip.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Client;
import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Services.AuthService;
import org.Esprit.TripNShip.Services.GoogleOAuthService;
import org.Esprit.TripNShip.Services.UserService;
import org.json.JSONObject;

public class LoginWithGoogleController {

    @FXML
    private WebView webView;

    @FXML
    public void initialize() {
        try {
            String authUrl = GoogleOAuthService.getAuthorizationUrl();

            WebEngine engine = webView.getEngine();
            engine.load(authUrl);

            engine.locationProperty().addListener((observable, oldLocation, newLocation) -> {
                if (newLocation.startsWith("http://localhost:8888/callback")) {
                    String code = GoogleOAuthService.extractCodeFromRedirectUrl(newLocation);

                    // Exchange the code for an access token
                    new Thread(() -> {
                        String accessToken = GoogleOAuthService.exchangeCodeForAccessToken(code);

                        // You can now use the token (e.g., fetch profile, etc.)
                        JSONObject userInfo = GoogleOAuthService.fetchUserInfo(accessToken);
                        System.out.println(userInfo);
                        AuthService authService = new AuthService();
                        if (userInfo != null) {
                            String email = userInfo.optString("email");
                            User user = authService.googleLogin(email);
                            if(user != null) {
                                System.out.println(user.toString());
                            }
                            else {
                                String firstname = userInfo.optString("given_name");
                                String lastname = userInfo.optString("family_name");
                                String picture = userInfo.optString("picture");

                                user = new Client(firstname,lastname , email, picture);
                                System.out.println(user.toString());
                                authService.signUpGoogle((Client) user);

                            }

//                            Client client = new Client(firstname, lastname, email, picture);


                        }
                        // Close the Googlelogin window
                        Platform.runLater(() -> {
                            Stage stage = (Stage) webView.getScene().getWindow();
                            stage.close();
                        });
                    }).start();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
