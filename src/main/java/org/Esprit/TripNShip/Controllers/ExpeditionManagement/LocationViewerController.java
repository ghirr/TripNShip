package org.Esprit.TripNShip.Controllers.ExpeditionManagement;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.net.URL;
import java.util.ResourceBundle;

public class LocationViewerController implements Initializable {

    @FXML
    private WebView mapWebView;

    @FXML
    private Label locationNameLabel;

    @FXML
    private Label coordinatesLabel;

    @FXML
    private Button closeButton;

    private WebEngine webEngine;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        webEngine = mapWebView.getEngine();

        // Load the HTML content directly
        webEngine.loadContent(getMapHtmlContent());

        // Set up button handlers
        closeButton.setOnAction(event -> close());
    }

    public void setLocation(String locationName, double latitude, double longitude) {
        // Update labels
        locationNameLabel.setText(locationName);
        coordinatesLabel.setText(String.format("Lat: %.6f, Lng: %.6f", latitude, longitude));

        // Set up map once loaded
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                // Set location on map
                webEngine.executeScript(String.format(
                        "showLocation(%f, %f, '%s')",
                        latitude, longitude, locationName.replace("'", "\\'")));
            }
        });
    }

    private String getMapHtmlContent() {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Location Viewer</title>
                
                <!-- Leaflet CSS -->
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" 
                      integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" 
                      crossorigin=""/>
                
                <!-- Leaflet JS -->
                <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" 
                        integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=" 
                        crossorigin=""></script>
                        
                <style>
                    html, body {
                        height: 100%;
                        margin: 0;
                        padding: 0;
                    }
                    
                    #map {
                        height: 100%;
                        width: 100%;
                    }
                    
                    .marker-popup {
                        font-size: 14px;
                        line-height: 1.5;
                    }
                </style>
            </head>
            <body>
                <div id="map"></div>
                
                <script>
                    // Initialize map
                    const map = L.map('map').setView([36.8065, 10.1815], 6); // Default view centered on Tunisia
                    let marker = null;
                    let circle = null;
                    
                    // Add tile layer (OpenStreetMap)
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
                        maxZoom: 19
                    }).addTo(map);
                    
                    function showLocation(lat, lng, name) {
                        // Remove previous marker if exists
                        if (marker) {
                            map.removeLayer(marker);
                        }
                        if (circle) {
                            map.removeLayer(circle);
                        }
                        
                        // Add marker
                        marker = L.marker([lat, lng]).addTo(map)
                            .bindPopup(`<div class="marker-popup"><strong>${name}</strong><br>Latitude: ${lat.toFixed(6)}<br>Longitude: ${lng.toFixed(6)}</div>`)
                            .openPopup();
                        
                        // Add accuracy circle
                        circle = L.circle([lat, lng], {
                            color: '#3388ff',
                            fillColor: '#3388ff',
                            fillOpacity: 0.1,
                            radius: 500
                        }).addTo(map);
                        
                        // Center map on marker with zoom
                        map.setView([lat, lng], 14);
                    }
                </script>
            </body>
            </html>
        """;
    }

    private void close() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}