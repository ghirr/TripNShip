package org.Esprit.TripNShip.Controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApp extends Application {

    // Clé API valide
    private static final String API_KEY = "3e336c5b04dc4676b1b130412251205";
    private static final String BASE_URL = "http://api.weatherapi.com/v1/current.json?key=%s&q=%s&lang=fr";

    @Override
    public void start(Stage stage) {
        TextField cityInput = new TextField();
        cityInput.setPromptText("Entrez une ville");

        Button fetchButton = new Button("Voir la météo");

        Label resultLabel = new Label();

        fetchButton.setOnAction(e -> {
            String city = cityInput.getText().trim();
            if (!city.isEmpty()) {
                resultLabel.setText("Chargement...");

                // Thread séparé pour ne pas bloquer l’interface
                new Thread(() -> {
                    String result = fetchWeather(city);
                    Platform.runLater(() -> resultLabel.setText(result));
                }).start();
            } else {
                resultLabel.setText("Veuillez entrer une ville.");
            }
        });

        VBox root = new VBox(10, cityInput, fetchButton, resultLabel);
        root.setStyle("-fx-padding: 20");
        stage.setScene(new Scene(root, 400, 200));
        stage.setTitle("Météo JavaFX");
        stage.show();
    }

    // Appel à l’API météo
    private String fetchWeather(String city) {
        try {
            String urlString = String.format(BASE_URL, API_KEY, city);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();

            JsonObject current = json.getAsJsonObject("current");
            String condition = current.getAsJsonObject("condition").get("text").getAsString();
            double temp = current.get("temp_c").getAsDouble();

            return String.format("À %s : %s, %.1f °C", city, condition, temp);
        } catch (Exception e) {
            return "Erreur : " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
