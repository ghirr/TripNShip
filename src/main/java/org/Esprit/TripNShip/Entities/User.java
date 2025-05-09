package org.Esprit.TripNShip.Entities;

import java.time.LocalDateTime;
import java.util.Set;

public sealed class User permits Client,Employee {

    private int idUser;
    private String firstName;
    private String lastName;
    private Gender gender;
    private Role role;
    private String email;
    private String password;
    private String profilePhoto;
    private LocalDateTime birthdayDate;
    private String phoneNumber;
    private Set<Asset> assets ;

    public User(int idUser, String firstName, String lastName, Gender gender, Role role, String email, String password, String profilePhoto, LocalDateTime birthdayDate, String phoneNumber) {
        this.idUser = idUser;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.role = role;
        this.email = email;
        this.password = password;
        this.profilePhoto = profilePhoto;
        this.birthdayDate = birthdayDate;
        this.phoneNumber = phoneNumber;
    }

    public User(String firstName, String lastName, Gender gender, Role role, String email, String password, String profilePhoto, LocalDateTime birthdayDate, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.role = role;
        this.email = email;
        this.password = password;
        this.profilePhoto = profilePhoto;
        this.birthdayDate = birthdayDate;
        this.phoneNumber = phoneNumber;
    }

    public User(int idUser, String firstName, String lastName, Gender gender, Role role, String email, String profilePhoto, LocalDateTime birthdayDate, String phoneNumber) {
        this.idUser = idUser;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.role = role;
        this.email = email;
        this.profilePhoto = profilePhoto;
        this.birthdayDate = birthdayDate;
        this.phoneNumber = phoneNumber;
    }

    public User(String firstName, int idUser, String lastName, Gender gender, Role role, String email, String password, String profilePhoto, LocalDateTime birthdayDate, String phoneNumber, Set<Asset> assets) {
        this.firstName = firstName;
        this.idUser = idUser;
        this.lastName = lastName;
        this.gender = gender;
        this.role = role;
        this.email = email;
        this.password = password;
        this.profilePhoto = profilePhoto;
        this.birthdayDate = birthdayDate;
        this.phoneNumber = phoneNumber;
        this.assets = assets;
    }

    public User(int idUser, String profilePhoto) {
        this.idUser = idUser;
        this.profilePhoto = profilePhoto;
    }

    public User(int idUser, String email, String password, Role role) {
        this.idUser = idUser;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(String firstName, String lastName, Role role, String email, String profilePhoto) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.email = email;
        this.profilePhoto = profilePhoto;
    }


    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public LocalDateTime getBirthdayDate() {
        return birthdayDate;
    }

    public void setBirthdayDate(LocalDateTime birthdayDate) {
        this.birthdayDate = birthdayDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<Asset> getAssets() {
        return assets;
    }

    public void setAssets(Set<Asset> assets) {
        this.assets = assets;
    }

    @Override
    public String toString() {
        return
                "idUser=" + idUser +
                ", firstname='" + firstName + '\'' +
                ", lastname='" + lastName + '\'' +
                ", gender=" + gender +
                ", role=" + role +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                ", birthdayDate=" + birthdayDate +
                ", phoneNumber='" + phoneNumber
                ;
    }
}