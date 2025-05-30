package org.Esprit.TripNShip.Controllers.ExpeditionManagement;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import netscape.javascript.JSObject;

import java.net.URL;
import java.util.ResourceBundle;

public class MapLocationPickerController implements Initializable {

    @FXML
    private WebView mapWebView;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchButton;

    @FXML
    private Label selectedLocationLabel;

    private WebEngine webEngine;
    private String selectedLocation = "";
    private double selectedLatitude = 0;
    private double selectedLongitude = 0;
    private boolean locationSelected = false;

    // Interface to communicate with JavaScript
    public interface LocationCallback {
        void locationSelected(String location, double lat, double lng);
    }

    private LocationCallback callback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webEngine = mapWebView.getEngine();

        // Load the HTML content directly
        webEngine.loadContent(getMapHtmlContent());

        // Set up callbacks
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                // Once the page is loaded, create a bridge between Java and JavaScript
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaConnector", new JavaConnector());

                // Enable debugging in JavaScript console
                webEngine.executeScript("console.log = function(message) { window.javaConnector.log(message); };");
            }
        });

        // Set up button actions - initially disabled until location selected
        confirmButton.setDisable(true);

        confirmButton.setOnAction(event -> {
            if (callback != null) {
                callback.locationSelected(selectedLocation, selectedLatitude, selectedLongitude);
                close();
            }
        });

        cancelButton.setOnAction(event -> close());

        // Set up search functionality
        searchButton.setOnAction(event -> {
            String searchQuery = searchTextField.getText().trim();
            if (!searchQuery.isEmpty()) {
                webEngine.executeScript("searchLocation('" + searchQuery.replace("'", "\\'") + "')");
            }
        });

        // Allow pressing Enter in the search field to trigger search
        searchTextField.setOnAction(event -> {
            if (!searchTextField.getText().trim().isEmpty()) {
                searchButton.fire();
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
                <title>Location Picker</title>
                
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
                    // Initialize map centered on Tunisia
                    const map = L.map('map').setView([36.8065, 10.1815], 6);
                    let marker = null;
                    let circle = null;
                    
                    // Add tile layer (OpenStreetMap)
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
                        maxZoom: 19
                    }).addTo(map);
                    
                    // Handle map click event
                    map.on('click', function(e) {
                        const lat = e.latlng.lat;
                        const lng = e.latlng.lng;
                        console.log(`Map clicked at: ${lat}, ${lng}`);
                        
                        // Reverse geocode to get location name
                        reverseGeocode(lat, lng);
                    });
                    
                    // Function to set the marker at the selected location
                    function setLocationMarker(lat, lng, name) {
                        console.log(`Setting marker at: ${lat}, ${lng}, Name: ${name}`);
                        
                        // Remove previous marker if exists
                        if (marker) {
                            map.removeLayer(marker);
                        }
                        if (circle) {
                            map.removeLayer(circle);
                        }
                        
                        // Add new marker
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
                        
                        // Call Java method to pass selected location
                        try {
                            console.log("Calling Java connector with location data");
                            if (window.javaConnector) {
                                window.javaConnector.onLocationSelected(name, lat, lng);
                                console.log("Java connector called successfully");
                            } else {
                                console.log("Java connector not available");
                            }
                        } catch (e) {
                            console.log("Error calling Java connector: " + e.message);
                        }
                    }
                    
                    // Function to reverse geocode coordinates
                    function reverseGeocode(lat, lng) {
                        console.log(`Reverse geocoding: ${lat}, ${lng}`);
                        const url = `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=18&addressdetails=1`;
                        
                        fetch(url)
                            .then(response => response.json())
                            .then(data => {
                                let locationName = data.display_name;
                                console.log(`Location name: ${locationName}`);
                                setLocationMarker(lat, lng, locationName);
                            })
                            .catch(error => {
                                console.log(`Error during reverse geocoding: ${error}`);
                                // Fallback to coordinates as location name
                                const locationName = `Location at ${lat.toFixed(4)}, ${lng.toFixed(4)}`;
                                setLocationMarker(lat, lng, locationName);
                            });
                    }
                    
                    // Function to search for a location (called from Java)
                    function searchLocation(query) {
                        console.log(`Searching for location: ${query}`);
                        if (!query) return;
                        
                        const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}&limit=1`;
                        
                        fetch(url)
                            .then(response => response.json())
                            .then(data => {
                                if (data && data.length > 0) {
                                    const result = data[0];
                                    const lat = parseFloat(result.lat);
                                    const lng = parseFloat(result.lon);
                                    console.log(`Search result: ${result.display_name}, ${lat}, ${lng}`);
                                    setLocationMarker(lat, lng, result.display_name);
                                    map.setView([lat, lng], 13);
                                } else {
                                    console.log("No search results found");
                                    alert('Location not found. Please try a different search term.');
                                }
                            })
                            .catch(error => {
                                console.log(`Error searching for location: ${error}`);
                                alert('Error searching for location. Please try again.');
                            });
                    }
                    
                    // Function to set initial location (called from Java)
                    function setInitialLocation(lat, lng) {
                        console.log(`Setting initial location: ${lat}, ${lng}`);
                        if (lat && lng) {
                            map.setView([lat, lng], 13);
                            reverseGeocode(lat, lng);
                        }
                    }
                </script>
            </body>
            </html>
        """;
    }

    public void setCallback(LocationCallback callback) {
        this.callback = callback;
    }

    public void setInitialLocation(double latitude, double longitude) {
        // This can be used to set an initial location on the map
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                webEngine.executeScript(String.format(
                        "setInitialLocation(%f, %f)", latitude, longitude));
            }
        });
    }

    public class JavaConnector {
        // Added logging method for JavaScript console.log
        public void log(String message) {
            System.out.println("JS: " + message);
        }

        public void onLocationSelected(String name, double lat, double lng) {
            System.out.println("Location selected in JS: " + name + " at " + lat + ", " + lng);

            // Update these values on the JavaFX thread
            Platform.runLater(() -> {
                selectedLocation = name;
                selectedLatitude = lat;
                selectedLongitude = lng;
                locationSelected = true;

                // Update UI
                selectedLocationLabel.setText("Selected: " + name);
                confirmButton.setDisable(false);

                System.out.println("UI updated. Confirm button enabled: " + !confirmButton.isDisabled());
            });
        }
    }

    private void close() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
