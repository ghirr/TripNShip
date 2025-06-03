package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.Esprit.TripNShip.Entities.VehicleRental;
import org.Esprit.TripNShip.Services.VehicleRentalService;
import org.Esprit.TripNShip.Utils.UserSession;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListVehicleBookingController {

    @FXML
    private GridPane gridPane;

    private final VehicleRentalService vehicleRentalService = new VehicleRentalService();
    private int defaultUserId = UserSession.getInstance().getUserId();

    @FXML
    public void initialize() {
        List<VehicleRental> rentals = vehicleRentalService.getVehicleRentalsByUserId(defaultUserId);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        gridPane.setHgap(30);
        gridPane.setVgap(30);
        gridPane.setAlignment(Pos.TOP_CENTER);

        if (rentals.isEmpty()) {
            Label noDataLabel = new Label("No vehicle rentals found for user ID " + defaultUserId);
            noDataLabel.setStyle("-fx-padding: 20; -fx-font-style: italic; -fx-font-size: 14px; -fx-text-fill: #666;");
            gridPane.add(noDataLabel, 0, 0);
            return;
        }

        int col = 0;
        int row = 0;
        for (VehicleRental rental : rentals) {
            VBox card = createCard(rental, dtf);
            gridPane.add(card, col, row);
            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createCard(VehicleRental rental, DateTimeFormatter dtf) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setPrefWidth(300);
        card.setStyle("""
            -fx-background-color: #ffffff;
            -fx-border-radius: 15;
            -fx-background-radius: 15;
            -fx-border-color: #4a6cf7;
            -fx-border-width: 1.5;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, rgba(74,108,247,0.12), 12, 0.3, 0, 4);
            -fx-transition: all 0.3s ease;
        """);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(12);
        shadow.setColor(Color.rgb(74, 108, 247, 0.3));
        shadow.setSpread(0.15);

        card.setOnMouseEntered(e -> {
            card.setEffect(shadow);
            card.setStyle("""
                -fx-background-color: #f4f7ff;
                -fx-border-radius: 15;
                -fx-background-radius: 15;
                -fx-border-color: #3751db;
                -fx-border-width: 1.8;
                -fx-cursor: hand;
                -fx-transition: all 0.3s ease;
            """);
        });

        card.setOnMouseExited(e -> {
            card.setEffect(null);
            card.setStyle("""
                -fx-background-color: #ffffff;
                -fx-border-radius: 15;
                -fx-background-radius: 15;
                -fx-border-color: #4a6cf7;
                -fx-border-width: 1.5;
                -fx-cursor: hand;
                -fx-transition: all 0.3s ease;
            """);
        });

        Label title = new Label("Rental #" + rental.getIdRental());
        title.setFont(Font.font("Segoe UI Semibold", FontWeight.SEMI_BOLD, 22));
        title.setTextFill(Color.web("#3751db"));

        Label start = createInfoLabel("🟢", "Start: " + rental.getStartDate().format(dtf));
        Label end = createInfoLabel("🔴", "End: " + rental.getEndDate().format(dtf));
        Label price = createInfoLabel("💲", String.format("Price: %.2f USD", rental.getTotalPrice()));
        Label status = createInfoLabel("📋", "Status: " + rental.getStatusCircuit().name());
        Label brand = createInfoLabel("🚗", "Vehicle: " + rental.getVehicle().getBrand());

        card.getChildren().addAll(title, start, end, price, status, brand);

        return card;
    }

    private Label createInfoLabel(String icon, String text) {
        Label label = new Label(icon + " " + text);
        label.setFont(Font.font("Segoe UI", 15));
        label.setTextFill(Color.web("#505050"));
        label.setWrapText(true);
        label.setTextAlignment(TextAlignment.LEFT);
        label.setPadding(new Insets(4, 0, 4, 0));
        return label;
    }
}
