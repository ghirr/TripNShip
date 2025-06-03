package org.Esprit.TripNShip.Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmailBookSender {

//
//    public static void sendEmail(String to, String subject, String content) {
//        try {
//            URL url = new URL(API_URL);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("accept", "application/json");
//            conn.setRequestProperty("api-key", API_KEY);
//            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setDoOutput(true);
//
//            String jsonBody = String.format(
//                    "{"
//                            + "\"sender\": {\"name\": \"TripNShip\", \"email\": \"ton-email-verifie@gmail.com\"},"
//                            + "\"to\": [{\"email\": \"%s\"}],"
//                            + "\"subject\": \"%s\","
//                            + "\"htmlContent\": \"%s\""
//                            + "}",
//                    to, subject, content.replace("\"", "\\\"")
//            );
//
//            try (OutputStream os = conn.getOutputStream()) {
//                os.write(jsonBody.getBytes("utf-8"));
//                os.flush();
//            }
//
//            int responseCode = conn.getResponseCode();
//            System.out.println("HTTP Response Code: " + responseCode);
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(
//                    (responseCode >= 200 && responseCode < 300) ? conn.getInputStream() : conn.getErrorStream(), "utf-8"));
//            StringBuilder response = new StringBuilder();
//            String line;
//
//            while ((line = reader.readLine()) != null) {
//                response.append(line.trim());
//            }
//
//            System.out.println("Brevo API Response: " + response.toString());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
