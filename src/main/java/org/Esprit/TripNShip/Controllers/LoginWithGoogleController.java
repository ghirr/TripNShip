package org.Esprit.TripNShip.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Client;
import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Services.AuthService;
import org.Esprit.TripNShip.Services.GoogleOAuthService;
import org.Esprit.TripNShip.Utils.Shared;
import org.Esprit.TripNShip.Utils.UserSession;
import org.json.JSONObject;

import static org.Esprit.TripNShip.Utils.Shared.showAlert;

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
                        if (userInfo != null) {
                            AuthService authService = new AuthService();
                            String email = userInfo.optString("email");
                            User user = authService.googleLogin(email);
                            if(user != null) {
                                UserSession.initSession(user);
                                Shared.switchScene(webView,getClass().getResource("/fxml/Home.fxml"),"Main");
                            }
                            else {
                                String firstname = userInfo.optString("given_name");
                                String lastname = userInfo.optString("family_name");
                                String picture = userInfo.optString("picture");
                                System.out.println(picture);
                                user = new Client(1,firstname,lastname , email, picture);
                                authService.signUpGoogle((Client) user);
                                System.out.println(user.getEmail());
                            }
                        }
                        // Close the Googlelogin window
                        Platform.runLater(() -> {
                            Stage stage1 = (Stage) LoginController.getInstance().getToggleButton().getScene().getWindow();
                            stage1.close();
                        });
                    }).start();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
