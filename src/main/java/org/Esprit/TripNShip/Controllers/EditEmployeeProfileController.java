package org.Esprit.TripNShip.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.Esprit.TripNShip.Entities.Employee;
import org.Esprit.TripNShip.Entities.Gender;
import org.Esprit.TripNShip.Services.UserService;
import org.Esprit.TripNShip.Utils.Shared;
import org.Esprit.TripNShip.Utils.UserSession;

import java.io.File;

import static org.Esprit.TripNShip.Utils.Shared.showAlert;

public class EditEmployeeProfileController {

    @FXML
    private ImageView profileImageView;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<Gender> genderComboBox;
    @FXML private TextField phoneNumberField;
    @FXML private DatePicker birthdayDatePicker;
    @FXML private Button cancelButton;
    @FXML private Button choosePhotoButton;

    private UserService userService;
    private Employee employee;
    private File selectedProfilePhoto;



    public void initialize() {
        userService = new UserService();

        genderComboBox.getItems().addAll(Gender.values());

        // Disable non-editable fields
        firstNameField.setEditable(false);
        lastNameField.setEditable(false);
        emailField.setEditable(false);
//        roleField.setEditable(false);
//        joiningDateField.setEditable(false);
    }

    public void setUser(Employee user) {
        this.employee = user;

        // Populate fields with user data
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
//        roleField.setText(user.getRole() != null ? user.getRole().toString() : "");
//
//        if (user.getHireDate() != null) {
//            joiningDateField.setText(user.getHireDate().toString());
//        }

        genderComboBox.setValue(user.getGender() != null ? user.getGender() : null);
        phoneNumberField.setText(user.getPhoneNumber());

        if (user.getBirthdayDate() != null) {
            birthdayDatePicker.setValue(user.getBirthdayDate().toLocalDate());
        }

        // Load profile image if available
        loadProfileImage(user.getProfilePhoto());
    }

    // Method to load profile image with error handling
    private void loadProfileImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                // Add a cache-busting parameter to force refresh
                String cacheBreaker = imageUrl.contains("?") ? "&t=" : "?t=";
                String urlWithCacheBuster = imageUrl + cacheBreaker + System.currentTimeMillis();
                System.out.println("Loading image from: " + urlWithCacheBuster);

                Image image = new Image(urlWithCacheBuster);
                profileImageView.setImage(image);
            } catch (Exception e) {
                System.err.println("Error loading profile image: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleChoosePhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) choosePhotoButton.getScene().getWindow();
        selectedProfilePhoto = fileChooser.showOpenDialog(stage);

        if (selectedProfilePhoto != null) {
            System.out.println("Selected photo: " + selectedProfilePhoto.getAbsolutePath());
            try {
                Image image = new Image(selectedProfilePhoto.toURI().toString());
                profileImageView.setImage(image);
            } catch (Exception e) {
                System.err.println("Error loading selected image: " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Could not load the selected image.");
            }
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {

            if (selectedProfilePhoto != null) {
                String photoUrl = Shared.uploadProfilePhoto(selectedProfilePhoto,Shared.USERS_PATH);
                employee.setProfilePhoto(photoUrl);
            }
            // Update user object with editable field values
            employee.setGender(genderComboBox.getValue());
            employee.setPhoneNumber(phoneNumberField.getText());
            employee.setBirthdayDate( birthdayDatePicker.getValue().atStartOfDay());
            
            userService.update(employee);
            UserSession.getInstance().setProfilePhotoUrl(employee.getProfilePhoto());
            EmployeeProfileController.getInstance().initialize();

            Shared.showAlert(Alert.AlertType.INFORMATION, "Success", "Successfully updated the profile");
            closeDialog();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
