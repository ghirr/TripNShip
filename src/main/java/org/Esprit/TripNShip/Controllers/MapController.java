package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import org.Esprit.TripNShip.Utils.GeoUtils;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.io.IOException;

public class MapController {
    @FXML
    private WebView mapWebView;

    public void initialize() {
        // Rien ici au départ
    }

    public void displayRoute(String city1, String city2) {
        try {
            String coord1 = GeoUtils.getCoordinatesFromCity(city1);
            String coord2 = GeoUtils.getCoordinatesFromCity(city2);

            String html = generateHTML(coord1, coord2);
            WebEngine engine = mapWebView.getEngine();
            engine.loadContent(html);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateHTML(String coord1, String coord2) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="utf-8" />
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <link rel="stylesheet" href="https://unpkg.com/leaflet/dist/leaflet.css"/>
          <script src="https://unpkg.com/leaflet/dist/leaflet.js"></script>
        </head>
        <body>
          <div id="map" style="width: 100%%; height: 100vh;"></div>
          <script>
            var map = L.map('map').setView([%s], 6);
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
              maxZoom: 19,
              attribution: '© OpenStreetMap'
            }).addTo(map);

            var point1 = [%s];
            var point2 = [%s];

            L.marker(point1).addTo(map).bindPopup("Départ").openPopup();
            L.marker(point2).addTo(map).bindPopup("Arrivée");

            var polyline = L.polyline([point1, point2], {color: 'blue'}).addTo(map);
            map.fitBounds(polyline.getBounds());
          </script>
        </body>
        </html>
        """.formatted(coord1, coord1, coord2);
    }

}
