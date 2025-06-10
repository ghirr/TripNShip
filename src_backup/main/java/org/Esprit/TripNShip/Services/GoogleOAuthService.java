package org.Esprit.TripNShip.Services;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class GoogleOAuthService {

    private static final String CREDENTIALS_FILE_PATH = "/credentials.json"; // Place it under resources
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = List.of(
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email"
//            ,"https://www.googleapis.com/auth/user.birthday.read"
    );
    private static final String REDIRECT_URI = "http://localhost:8888/callback";

    private static GoogleAuthorizationCodeFlow flow;
    private static GoogleClientSecrets clientSecrets;

    public static String getAuthorizationUrl() throws Exception {
        clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(GoogleOAuthService.class.getResourceAsStream(CREDENTIALS_FILE_PATH)));

        flow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .build();

        GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
        return url.setRedirectUri(REDIRECT_URI).build();
    }

    public static String extractCodeFromRedirectUrl(String redirectedUrl) {
        try {
            String[] parts = redirectedUrl.split("code=");
            if (parts.length > 1) {
                return URLDecoder.decode(parts[1].split("&")[0], "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String exchangeCodeForAccessToken(String code) {
        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    JSON_FACTORY,
                    "https://oauth2.googleapis.com/token",
                    clientSecrets.getDetails().getClientId(),
                    clientSecrets.getDetails().getClientSecret(),
                    code,
                    REDIRECT_URI
            ).execute();

            return tokenResponse.getAccessToken();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject fetchUserInfo(String accessToken) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://www.googleapis.com/oauth2/v2/userinfo"))
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new JSONObject(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
