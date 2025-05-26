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
import org.Esprit.TripNShip.Utils.Shared;
import org.Esprit.TripNShip.Utils.UserSession;

import java.io.IOException;

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

    public void navigateToTransportManagement(ActionEvent actionEvent) {
    }

    public void navigateToAccommodationManagement(ActionEvent actionEvent) {
    }

    public void navigateToCircuitManagement(ActionEvent actionEvent) {
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
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent newLoadedPane = loader.load();

            // Clear old content and set new content
            contentArea.getChildren().clear();
            contentArea.getChildren().add(newLoadedPane);

            // Make sure the loaded pane fills the contentArea
            AnchorPane.setTopAnchor(newLoadedPane, 0.0);
            AnchorPane.setBottomAnchor(newLoadedPane, 0.0);
            AnchorPane.setLeftAnchor(newLoadedPane, 0.0);
            AnchorPane.setRightAnchor(newLoadedPane, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
