package org.Esprit.TripNShip.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.Esprit.TripNShip.Entities.Role;
import org.Esprit.TripNShip.Utils.Shared;
import org.Esprit.TripNShip.Utils.UserSession;
import org.Esprit.TripNShip.Utils.WeatherAPI;
import org.Esprit.TripNShip.Utils.WeatherResult;

import java.io.IOException;

public class HomeController {

    public ImageView userIcon;
    public Label username;
    public Label welcomeText;
    public ImageView logoutIcon;
    public Circle userAvatarBorder;

    @FXML private BorderPane mainBorderPane;
    @FXML private VBox defaultContent;

    @FXML private ImageView logoImage;
    @FXML private ImageView card1;
    @FXML private ImageView card2;
    @FXML private ImageView card3;
    @FXML private ImageView card4;

    @FXML private Label homeLabel;
    @FXML private Label accommodationLabel;
    @FXML private Label transportLabel;
    @FXML private Label expeditionLabel;
    @FXML private Label circuitLabel;
    @FXML private Label vehicleLabel;

    @FXML private TextField cityTextField;
    @FXML private Button fetchButton;
    @FXML private Label weatherLabel;
    @FXML private ImageView weatherIcon;

    @FXML
    public void initialize() {
        initializeUser();
        initializeWeather();
        loadImages();
        setupNavigation();
    }

    private void initializeUser() {
        UserSession currentUser = UserSession.getInstance();
        if(currentUser == null) {
            logout();
        } else {
            username.setText(currentUser.getUserFirstName() + " " + currentUser.getUserLastName());
            welcomeText.setText("Welcome back,");

            userIcon.setImage(new Image(currentUser.getUserProfilePhotoPath()));
            userIcon.setOnMouseClicked(event -> navigateToProfile());
            username.setOnMouseClicked(event -> navigateToProfile());

            currentUser.getprofilePhotoUrlProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.isEmpty()) {
                    userIcon.setImage(new Image(newVal));
                }
            });
        }
    }

    private void initializeWeather() {
        // Récupérer la ville actuelle par IP au démarrage
        new Thread(() -> {
            String city = WeatherAPI.getCurrentCityFromIP();
            if (city != null && !city.isEmpty()) {
                javafx.application.Platform.runLater(() -> {
                    cityTextField.setText(city);
                    fetchWeatherByCity();
                });
            }
        }).start();

        fetchButton.setOnAction(event -> fetchWeatherByCity());
    }

    private void loadImages() {
        loadImage(logoImage, "/images/logo.png");
        loadImage(card1, "/images/plane.png");
        loadImage(card2, "/images/circuit.jpg");
        loadImage(card3, "/images/hotel.jpg");
        loadImage(card4, "/images/expedition.jpg");
    }

    private void setupNavigation() {
        // CORRECTION: Pour Home, on restore juste le contenu par défaut
        // au lieu de recharger toute la page
        homeLabel.setOnMouseClicked(event -> {
            resetToHomeContent();
            updateMenuActiveState(homeLabel);
        });

        setupDropdownMenu(accommodationLabel,
                createMenuItem("Book Room", "/fxml/AccommodationManagementFXML/RoomListView.fxml"),
                createMenuItem("My Booking", "/fxml/AccommodationManagementFXML/UserBookings.fxml")
        );

        setupDropdownMenu(transportLabel,
                createMenuItem("Bus", "#"),
                createMenuItem("Train", "#")
        );

        expeditionLabel.setOnMouseClicked(event -> {
            navigateTo("/fxml/ExpeditionManagement/ClientExpeditions.fxml");
            updateMenuActiveState(expeditionLabel);
        });

        setupDropdownMenu(circuitLabel,
                createMenuItem("Circuits List", "/fxml/CircuitManagementFXML/CircuitView.fxml"),
                createMenuItem("Circuits Booking", "/fxml/CircuitManagementFXML/ListCitcuitBooking.fxml")
        );

        setupDropdownMenu(vehicleLabel,
                createMenuItem("Vehicles List", "/fxml/CircuitManagementFXML/VehicleView.fxml"),
                createMenuItem("Vehicles Booking", "/fxml/CircuitManagementFXML/ListVehicleBooking.fxml")
        );
    }

    /**
     * CORRECTION: Cette méthode restore le contenu home par défaut
     * sans recharger la page complète
     */
    private void resetToHomeContent() {
        // Réafficher le contenu par défaut
        if (defaultContent != null) {
            mainBorderPane.setCenter(new ScrollPane(defaultContent));
        }
    }

    /**
     * Met à jour l'état actif du menu
     */
    private void updateMenuActiveState(Label activeLabel) {
        // Retirer la classe active de tous les labels
        homeLabel.getStyleClass().remove("active");
        accommodationLabel.getStyleClass().remove("active");
        transportLabel.getStyleClass().remove("active");
        expeditionLabel.getStyleClass().remove("active");
        circuitLabel.getStyleClass().remove("active");
        vehicleLabel.getStyleClass().remove("active");

        // Ajouter la classe active au label sélectionné
        if (!activeLabel.getStyleClass().contains("active")) {
            activeLabel.getStyleClass().add("active");
        }
    }

    private void setupDropdownMenu(Label label, MenuItem... items) {
        ContextMenu menu = new ContextMenu(items);
        label.setOnMouseClicked(event -> {
            if (menu.isShowing()) {
                menu.hide();
            } else {
                menu.show(label, event.getScreenX(), event.getScreenY());
                updateMenuActiveState(label);
            }
        });
    }

    private MenuItem createMenuItem(String title, String fxmlPath) {
        MenuItem item = new MenuItem(title);
        item.setOnAction(e -> navigateTo(fxmlPath));
        return item;
    }

    private void navigateTo(String fxmlPath) {
        if ("#".equals(fxmlPath)) {
            showAlert("Coming Soon", "This feature will be available soon!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            mainBorderPane.setCenter(content);
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlPath);
            e.printStackTrace();
            showAlert("Error", "Failed to load the requested page.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadImage(ImageView imageView, String path) {
        try {
            var resource = getClass().getResource(path);
            if (resource != null) {
                imageView.setImage(new Image(resource.toExternalForm()));
            } else {
                System.err.println("Image not found: " + path);
            }
        } catch (Exception e) {
            System.err.println("Failed to load image: " + path);
            e.printStackTrace();
        }
    }

    @FXML
    private void fetchWeatherByCity() {
        String city = cityTextField.getText().trim();
        if (city.isEmpty()) {
            showAlert("Input Required", "Please enter a city name.");
            return;
        }

        // Disable button during loading
        fetchButton.setDisable(true);
        fetchButton.setText("Loading...");

        new Thread(() -> {
            WeatherResult result = WeatherAPI.getWeatherResult(city);
            Platform.runLater(() -> {
                fetchButton.setDisable(false);
                fetchButton.setText("Get Weather");

                if (result != null) {
                    weatherLabel.setText(result.getDisplayText());
                    String iconUrl = "https://openweathermap.org/img/wn/" + result.getIconCode() + "@2x.png";
                    weatherIcon.setImage(new Image(iconUrl));
                } else {
                    weatherLabel.setText("Weather unavailable for " + city);
                    weatherIcon.setImage(null);
                    showAlert("Weather Error", "Unable to fetch weather data for " + city);
                }
            });
        }).start();
    }

    public void logout() {
        UserSession.clearSession();
        Shared.switchScene(logoutIcon, getClass().getResource("/fxml/login.fxml"), "Login");
    }

    public void navigateToProfile() {
        navigateTo("/fxml/employeeProfile.fxml");
        // Note: Pas de updateMenuActiveState ici car le profil n'est pas dans le menu principal
    }
}