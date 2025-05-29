package org.Esprit.TripNShip.Utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WeatherAPI {

    private static final String API_KEY = "f016147d532c5039e6f4e5f4678ec8ed";

    // Récupère la météo d'une ville
    public static WeatherResult getWeatherResult(String city) {
        try {
            // Encodage de la ville pour gérer les espaces/accents
            String encodedCity = URLEncoder.encode(city, "UTF-8");

            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" +
                    encodedCity + "&appid=" + API_KEY + "&units=metric&lang=fr";

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            // Extraction des données
            String condition = jsonObject.getAsJsonArray("weather")
                    .get(0).getAsJsonObject()
                    .get("description").getAsString();

            String iconCode = jsonObject.getAsJsonArray("weather")
                    .get(0).getAsJsonObject()
                    .get("icon").getAsString();

            double temp = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();

            return new WeatherResult(condition, temp, iconCode);

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des données météo pour la ville : " + city);
            e.printStackTrace();
            return null;
        }
    }

    // Récupère la ville actuelle via l'adresse IP
    public static String getCurrentCityFromIP() {
        try {
            URL url = new URL("http://ip-api.com/json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            JSONObject json = new JSONObject(content.toString());
            return json.getString("city");
        } catch (Exception e) {
            System.err.println("Erreur géolocalisation IP : " + e.getMessage());
            return null;
        }
    }

}
