package org.Esprit.TripNShip.Utils;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;

public class CustomMarker extends StackPane {

    public CustomMarker(Image emoji, String name, String address, double lat, double lng) {
        // Pin-like polygon
        Polygon pinShape = new Polygon(
                0.0, 0.0,
                30.0, 0.0,
                30.0, 30.0,
                15.0, 45.0,
                0.0, 30.0
        );
        Color color ;
        try {
            color = Color.web("#3b82f6");
        } catch (IllegalArgumentException | NullPointerException e) {
            color = Color.RED; // fallback default
        }
        pinShape.setFill(color);
        pinShape.setEffect(new DropShadow(5, Color.gray(0, 0.3)));
        pinShape.setStroke(Color.WHITE);
        pinShape.setStrokeWidth(2);

        // Use ImageView to display the image
        ImageView iconView = new ImageView(emoji);
        iconView.setFitWidth(20);  // You can adjust size
        iconView.setFitHeight(20);
        iconView.setPreserveRatio(true);
        iconView.setSmooth(true);
        StackPane.setAlignment(iconView, Pos.CENTER);

        this.getChildren().addAll(pinShape, iconView);
        this.setAlignment(Pos.CENTER);

        // Set marker node size
        this.setPrefSize(45, 45);
        this.setPickOnBounds(false); // Let clicks pass through empty areas

        // Store marker information
        this.setUserData(new MarkerInfo(lat, lng, name, address));
    }

    public static class MarkerInfo {
        public final double lat, lng;
        public final String name, address;
        public MarkerInfo(double lat, double lng, String name, String address) {
            this.lat = lat;
            this.lng = lng;
            this.name = name;
            this.address = address;
        }
    }
}
