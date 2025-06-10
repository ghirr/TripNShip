package org.Esprit.TripNShip.Utils;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.application.Platform;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.concurrent.Task;

import java.util.function.BiConsumer;
import java.util.concurrent.atomic.AtomicBoolean;

public class MapController {

    private final MapView mapView;
    private final MarkerLayer markerLayer;
    private final AtomicBoolean mapReady = new AtomicBoolean(false);
    private final AtomicBoolean markerPlacementEnabled = new AtomicBoolean(false);
    private final AtomicBoolean isProcessingClick = new AtomicBoolean(false);

    // Variables to track mouse dragging
    private volatile double mouseDownX, mouseDownY;
    private final AtomicBoolean isDragging = new AtomicBoolean(false);
    private static final double DRAG_THRESHOLD = 5.0; // pixels

    // Timing control to prevent rapid-fire events
    private volatile long lastEventTime = 0;
    private static final long MIN_EVENT_INTERVAL = 50; // milliseconds

    public MapController(MapView mapView) {
        this.mapView = mapView;
        this.mapView.setZoom(10);
        this.mapView.setCenter(new MapPoint(36.8065, 10.1815)); // Default to Tunis

        this.markerLayer = new MarkerLayer();
        this.mapView.addLayer(markerLayer);

        // Setup mouse event handlers for drag detection
        setupMouseHandlers();

        // Initialize map ready state safely with proper timing
        initializeMapReadyState();
    }

    private void initializeMapReadyState() {
        // Use a background task to avoid blocking the JavaFX thread
        Task<Void> initTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Wait for map initialization without blocking UI
                Thread.sleep(1000); // Increased wait time for better stability
                return null;
            }
        };

        initTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                mapReady.set(true);
                System.out.println("Map is now ready for interactions");
            });
        });

        initTask.setOnFailed(e -> {
            System.err.println("Failed to initialize map: " + initTask.getException().getMessage());
        });

        Thread initThread = new Thread(initTask);
        initThread.setDaemon(true);
        initThread.start();
    }

    private void setupMouseHandlers() {
        // Mouse pressed - start tracking with throttling
        mapView.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastEventTime < MIN_EVENT_INTERVAL) {
                return; // Throttle rapid events
            }
            lastEventTime = currentTime;

            try {
                mouseDownX = event.getX();
                mouseDownY = event.getY();
                isDragging.set(false);
            } catch (Exception e) {
                System.err.println("Error in mouse pressed handler: " + e.getMessage());
            }
        });

        // Mouse dragged - detect dragging with throttling
        mapView.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastEventTime < MIN_EVENT_INTERVAL / 2) {
                return; // More frequent updates for drag detection
            }
            lastEventTime = currentTime;

            try {
                double deltaX = Math.abs(event.getX() - mouseDownX);
                double deltaY = Math.abs(event.getY() - mouseDownY);

                if (deltaX > DRAG_THRESHOLD || deltaY > DRAG_THRESHOLD) {
                    isDragging.set(true);
                }
            } catch (Exception e) {
                System.err.println("Error in mouse dragged handler: " + e.getMessage());
            }
        });

        // Mouse released - handle click only if not dragging, with proper synchronization
        mapView.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastEventTime < MIN_EVENT_INTERVAL) {
                return; // Throttle rapid events
            }
            lastEventTime = currentTime;

            // Use Platform.runLater to ensure proper thread synchronization
            Platform.runLater(() -> {
                try {
                    if (!isDragging.get() && markerPlacementEnabled.get() && mapReady.get()) {
                        handleMapClickSafely(event);
                    }
                } catch (Exception e) {
                    System.err.println("Error in mouse released handler: " + e.getMessage());
                } finally {
                    isDragging.set(false);
                }
            });
        });
    }

    /**
     * Enables the user to place a marker by clicking on the map.
     * @param onMarkerPlaced Callback with (latitude, longitude) of the click
     */
    public void enableMarkerPlacement(BiConsumer<Double, Double> onMarkerPlaced) {
        Platform.runLater(() -> {
            markerPlacementEnabled.set(true);
            mapView.setUserData(onMarkerPlaced);
        });
    }

    /**
     * Disables marker placement to allow free navigation
     */
    public void disableMarkerPlacement() {
        Platform.runLater(() -> {
            markerPlacementEnabled.set(false);
            mapView.setUserData(null);
        });
    }

    private void handleMapClickSafely(MouseEvent event) {
        // Prevent concurrent clicks from processing
        if (!isProcessingClick.compareAndSet(false, true)) {
            return;
        }

        try {
            handleMapClick(event);
        } finally {
            isProcessingClick.set(false);
        }
    }

    private void handleMapClick(MouseEvent event) {
        try {
            // Additional safety checks
            if (!mapReady.get() || !markerPlacementEnabled.get() || isDragging.get()) {
                return;
            }

            double x = event.getX();
            double y = event.getY();

            // Validate click coordinates with additional buffer
            double mapWidth = mapView.getWidth();
            double mapHeight = mapView.getHeight();

            if (x < 0 || y < 0 || x >= mapWidth || y >= mapHeight || mapWidth <= 0 || mapHeight <= 0) {
                System.out.println("Click coordinates outside valid map bounds: " + x + ", " + y +
                        " (map size: " + mapWidth + "x" + mapHeight + ")");
                return;
            }

            @SuppressWarnings("unchecked")
            BiConsumer<Double, Double> onMarkerPlaced = (BiConsumer<Double, Double>) mapView.getUserData();

            if (onMarkerPlaced != null) {
                // Use background task for map position calculation to avoid blocking UI
                Task<MapPoint> positionTask = new Task<MapPoint>() {
                    @Override
                    protected MapPoint call() throws Exception {
                        return safeGetMapPosition(x, y);
                    }
                };

                positionTask.setOnSucceeded(e -> {
                    MapPoint clickedPoint = positionTask.getValue();
                    if (clickedPoint != null) {
                        double lat = clickedPoint.getLatitude();
                        double lng = clickedPoint.getLongitude();

                        if (isValidCoordinate(lat, lng)) {
                            System.out.println("Valid click at: " + lat + ", " + lng);

                            Platform.runLater(() -> {
                                try {
                                    onMarkerPlaced.accept(lat, lng);
                                    addMarker(lat, lng, "📍 Selected Location", "");
                                } catch (Exception ex) {
                                    System.err.println("Error in marker placement callback: " + ex.getMessage());
                                }
                            });
                        } else {
                            System.out.println("Invalid coordinates: " + lat + ", " + lng);
                        }
                    } else {
                        System.out.println("Could not get map position for click");
                    }
                });

                positionTask.setOnFailed(e -> {
                    System.err.println("Failed to get map position: " + positionTask.getException().getMessage());
                });

                Thread positionThread = new Thread(positionTask);
                positionThread.setDaemon(true);
                positionThread.start();
            }
        } catch (Exception e) {
            System.err.println("Error handling map click: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Safely gets map position with comprehensive error handling for GluonHQ Maps issue #67
     */
    private MapPoint safeGetMapPosition(double x, double y) {
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                // Additional validation before calling getMapPosition
                if (!mapReady.get() || mapView.getWidth() <= 0 || mapView.getHeight() <= 0) {
                    System.err.println("Map not ready or invalid dimensions");
                    return null;
                }

                // Add small delay between retries to allow map stabilization
                if (retryCount > 0) {
                    Thread.sleep(50);
                }

                MapPoint point = mapView.getMapPosition(x, y);
                if (point == null) {
                    System.err.println("Map position is null at x: " + x + ", y: " + y + " (attempt " + (retryCount + 1) + ")");
                    retryCount++;
                    continue;
                }
                return point;

            } catch (IndexOutOfBoundsException e) {
                retryCount++;
                System.err.println("getMapPosition IndexOutOfBounds (attempt " + retryCount + "): " + e.getMessage() +
                        " at x: " + x + ", y: " + y);
                if (retryCount >= maxRetries) {
                    System.err.println("Max retries reached for getMapPosition");
                    return null;
                }
            } catch (IllegalStateException e) {
                System.err.println("IllegalState in getMapPosition: " + e.getMessage());
                return null;
            } catch (Exception e) {
                System.err.println("Exception in getMapPosition: " + e.getMessage());
                return null;
            }
        }

        return null;
    }

    /**
     * Validates if coordinates are reasonable
     */
    private boolean isValidCoordinate(double lat, double lng) {
        return lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180
                && !Double.isNaN(lat) && !Double.isNaN(lng)
                && !Double.isInfinite(lat) && !Double.isInfinite(lng);
    }

    /**
     * Adds a marker at the specified location with metadata.
     */
    public void addMarker(double lat, double lng, String name, String address) {
        Platform.runLater(() -> {
            try {
                if (!isValidCoordinate(lat, lng)) {
                    System.err.println("Invalid coordinates for marker: " + lat + ", " + lng);
                    return;
                }

                CustomMarker marker = new CustomMarker(
                        new Image("images/icon_accommodation_management.png"),
                        name, address, lat, lng
                );

                Tooltip tooltip = new Tooltip(
                        name + "\n📍 " + address +
                                "\nLat: " + String.format("%.6f", lat) +
                                "\nLng: " + String.format("%.6f", lng)
                );
                Tooltip.install(marker, tooltip);

                markerLayer.clearMarkers();
                markerLayer.addMarker(marker);
                setMapCenter(lat, lng);
            } catch (Exception e) {
                System.err.println("Error adding marker: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void addMarker(MapPoint point) {
        if (point != null) {
            addMarker(point.getLatitude(), point.getLongitude(), "📍 Selected Location", "");
        }
    }

    /**
     * Shows an accommodation location on the map.
     */
    public void showAccommodationOnMap(double latitude, double longitude, String name, String address) {
        Platform.runLater(() -> {
            addMarker(latitude, longitude, name, address);
            setMapCenter(latitude, longitude);
        });
    }

    /**
     * Clears all markers from the map.
     */
    public void clearMarkers() {
        Platform.runLater(() -> {
            try {
                markerLayer.clearMarkers();
            } catch (Exception e) {
                System.err.println("Error clearing markers: " + e.getMessage());
            }
        });
    }

    public void setMapCenter(double latitude, double longitude) {
        Platform.runLater(() -> {
            try {
                if (isValidCoordinate(latitude, longitude) && mapReady.get()) {
                    mapView.setCenter(new MapPoint(latitude, longitude));
                }
            } catch (Exception e) {
                System.err.println("Error setting map center: " + e.getMessage());
            }
        });
    }

    public void setMapCenter(double latitude, double longitude, double zoom) {
        Platform.runLater(() -> {
            setMapCenter(latitude, longitude);
            setZoom(zoom);
        });
    }

    public void setZoom(double zoom) {
        Platform.runLater(() -> {
            try {
                if (zoom > 0 && zoom <= 20 && mapReady.get()) {
                    mapView.setZoom(zoom);
                }
            } catch (Exception e) {
                System.err.println("Error setting zoom: " + e.getMessage());
            }
        });
    }

    public void zoomToPoint(double x, double y, double zoomDelta) {
        if (!mapReady.get()) return;

        Platform.runLater(() -> {
            try {
                MapPoint beforeZoom = safeGetMapPosition(x, y);
                if (beforeZoom == null) return;

                double newZoom = getZoom() + zoomDelta;
                if (newZoom < 1 || newZoom > 20) return;

                mapView.setZoom(newZoom);

                // Delay to let the zoom take effect before adjusting center
                Platform.runLater(() -> {
                    try {
                        MapPoint afterZoom = safeGetMapPosition(x, y);
                        if (afterZoom == null) return;

                        // Compute the new center to keep the clicked point under the cursor
                        double latDiff = beforeZoom.getLatitude() - afterZoom.getLatitude();
                        double lngDiff = beforeZoom.getLongitude() - afterZoom.getLongitude();

                        MapPoint currentCenter = mapView.getCenter();
                        MapPoint newCenter = new MapPoint(
                                currentCenter.getLatitude() + latDiff,
                                currentCenter.getLongitude() + lngDiff
                        );

                        mapView.setCenter(newCenter);
                    } catch (Exception e) {
                        System.err.println("Error in zoom adjustment: " + e.getMessage());
                    }
                });

            } catch (Exception e) {
                System.err.println("Error during zoomToPoint: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public MapPoint getMapCenter() {
        try {
            return mapView.getCenter();
        } catch (Exception e) {
            System.err.println("Error getting map center: " + e.getMessage());
            return new MapPoint(36.8065, 10.1815); // Default fallback
        }
    }

    public double getZoom() {
        try {
            return mapView.getZoom();
        } catch (Exception e) {
            System.err.println("Error getting zoom: " + e.getMessage());
            return 10.0; // Default fallback
        }
    }

    public boolean hasMarkers() {
        try {
            return markerLayer.getMarkerCount() > 0;
        } catch (Exception e) {
            System.err.println("Error checking marker count: " + e.getMessage());
            return false;
        }
    }

    public boolean isMapReady() {
        return mapReady.get();
    }

    public boolean isMarkerPlacementEnabled() {
        return markerPlacementEnabled.get();
    }
}