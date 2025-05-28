package org.Esprit.TripNShip.Controllers;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.TypeAccommodation;
import org.Esprit.TripNShip.Services.AccommodationService;
import org.Esprit.TripNShip.Utils.GeocodingService;
import org.Esprit.TripNShip.Utils.MapController;
import org.Esprit.TripNShip.Utils.Shared;

import java.io.File;

public class AccommodationController {

    // Existing FXML fields
    @FXML public ImageView accommodationImageView;
    @FXML public Button buttonPhoto;
    public Button zoomInButton;
    public Button zoomOutButton;
    public VBox zoomControls;
    public Button toggleMarkerModeButton;
    public ScrollPane scrollPane;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private ChoiceBox<String> typeChoiceBox;
    @FXML private TextField capacityField;
    @FXML private Button addAccommodationButton;

    // New map-related FXML fields
    @FXML private MapView mapView;
    @FXML private Button geocodeButton;
    @FXML private Button clearMapButton;
    @FXML private Label coordinatesLabel;

    // Services and utilities
    private final AccommodationService accommodationService = new AccommodationService();
    private final GeocodingService geocodingService = new GeocodingService();
    private MapController mapController;

    // Data fields
    private Accommodation accommodation;
    private File selectedPhoto;
    private double selectedLatitude = 0.0;
    private double selectedLongitude = 0.0;
    private boolean hasValidCoordinates = false;
    private boolean mapInitialized = false;

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
        if (accommodation != null) {
            nameField.setText(accommodation.getName());
            addressField.setText(accommodation.getAddress());
            capacityField.setText(String.valueOf(accommodation.getCapacity()));
            typeChoiceBox.setValue(accommodation.getType().name());

            // Set coordinates if available
            if (accommodation.getLatitude() != 0 && accommodation.getLongitude() != 0) {
                selectedLatitude = accommodation.getLatitude();
                selectedLongitude = accommodation.getLongitude();
                hasValidCoordinates = true;
                updateCoordinatesLabel();

                // Show accommodation on map when map is ready
                Platform.runLater(() -> {
                    if (mapInitialized && mapController != null) {
                        showAccommodationOnMap();
                    }
                });
            }

            addAccommodationButton.setText("Update Accommodation");
        }
    }


    // Update your initialize() method to include the toggle button setup:
    @FXML
    private void initialize() {
        // Initialize choice box
        typeChoiceBox.setItems(FXCollections.observableArrayList("HOTEL", "AIRBNB", "GUESTHOUSE"));
        typeChoiceBox.setValue("HOTEL");

        // Initialize coordinates label
        updateCoordinatesLabel();

        // Initialize toggle button
//        if (toggleMarkerModeButton != null) {
//            toggleMarkerModeButton.setText("🎯 Enable Location Selection");
//            toggleMarkerModeButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white;");
//        }

        // Initialize map with delay to ensure MapView is ready
        Platform.runLater(() -> {
            initializeMap();
        });
    }

    // Update your initializeMap() method:
    private void initializeMap() {
        try {
            if (mapView == null) {
                System.err.println("mapView is null!");
                return;
            }

            System.out.println("Initializing Gluon MapView...");
            mapController = new MapController(mapView);

            // Wait for map to be ready before enabling interactions
            Platform.runLater(() -> {
                // Give map more time to initialize
                new Thread(() -> {
                    try {
                        Thread.sleep(1000); // Wait 1 second for map initialization
                        Platform.runLater(() -> {
                            if (mapController.isMapReady()) {
                                // DON'T enable marker placement by default - let user choose
                                mapInitialized = true;

                                mapView.setOnMouseEntered(event -> scrollPane.setPannable(false));
                                mapView.setOnMouseExited(event -> scrollPane.setPannable(true));
                                mapView.setOnZoom(event -> scrollPane.setPannable(false));
                                mapController.enableMarkerPlacement(this::onMapMarkerPlaced);

//                                zoomInButton.setOnAction(e -> {
//                                    double currentZoom = mapView.getZoom();
//                                    mapView.setZoom(Math.min(currentZoom + 1, 20)); // Max zoom limit (example)
//                                });
//
//                                zoomOutButton.setOnAction(e -> {
//                                    double currentZoom = mapView.getZoom();
//                                    mapView.setZoom(Math.max(currentZoom - 1, 1)); // Min zoom limit (example)
//                                });
                                zoomInButton.setOnAction(e -> {
                                    double centerX = mapView.getWidth() / 2;
                                    double centerY = mapView.getHeight() / 2;
                                    mapController.zoomToPoint(centerX, centerY, +1);
                                });

                                zoomOutButton.setOnAction(e -> {
                                    double centerX = mapView.getWidth() / 2;
                                    double centerY = mapView.getHeight() / 2;
                                    mapController.zoomToPoint(centerX, centerY, -1);
                                });


                                // If we have existing coordinates, show them on the map
                                if (hasValidCoordinates) {
                                    showAccommodationOnMap();
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            });

        } catch (Exception e) {
            System.err.println("Error initializing Gluon map: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to initialize map. Please check your configuration and try again.");
            mapInitialized = false;
        }
    }

    private void showAccommodationOnMap() {
        if (mapController != null && hasValidCoordinates && mapController.isMapReady()) {
            try {
                MapPoint point = new MapPoint(selectedLatitude, selectedLongitude);

                // Add marker and center map
                mapController.addMarker(point);
                mapController.setMapCenter(selectedLatitude, selectedLongitude);

            } catch (Exception e) {
                System.err.println("Error showing accommodation on map: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Cannot show accommodation on map - mapController: " +
                    (mapController != null) + ", hasValidCoordinates: " + hasValidCoordinates +
                    ", mapReady: " + (mapController != null ? mapController.isMapReady() : false));
        }
    }

    private void onMapMarkerPlaced(double latitude, double longitude) {
        System.out.println("AccommodationController: Marker placed at: " + latitude + ", " + longitude);
        selectedLatitude = latitude;
        selectedLongitude = longitude;
        hasValidCoordinates = true;
        updateCoordinatesLabel();
    }

    private void updateCoordinatesLabel() {
        if (coordinatesLabel != null) {
            if (hasValidCoordinates) {
                coordinatesLabel.setText(String.format("Coordinates: %.6f, %.6f", selectedLatitude, selectedLongitude));
                coordinatesLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #059669; -fx-font-family: monospace; -fx-font-weight: bold;");
            } else {
                coordinatesLabel.setText("Coordinates: Not set");
                coordinatesLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ef4444; -fx-font-family: monospace;");
            }
        }
    }

    @FXML
    public void geocodeAddress(ActionEvent actionEvent) {
        String address = addressField.getText().trim();
        if (address.isEmpty()) {
            showError("Please enter an address first.");
            return;
        }

        if (!mapInitialized) {
            showError("Map is not ready yet. Please wait a moment and try again.");
            return;
        }

        // Disable button and show loading state
        geocodeButton.setDisable(true);
        geocodeButton.setText("🔄 Finding location...");

        // Run geocoding in background thread
        new Thread(() -> {
            try {
                GeocodingService.Coordinates coords = geocodingService.geocodeAddress(address);

                Platform.runLater(() -> {
                    geocodeButton.setDisable(false);
                    geocodeButton.setText("📍 Get Location from Address");

                    if (coords.isValid) {
                        selectedLatitude = coords.latitude;
                        selectedLongitude = coords.longitude;
                        hasValidCoordinates = true;
                        updateCoordinatesLabel();

                        // Show location on map
                        showAccommodationOnMap();
                        showSuccessMessage("Location found successfully!");
                    } else {
                        showError("Could not find coordinates for the given address. Please try a more specific address or set the location manually on the map.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    geocodeButton.setDisable(false);
                    geocodeButton.setText("📍 Get Location from Address");
                    showError("Error occurred while finding location: " + e.getMessage());
                });
            }
        }).start();
    }

    public void clearMap(ActionEvent actionEvent) {
        try {
            if (mapController != null && mapController.isMapReady()) {
                mapController.clearMarkers();
            }
            selectedLatitude = 0.0;
            selectedLongitude = 0.0;
            hasValidCoordinates = false;
            updateCoordinatesLabel();
            System.out.println("Map markers cleared");
        } catch (Exception e) {
            System.err.println("Error clearing map: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void closeWindow() {
        ((Stage) capacityField.getScene().getWindow()).close();
    }

    @FXML
    public void saveAccommodation(ActionEvent actionEvent) {
        // Validate basic fields
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String typeValue = typeChoiceBox.getValue();
        String capacityText = capacityField.getText().trim();

        // Field validation
        if (name.isEmpty() || !name.matches("[a-zA-Z0-9\\s]+")) {
            showError("Please enter a valid name (letters and numbers only).");
            return;
        }

        if (address.isEmpty()) {
            showError("Address cannot be empty.");
            return;
        }

        if (typeValue == null || typeValue.isEmpty()) {
            showError("Please select a type of accommodation.");
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityText);
            if (capacity <= 0) {
                showError("Capacity must be a positive integer.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid capacity.");
            return;
        }

        TypeAccommodation typeEnum;
        try {
            typeEnum = TypeAccommodation.valueOf(typeValue);
        } catch (IllegalArgumentException e) {
            showError("Invalid accommodation type selected.");
            return;
        }

        // Creation or update
        if (accommodation == null) {
            accommodation = new Accommodation();
        }

        String photo = Shared.uploadProfilePhoto(selectedPhoto, Shared.ACCOMMODATION_PATH);

        accommodation.setName(name);
        accommodation.setAddress(address);
        accommodation.setType(typeEnum);
        accommodation.setCapacity(capacity);
        accommodation.setPhotosAccommodation(photo);

        // Save coordinates if available
        if (hasValidCoordinates) {
            accommodation.setLatitude(selectedLatitude);
            accommodation.setLongitude(selectedLongitude);
            System.out.println("Saving accommodation with coordinates: " + selectedLatitude + ", " + selectedLongitude);
        } else {
            System.out.println("Saving accommodation without coordinates");
        }

        try {
            if (accommodation.getIdAccommodation() == 0) {
                accommodationService.add(accommodation);
                TableAccommodationController.getInstance().loadAccommodationsFromDatabase();
                showSuccessMessage("Accommodation added successfully!");
            } else {
                accommodationService.update(accommodation);
                showSuccessMessage("Accommodation updated successfully!");
            }

            closeWindow();
        } catch (Exception e) {
            System.err.println("Error saving accommodation: " + e.getMessage());
            e.printStackTrace();
            showError("Error saving accommodation: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Operation Failed");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void showSuccessMessage(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public void selectPhoto(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) buttonPhoto.getScene().getWindow();
        selectedPhoto = fileChooser.showOpenDialog(stage);

        if (selectedPhoto != null) {
            System.out.println("Selected photo: " + selectedPhoto.getAbsolutePath());
            try {
                Image image = new Image(selectedPhoto.toURI().toString());
                accommodationImageView.setImage(image);
            } catch (Exception e) {
                System.err.println("Error loading selected image: " + e.getMessage());
                e.printStackTrace();
                Shared.showAlert(Alert.AlertType.ERROR, "Error", "Could not load the selected image.");
            }
        }
    }
}