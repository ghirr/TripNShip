package org.Esprit.TripNShip.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public class GeoUtils {
    public static String getCoordinatesFromCity(String cityName) throws IOException {
        String apiKey = "5b3ce3597851110001cf624873f8c6ba0ae84b41b8a1f73413e37053";
        String url = "https://api.openrouteservice.org/geocode/search?api_key=" + apiKey + "&text=" + URLEncoder.encode(cityName, StandardCharsets.UTF_8);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder json = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) json.append(line);
        reader.close();

        JSONObject obj = new JSONObject(json.toString());
        JSONArray coords = obj.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates");
        double lon = coords.getDouble(0);
        double lat = coords.getDouble(1);

        return lat + "," + lon;
    }

    }
