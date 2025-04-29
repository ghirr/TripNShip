package org.Esprit.TripNShip.Utils;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CaptchaVerifier {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String SECRET_KEY = dotenv.get("CAPTCHA_SECRET_KEY");

    public static boolean verifyCaptcha(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            String url = "https://www.google.com/recaptcha/api/siteverify";
            String params = "secret=" + URLEncoder.encode(SECRET_KEY, StandardCharsets.UTF_8) +
                    "&response=" + URLEncoder.encode(token, StandardCharsets.UTF_8);

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(params.getBytes(StandardCharsets.UTF_8));
            }

            try (InputStream responseStream = conn.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream))) {
                String response = reader.lines().reduce("", (acc, line) -> acc + line);
                return response.contains("\"success\": true");
            }

        } catch (IOException e) {
            e.printStackTrace(); // Optional: print error for debugging
            return false;
        }
    }

}
