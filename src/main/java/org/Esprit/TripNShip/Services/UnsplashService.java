package org.Esprit.TripNShip.Services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UnsplashService {

    private static final String ACCESS_KEY = "4p_kDX3TaxTCt6_8jB9tO5IkPn_gxNOhZBEIGWY0uI4";

    public List<String> searchImageUrls(String query, int count) {
        List<String> imageUrls = new ArrayList<>();

        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String urlString = "https://api.unsplash.com/search/photos?query=" + encodedQuery + "&per_page=" + count + "&client_id=" + ACCESS_KEY;


            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();

                String inputLine;
                while((inputLine = in.readLine()) != null){
                    response.append(inputLine);
                }
                in.close();

                Gson gson = new Gson();
                JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
                JsonArray results = jsonResponse.getAsJsonArray("results");

                for(int i = 0; i < results.size(); i++) {
                    JsonObject photo = results.get(i).getAsJsonObject();
                    JsonObject urls = photo.getAsJsonObject("urls");
                    String imageUrl = urls.get("small").getAsString(); // ou "regular", "thumb", etc.
                    imageUrls.add(imageUrl);
                }
            } else {
                System.out.println("Erreur : code HTTP " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageUrls;
    }
}
