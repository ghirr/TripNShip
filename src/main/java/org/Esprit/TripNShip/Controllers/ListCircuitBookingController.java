package org.Esprit.TripNShip.Controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.Esprit.TripNShip.Entities.CircuitBooking;
import org.Esprit.TripNShip.Services.CircuitBookingService;
import org.Esprit.TripNShip.Utils.UserSession;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListCircuitBookingController {

    @FXML
    private FlowPane cardsContainer;

    private final CircuitBookingService circuitBookingService = new CircuitBookingService();

    private int currentUserId = UserSession.getInstance().getUserId();

    @FXML
    public void initialize() {
        List<CircuitBooking> bookings = circuitBookingService.getCircuitBookingByUserId(currentUserId);
        cardsContainer.getChildren().clear();


        if (bookings.isEmpty()) {
            Label noData = new Label("No circuit bookings found for user ID " + currentUserId);
            noData.setFont(Font.font("Segoe UI", 16));
            noData.setStyle("-fx-text-fill: #555; -fx-font-style: italic;");
            cardsContainer.setAlignment(Pos.CENTER);
            cardsContainer.getChildren().add(noData);
            return;
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (CircuitBooking booking : bookings) {
            VBox card = createCard(booking, dtf);
            cardsContainer.getChildren().add(card);
        }
    }

    private VBox createCard(CircuitBooking booking, DateTimeFormatter dtf) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefWidth(280);
        card.setStyle("""
            -fx-background-color: white;
            -fx-border-radius: 20;
            -fx-background-radius: 20;
            -fx-border-color: #3f51b5;
            -fx-border-width: 2;
            -fx-cursor: hand;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 4);
            -fx-transition: all 0.3s ease;
            """);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(15);
        shadow.setColor(Color.rgb(63, 81, 181, 0.25));

        card.setOnMouseEntered(e -> {
            card.setEffect(shadow);
            card.setStyle("""
                -fx-background-color: white;
                -fx-border-radius: 20;
                -fx-background-radius: 20;
                -fx-border-color: #303f9f;
                -fx-border-width: 2;
                -fx-cursor: hand;
                -fx-transition: all 0.3s ease;
                """);
        });

        card.setOnMouseExited(e -> {
            card.setEffect(null);
            card.setStyle("""
                -fx-background-color: white;
                -fx-border-radius: 20;
                -fx-background-radius: 20;
                -fx-border-color: #3f51b5;
                -fx-border-width: 2;
                -fx-cursor: hand;
                -fx-transition: all 0.3s ease;
                """);
        });

        Label title = new Label("Booking #" + booking.getIdBooking());
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#3f51b5"));

        Label date = createInfoLabel("📅", "Booking Date: " + booking.getBookingDate().format(dtf));
        Label status = createInfoLabel("📋", "Status: " + booking.getStatusBooking().name());
        Label circuit = createInfoLabel("🗺️", "Circuit: " + booking.getTourCircuit().getNameCircuit());

        card.getChildren().addAll(title, date, status, circuit);

        return card;
    }

    private Label createInfoLabel(String icon, String text) {
        Label label = new Label(icon + " " + text);
        label.setFont(Font.font("Segoe UI", 14));
        label.setTextFill(Color.web("#444"));
        label.setWrapText(true);
        label.setTextAlignment(TextAlignment.LEFT);
        label.setPadding(new Insets(3, 0, 3, 0));
        return label;
    }
}
