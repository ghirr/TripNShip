package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.Esprit.TripNShip.Entities.Role;
import org.Esprit.TripNShip.Utils.Shared;
import org.Esprit.TripNShip.Utils.UserSession;
import org.Esprit.TripNShip.Utils.WeatherAPI;
import org.Esprit.TripNShip.Utils.WeatherResult;

import java.io.IOException;

public class HomeController {

    public ImageView userIcon;
    public Label username;
    public ImageView logoutIcon;
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

    @FXML
    private TextField cityTextField;

    @FXML
    private Button fetchButton;

    @FXML
    private Label weatherLabel;

    @FXML
    private ImageView weatherIcon;

    @FXML
    public void initialize() {

        UserSession currentUser = UserSession.getInstance();
        if(currentUser==null){
            logout();
        }
        else{
            username.setText(currentUser.getUserFirstName()+" "+currentUser.getUserLastName());
            userIcon.setImage(new Image(currentUser.getUserProfilePhotoPath()));
            userIcon.setOnMouseClicked(event -> {navigateToProfile();});
            username.setOnMouseClicked(event -> navigateToProfile());
            currentUser.getprofilePhotoUrlProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.isEmpty()) {
                    userIcon.setImage(new Image(newVal));
                }
            });
        }
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

        loadImage(logoImage, "/images/logo.png");
        loadImage(card1, "/images/plane.png");
        loadImage(card2, "/images/circuit.jpg");
        loadImage(card3, "/images/hotel.jpg");
        loadImage(card4, "/images/expedition.jpg");


        homeLabel.setOnMouseClicked(event -> navigateTo("/fxml/Home.fxml"));


        setupDropdownMenu(accommodationLabel,
                createMenuItem("Book Room", "/fxml/AccommodationManagementFXML/RoomListView.fxml"),
                createMenuItem("My Booking", "/fxml/AccommodationManagementFXML/UserBookings.fxml")
        );

        transportLabel.setOnMouseClicked(event -> {
            navigateTo("/fxml/itineraryContainer.fxml"); // Chemin à adapter selon ton projet
        });

        expeditionLabel.setOnMouseClicked(event -> {
            navigateTo("/fxml/ExpeditionManagement/ClientExpeditions.fxml");
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

    private void setupDropdownMenu(Label label, MenuItem... items) {
        ContextMenu menu = new ContextMenu(items);
        label.setOnMouseClicked(event -> {
            if (menu.isShowing()) {
                menu.hide();
            } else {
                menu.show(label, event.getScreenX(), event.getScreenY());
            }
        });
    }

    private MenuItem createMenuItem(String title, String fxmlPath) {
        MenuItem item = new MenuItem(title);
        item.setOnAction(e -> navigateTo(fxmlPath));
        return item;
    }

    private void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            mainBorderPane.setCenter(content);
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlPath);
            e.printStackTrace();
        }
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

    public void logout() {
        UserSession.clearSession();
        Shared.switchScene(logoutIcon,getClass().getResource("/fxml/login.fxml"),"Login");
    }

    public void navigateToProfile() {
        navigateTo("/fxml/employeeProfile.fxml");
    }
}
