package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.Esprit.TripNShip.Utils.WeatherAPI;
import org.Esprit.TripNShip.Utils.WeatherResult;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private TextField cityTextField;

    @FXML
    private Button fetchButton;

    @FXML
    private Label weatherLabel;

    @FXML
    private ImageView weatherIcon;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Dès le lancement, récupérer la ville actuelle par IP
        new Thread(() -> {
            String city = WeatherAPI.getCurrentCityFromIP();
            if (city != null && !city.isEmpty()) {
                javafx.application.Platform.runLater(() -> {
                    cityTextField.setText(city);
                    fetchWeatherByCity(); // Appelle la méthode sans paramètre ici
                });
            }
        }).start();

        // Bouton recherche
        fetchButton.setOnAction(event -> fetchWeatherByCity());
    }

    @FXML
    private void fetchWeatherByCity() {
        String city = cityTextField.getText().trim();
        if (city.isEmpty()) return;

        new Thread(() -> {
            WeatherResult result = WeatherAPI.getWeatherResult(city);
            if (result != null) {
                javafx.application.Platform.runLater(() -> {
                    weatherLabel.setText(result.getDisplayText());
                    String iconUrl = "https://openweathermap.org/img/wn/" + result.getIconCode() + "@2x.png";
                    weatherIcon.setImage(new Image(iconUrl));
                });
            } else {
                javafx.application.Platform.runLater(() -> {
                    weatherLabel.setText("Météo indisponible pour " + city);
                    weatherIcon.setImage(null);
                });
            }
        }).start();
    }
}
