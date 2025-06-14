package org.Esprit.TripNShip.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.Esprit.TripNShip.Entities.Role;
import org.Esprit.TripNShip.Utils.Shared;
import org.Esprit.TripNShip.Utils.UserSession;

import java.io.IOException;
import java.net.URL;

public class AdminNavigation {

    @FXML
    public Button btnDashboard,btnUserManagement,btnTransportManagement,btnAccommodationManagement,btnCircuitManagement,btnExpeditionManagement;
    @FXML
    public ImageView logoutIcon,userIcon;
    @FXML
    public Label username;
    @FXML
    public AnchorPane contentArea;


    private static AdminNavigation instance;
    public VBox subMenuTransportManagement;
    public Button btnTransports;
    public Button btnItinerary;
    public Button btnTicket;
    public ImageView arrowIconTransportManagement;
    public ImageView arrowIconAccommodationManagement;
    public VBox subMenuAccommodationManagement;
    public Button btnAccommodation;
    public Button btnRoom;
    public ImageView arrowIconCircuitManagement;
    public VBox SubMenuCircuitManagement;
    public Button btnRentalAgency;
    public Button btnVehicle;
    public Button btnVehicleRental;
    public Button btnTourCircuit;
    public Button btnCircuitBooking;
    public ImageView arrowIconExpeditionManagement;
    public VBox SubMenuExpeditionManagement;
    public Button btnAssignExpedition;
    public Button btnExpeditionOverview;
    public Button btnExpeditionAgent;
    private boolean isSubMenuTransportManagementVisible = false;
    private boolean isSubMenuAccommodationManagementVisible = false;
    private boolean isSubMenuCircuitManagementVisible = false;
    private boolean isSubMenuExpeditionManagementVisible = false;

    public AdminNavigation() {
        instance = this;
    }

    public static AdminNavigation getInstance(){
        return instance;
    }


    @FXML
    public void initialize() {

        UserSession currentUser = UserSession.getInstance();
        if(currentUser==null){
            logout();
        }
        else{

            if (!currentUser.getUserRole().equals(Role.ADMIN)) {
                btnDashboard.setVisible(false);
                btnUserManagement.setVisible(false);
                btnExpeditionOverview.setVisible(false);
            }

            if (!currentUser.getUserRole().equals(Role.TRAVEL_ORGANIZER) && !currentUser.getUserRole().equals(Role.ADMIN)) {
                btnTransportManagement.setVisible(false);
            }

            if (!currentUser.getUserRole().equals(Role.ACCOMMODATION_SPECIALIST) && !currentUser.getUserRole().equals(Role.ADMIN)) {
                btnAccommodationManagement.setVisible(false);
            }

            if (!currentUser.getUserRole().equals(Role.TOUR_COORDINATOR) && !currentUser.getUserRole().equals(Role.ADMIN)) {
                btnCircuitManagement.setVisible(false);
            }

            if (!currentUser.getUserRole().equals(Role.SHIPPING_COORDINATOR) && !currentUser.getUserRole().equals(Role.ADMIN)) {
                btnExpeditionManagement.setVisible(false);
            }

            if (!currentUser.getUserRole().equals(Role.TRANSPORTER)) {
                btnExpeditionAgent.setVisible(false);
            }

            username.setText(currentUser.getUserFirstName()+" "+currentUser.getUserLastName());
            userIcon.setImage(new Image(currentUser.getUserProfilePhotoPath()));
            btnUserManagement.setOnAction(event -> {navigateToUserManagement();});
            btnDashboard.setOnMouseClicked(event -> {navigateToDashBoard();});
            navigateToDashBoard();
            userIcon.setOnMouseClicked(event -> {navigateToProfile();});
            username.setOnMouseClicked(event -> navigateToProfile());
            currentUser.getprofilePhotoUrlProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.isEmpty()) {
                    userIcon.setImage(new Image(newVal));
                }
            });
        }
    }


    public void navigateToDashBoard() {
        loadView("/fxml/dashboardAdmin.fxml");
    }

    public void navigateToUserManagement() {
        loadView("/fxml/listUsers.fxml");
    }

    public void navigateToCircuitManagement() {
        loadView("/fxml/CircuitManagementFXML/Dashboard.fxml");
    }

    public void navigateToExpeditionManagement(ActionEvent actionEvent) {
        loadView("/fxml/ExpeditionManagement/AgentPendingExpeditions.fxml");
    }

    public void navigateToProfile() {
        loadView("/fxml/employeeProfile.fxml");
    }

    public void logout() {
        UserSession.clearSession();
        Shared.switchScene(btnUserManagement,getClass().getResource("/fxml/login.fxml"),"Login");
    }

    private void loadView(String fxmlFile) {
        try {
            URL resource = getClass().getResource(fxmlFile);
            if (resource == null) {
                System.err.println("Fichier FXML introuvable : " + fxmlFile);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent newLoadedPane = loader.load();

            contentArea.getChildren().setAll(newLoadedPane);

            AnchorPane.setTopAnchor(newLoadedPane, 0.0);
            AnchorPane.setBottomAnchor(newLoadedPane, 0.0);
            AnchorPane.setLeftAnchor(newLoadedPane, 0.0);
            AnchorPane.setRightAnchor(newLoadedPane, 0.0);

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du fichier FXML : " + fxmlFile);
            e.printStackTrace();
        }
    }


    public void navigateToItineraryManagement(ActionEvent actionEvent) {
        loadView("/fxml/Itinerary.fxml");

    }

    public void navigateToTicketManagement(ActionEvent actionEvent) {
        loadView("/fxml/Ticket.fxml");
    }

    public void navigateToTransportManagement(ActionEvent actionEvent) {
        loadView("/fxml/Transport.fxml");
    }

    public void toggleSubMenuTransportManagement(ActionEvent actionEvent) {
        toggleSubMenu(subMenuTransportManagement, arrowIconTransportManagement, isSubMenuTransportManagementVisible);
        isSubMenuTransportManagementVisible = !isSubMenuTransportManagementVisible;
    }

    private void toggleSubMenu(VBox subMenu, ImageView arrowIcon, boolean isVisible) {
        boolean newState = !isVisible;
//        Timeline timeline = new Timeline(
//                new KeyFrame(Duration.millis(200),
//                        new KeyValue(subMenu.prefHeightProperty(), newState ? 80 : 0),
//                        new KeyValue(subMenu.opacityProperty(), newState ? 1 : 0)));
//        timeline.play();
        subMenu.setManaged(newState);
        subMenu.setVisible(newState);
        arrowIcon.setRotate(newState ? 90 : 0);
    }

    public void toggleSubMenuAccommodationManagement(ActionEvent actionEvent) {
        toggleSubMenu(subMenuAccommodationManagement, arrowIconAccommodationManagement, isSubMenuAccommodationManagementVisible);
        isSubMenuAccommodationManagementVisible = !isSubMenuAccommodationManagementVisible;
    }

    public void navigateToAccommodationManagement(ActionEvent actionEvent) {
        loadView("/fxml/AccommodationManagementFXML/TableAccommodation.fxml");
    }

    public void navigateToRoomManagement(ActionEvent actionEvent) {
        loadView("/fxml/AccommodationManagementFXML/Table Room.fxml");
    }

    public void toggleSubMenuCircuitManagement(ActionEvent actionEvent) {
        toggleSubMenu(SubMenuCircuitManagement, arrowIconCircuitManagement, isSubMenuCircuitManagementVisible);
        isSubMenuCircuitManagementVisible = !isSubMenuCircuitManagementVisible;
    }

    public void navigateToRentalAgencyManagement(ActionEvent actionEvent) {
        loadView("/fxml/CircuitManagementFXML/AgencyListView.fxml");
    }

    public void navigateToVehicle(ActionEvent actionEvent) {
        loadView("/fxml/CircuitManagementFXML/VehicleListView.fxml");
    }

    public void navigateToVehicleRental(ActionEvent actionEvent) {
        loadView("/fxml/CircuitManagementFXML/VehicleRentalListView.fxml");
    }

    public void navigateToTourCircuit(ActionEvent actionEvent) {
        loadView("/fxml/CircuitManagementFXML/TourCircuitListView.fxml");
    }

    public void navigateToCircuitBooking(ActionEvent actionEvent) {
        loadView("/fxml/CircuitManagementFXML/CircuitBookingListView.fxml");
    }

    public void toggleSubMenuExpeditionManagement(ActionEvent actionEvent) {
        toggleSubMenu(SubMenuExpeditionManagement, arrowIconExpeditionManagement, isSubMenuExpeditionManagementVisible);
        isSubMenuExpeditionManagementVisible = !isSubMenuExpeditionManagementVisible;
    }

    public void navigateToAssignExpedition(ActionEvent actionEvent) {
        loadView("/fxml/ExpeditionManagement/AgentPendingExpeditions.fxml");
    }

    public void navigateToExpeditionOverview(ActionEvent actionEvent) {
        loadView("/fxml/ExpeditionManagement/AdminExpeditionsView.fxml");
    }

    public void navigateToExpeditionAgent(ActionEvent actionEvent) {
        loadView("/fxml/ExpeditionManagement/TransporterExpeditions.fxml");
    }
}
