package org.Esprit.TripNShip.Utils;

import com.mysql.cj.conf.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import org.Esprit.TripNShip.Entities.Role;
import org.Esprit.TripNShip.Entities.User;

public class UserSession {

    private static UserSession instance;

    private int userId;
    private String userFirstName;
    private String userLastName;
    private Role userRole;
    private String userEmail;
    private String userProfilePhotoPath;
    private final SimpleStringProperty profilePhotoUrl = new SimpleStringProperty();

    public UserSession(int userId, String userFirstName, String userLastName, Role userRole, String userEmail, String userProfilePhotoPath) {
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userRole = userRole;
        this.userEmail = userEmail;
        this.userProfilePhotoPath = userProfilePhotoPath;
        this.profilePhotoUrl.set(userProfilePhotoPath);
    }

    public static void initSession(User user) {
        System.out.println(user);
        if (instance == null) {
            instance = new UserSession(user.getIdUser(),user.getFirstName(), user.getLastName(), user.getRole(), user.getEmail(), user.getProfilePhoto());
        }
    }

    public static UserSession getInstance() {
        return instance;
    }

    public static void clearSession() {
        instance = null;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public Role getUserRole() {
        return userRole;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserProfilePhotoPath() {
        return userProfilePhotoPath;
    }

    public SimpleStringProperty getprofilePhotoUrlProperty() {
        return profilePhotoUrl;
    }
    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl.set(profilePhotoUrl);
    }

}
