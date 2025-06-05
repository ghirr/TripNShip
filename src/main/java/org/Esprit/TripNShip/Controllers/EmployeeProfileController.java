package org.Esprit.TripNShip.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Employee;
import org.Esprit.TripNShip.Entities.Gender;
import org.Esprit.TripNShip.Entities.Role;
import org.Esprit.TripNShip.Services.UserService;
import org.Esprit.TripNShip.Utils.UserSession;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static org.Esprit.TripNShip.Utils.Shared.showAlert;

public class EmployeeProfileController {

    public VBox employeeInfo;
    @FXML
    private Label nameLabel, emailLabel, roleLabel, genderLabel, phoneLabel, birthDateLabel;
    @FXML
    private Label employeeAddressLabel, joiningDateLabel, employeeSalaryLabel;
    @FXML
    private ImageView profileImageView;
    @FXML
    private Button editProfileBtn;
    @FXML
    private Circle profilePhotoCircle;

    private Employee employee;
    private final UserService userService = new UserService();

    private static EmployeeProfileController instance;

    public EmployeeProfileController() {
        instance = this;
    }

    public static EmployeeProfileController getInstance(){
        return instance;
    }


    public void initialize() {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10.0);
        dropShadow.setOffsetX(0.0);
        dropShadow.setOffsetY(2.0);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.2));
        profilePhotoCircle.setEffect(dropShadow);
        profileImageView.setClip(new Circle(70, 70, 70));
        profileImageView.setPreserveRatio(false);
        profileImageView.setSmooth(true);
        profileImageView.setCache(true);
        editProfileBtn.setOnAction(e -> handleEditProfile(e));

        if (UserSession.getInstance().getUserRole().equals(Role.CLIENT)){
            employeeInfo.setVisible(false);
        }
        loadUserData();

    }

    @FXML
    private void handleEditProfile(ActionEvent event) {
        try {
            if (employee == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not retrieve user profile.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editEmployeeProfile.fxml"));
            Parent root = loader.load();
            EditEmployeeProfileController controller = loader.getController();
            controller.setUser(employee);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Profile");
            dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/iconLogo.png")));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            dialogStage.setResizable(false);
            dialogStage.setScene(new Scene(root));

            dialogStage.showAndWait();

            // ✅ Rafraîchir les données après fermeture de la popup
//            refreshUserProfile();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Edit Profile dialog: " + e.getMessage());
        }
    }

    @FXML
    private void handleChangePwd(ActionEvent event) {
        try {
            if (employee == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not retrieve user profile.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/changePassword.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Change Password");
            dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/iconLogo.png")));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            dialogStage.setResizable(false);
            dialogStage.setScene(new Scene(root));

            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Change Password dialog: " + e.getMessage());
        }
    }

    @FXML
    private void handleFaceID(ActionEvent event) {
        try {
            if (employee == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not retrieve user profile.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FaceRegistration.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Face ID");
            dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/iconLogo.png")));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            dialogStage.setResizable(false);
            dialogStage.setScene(new Scene(root));

            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Change Password dialog: " + e.getMessage());
        }
    }


    private void loadUserData() {
        UserSession currentUser = UserSession.getInstance();
        employee = userService.getEmployeeById(currentUser.getUserId());
        if (employee != null) {
            nameLabel.setText(employee.getFirstName() + " " + employee.getLastName());
            emailLabel.setText(employee.getEmail());
            Role role = employee.getRole();
            roleLabel.setText(role != null ? role.toString() : "null");
            Gender gender = employee.getGender();
            genderLabel.setText(gender != null ? gender.toString() : "null");
            phoneLabel.setText(employee.getPhoneNumber());
            employeeAddressLabel.setText(employee.getAddress());
            employeeSalaryLabel.setText(employee.getSalary() + "");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime date = employee.getBirthdayDate();
            System.out.println(date);
            birthDateLabel.setText(date!=null ?formatter.format(date) : "null");
            date = employee.getHireDate();
            System.out.println(date);
            joiningDateLabel.setText(date !=null ? formatter.format(date) : "null");
            Image image = new Image(employee.getProfilePhoto());
            profileImageView.setImage(image);
        }
    }


}
