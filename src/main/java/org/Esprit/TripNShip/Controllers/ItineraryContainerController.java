package org.Esprit.TripNShip.Controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import org.Esprit.TripNShip.Entities.Itinerary;
import org.Esprit.TripNShip.Entities.Transport;
import org.Esprit.TripNShip.Entities.TransportType;
import org.Esprit.TripNShip.Services.ItineraryService;
import org.Esprit.TripNShip.Services.TransportService;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class ItineraryContainerController {

    private ObservableList<Itinerary> itineraryList = FXCollections.observableArrayList();
    @FXML TextField destinationSearchField;
    @FXML TextField departureSearchField;
    @FXML private FlowPane cardContainer;
    @FXML private ComboBox<TransportType> transportationFilterComboBox;
    @FXML private Button myTicketsButton;

    private TransportService ts = new TransportService();
    private List<Transport> transportList = ts.getAll(); // Tous les transports


    ItineraryService is = new ItineraryService();


    @FXML
    public void initialize() {

        itineraryList.addAll(is.getAll());

        // Création de la liste avec l'option "All" (null)
        ObservableList<TransportType> transportTypesWithAll = FXCollections.observableArrayList();
        transportTypesWithAll.add(null); // Option "All" (pas de filtre)
        transportTypesWithAll.addAll(TransportType.values());

        transportationFilterComboBox.setItems(transportTypesWithAll);

        // Personnaliser l'affichage de la ComboBox pour afficher "All" quand c'est null
        transportationFilterComboBox.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(TransportType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(empty ? null : "All");
                } else {
                    setText(item.toString());
                }
            }
        });

        // Même rendu pour la zone affichée quand la ComboBox n'est pas ouverte
        transportationFilterComboBox.setButtonCell(
                transportationFilterComboBox.getCellFactory().call(null)
        );

        transportationFilterComboBox.setValue(null); // Par défaut, "All"

        // Le reste de tes listeners...
        departureSearchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        destinationSearchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        transportationFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Affiche les cartes dès l'initialisation
        applyFilters();

        Platform.runLater(() -> {
            Scene scene = cardContainer.getScene();
            if (scene != null) {
                scene.getStylesheets().add(getClass().getResource("/Style/CardStyle.css").toExternalForm());
            }
        });
    }


    private Transport getTransportForItinerary(Itinerary itinerary) {
        String ref = itinerary.getTransporterReference();
        for (Transport t : transportList) {
            if (t.getTransporterReference().equals(ref)) {
                return t;
            }
        }
        return null; // Pas trouvé
    }

    public void displayCards(List<Itinerary> itineraries) {
        cardContainer.getChildren().clear(); // Reset
        for (Itinerary itinerary : itineraries) {
            Node card = createItineraryCard(itinerary);
            cardContainer.getChildren().add(card);
        }
    }

    private Node createItineraryCard(Itinerary itinerary) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(10));
        card.getStyleClass().add("itinerary-card");
        card.setPrefWidth(800);

        Label code = new Label("Code: " + itinerary.getItineraryCode());
        code.setStyle("-fx-font-weight: bold;-fx-font-size: 15px;");code.setMaxWidth(Double.MAX_VALUE);code.setAlignment(Pos.CENTER);
        Label transporter = new Label("Transporter: " + itinerary.getTransporterReference());
        Label departure = new Label("Departure: " + itinerary.getDepartureLocation() + " at " + itinerary.getDepartureTime());
        Label arrival = new Label("Arrival: " + itinerary.getArrivalLocation() + " at " + itinerary.getArrivalTime());
        Label duration = new Label("Duration: " + itinerary.getDuration());
        String priceText = itinerary.getPrice() == 0 ? "Price Not available" : "Price: " + itinerary.getPrice() + " DT";
        Label price = new Label(priceText);

        Button bookButton = new Button("Book");
        bookButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 6;");

        bookButton.setOnAction(event -> openBookingPage(itinerary, event));

        HBox actions = new HBox(bookButton);
        actions.setAlignment(Pos.CENTER);
        card.setMaxWidth(250);

        card.getChildren().addAll(code, transporter, departure, arrival, duration, price, actions);

        return card;
    }



    private void applyFilters() {
        String departureFilter = departureSearchField.getText() == null ? "" : departureSearchField.getText().toLowerCase();
        String destinationFilter = destinationSearchField.getText() == null ? "" : destinationSearchField.getText().toLowerCase();
        TransportType selectedType = transportationFilterComboBox.getValue();

        cardContainer.getChildren().clear();

        for (Itinerary itinerary : itineraryList) {
            boolean matchesDeparture = itinerary.getDepartureLocation().toLowerCase().contains(departureFilter);
            boolean matchesDestination = itinerary.getArrivalLocation().toLowerCase().contains(destinationFilter);
            Transport transport = getTransportForItinerary(itinerary);
            boolean matchesTransport = selectedType == null || (transport != null && transport.getTransportation() == selectedType);

            if (matchesDeparture && matchesDestination && matchesTransport) {
                Node card = createItineraryCard(itinerary);
                cardContainer.getChildren().add(card);
            }
        }
    }
    private void openBookingPage(Itinerary itinerary, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/BookTicket.fxml"));
            Parent root = loader.load();

            // Passer les données à la page de booking
            BookTicketController controller = loader.getController();
            controller.setItinerary(itinerary);

            Stage bookingStage = new Stage();
            bookingStage.setTitle("Book Your Ticket");
            bookingStage.setScene(new Scene(root));
            bookingStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
