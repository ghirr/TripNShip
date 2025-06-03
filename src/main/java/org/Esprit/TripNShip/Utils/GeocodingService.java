package org.Esprit.TripNShip.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GeocodingService {
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";

    public static class Coordinates {
        public final double latitude;
        public final double longitude;
        public final boolean isValid;

        public Coordinates(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.isValid = true;
        }

        public Coordinates() {
            this.latitude = 0;
            this.longitude = 0;
            this.isValid = false;
        }
    }

    public Coordinates geocodeAddress(String address) {
        try {
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String urlString = NOMINATIM_URL + "?q=" + encodedAddress + "&format=json&limit=1";

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "TripNShipApp/1.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) {
                return new Coordinates();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonArray results = JsonParser.parseString(response.toString()).getAsJsonArray();
            if (results.size() > 0) {
                JsonObject firstResult = results.get(0).getAsJsonObject();
                double lat = firstResult.get("lat").getAsDouble();
                double lon = firstResult.get("lon").getAsDouble();
                return new Coordinates(lat, lon);
            }

        } catch (Exception e) {
            System.err.println("Geocoding error: " + e.getMessage());
            e.printStackTrace();
        }

        return new Coordinates();
    }
}
