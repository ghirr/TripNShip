package org.Esprit.TripNShip.Controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController {

    @FXML
    private Label totalLeadsLabel;

    @FXML
    private Label totalPaymentLabel;

    @FXML
    private Label totalSalesLabel;

    @FXML
    private Label newOrdersLabel;

    @FXML
    private PieChart bookingPieChart;

    @FXML
    private Label weatherLabel;

    @FXML
    private Label temperatureLabel;

    @FXML
    private Label conditionLabel;

    private static final String API_KEY = "aea3f5f67e884cd4888140027251205"; // Remplace par ta clé API
    private static final String BASE_URL = "http://api.weatherapi.com/v1/current.json?key=aea3f5f67e884cd4888140027251205&q=Paris&lang=fr";

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        // Simuler des données de dashboard pour l'exemple
        int totalLeads = 124;
        int totalPayment = 87;
        int totalSales = 37;
        int newOrders = 20;

        totalLeadsLabel.setText(String.valueOf(totalLeads));
        totalPaymentLabel.setText(String.valueOf(totalPayment));
        totalSalesLabel.setText(String.valueOf(totalSales));
        newOrdersLabel.setText(String.valueOf(newOrders));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Confirmées", 80),
                new PieChart.Data("En attente", 44)
        );
        bookingPieChart.setData(pieChartData);

        // Mettre à jour les informations météo
        String city = "Paris"; // Remplace par la ville de ton choix
        updateWeatherInfo(city);
    }

    private void updateWeatherInfo(String city) {
        try {
            String urlString = String.format(BASE_URL, API_KEY, city); // Correction ici
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();

            JsonObject current = json.getAsJsonObject("current");
            String condition = current.getAsJsonObject("condition").get("text").getAsString();
            double temp = current.get("temp_c").getAsDouble();

            // Mettre à jour les labels avec les données de l'API
            weatherLabel.setText("Météo à " + city);
            temperatureLabel.setText(String.format("Température: %.1f °C", temp));
            conditionLabel.setText("Condition: " + condition);

        } catch (Exception e) {
            weatherLabel.setText("Erreur de récupération des données météo");
            temperatureLabel.setText("Température: N/A");
            conditionLabel.setText("Condition: N/A");
        }
    }
}
