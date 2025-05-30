package org.Esprit.TripNShip.Controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
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
    private boolean isSubMenuTransportManagementVisible = false;


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
            username.setText(currentUser.getUserFirstName()+" "+currentUser.getUserLastName());
            userIcon.setImage(new Image(currentUser.getUserProfilePhotoPath()));
            btnUserManagement.setOnAction(event -> {navigateToUserManagement();});
            navigateToUserManagement();
            userIcon.setOnMouseClicked(event -> {navigateToProfile();});
            username.setOnMouseClicked(event -> navigateToProfile());
            currentUser.getprofilePhotoUrlProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.isEmpty()) {
                    userIcon.setImage(new Image(newVal));
                }
            });
        }
    }


    public void navigateToDashBoard(ActionEvent actionEvent) {
    }

    public void navigateToUserManagement() {
        loadView("/fxml/listUsers.fxml");
    }

    public void navigateToAccommodationManagement() {
        loadView("/fxml/Home.fxml");
    }

    public void navigateToCircuitManagement() {
        loadView("/fxml/CircuitManagementFXML/Dashboard.fxml");
    }

    public void navigateToExpeditionManagement(ActionEvent actionEvent) {
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
        loadView("/fxml/itineraryManagement.fxml");

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
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(subMenu.prefHeightProperty(), newState ? 80 : 0),
                        new KeyValue(subMenu.opacityProperty(), newState ? 1 : 0)));
        timeline.play();
        subMenu.setManaged(newState);
        subMenu.setVisible(newState);
        arrowIcon.setRotate(newState ? 90 : 0);
    }
}
