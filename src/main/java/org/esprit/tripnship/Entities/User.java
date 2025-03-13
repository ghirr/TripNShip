package org.esprit.tripnship.Entities;

import java.time.LocalDateTime;

public sealed class User permits Client,Employee {

    private int idUser;
    private String firstname;
    private String lastname;
    private Gender gender;
    private Role role;
    private String email;
    private String password;
    private String profilePhoto;
    private LocalDateTime birthdayDate;
    private String phoneNumber;

    public User(int idUser, String firstname, String lastname, Gender gender, Role role, String email, String password, String profilePhoto, LocalDateTime birthdayDate, String phoneNumber) {
        this.idUser = idUser;
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.role = role;
        this.email = email;
        this.password = password;
        this.profilePhoto = profilePhoto;
        this.birthdayDate = birthdayDate;
        this.phoneNumber = phoneNumber;
    }

    public User(String firstname, String lastname, Gender gender, Role role, String email, String password, String profilePhoto, LocalDateTime birthdayDate, String phoneNumber) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.role = role;
        this.email = email;
        this.password = password;
        this.profilePhoto = profilePhoto;
        this.birthdayDate = birthdayDate;
        this.phoneNumber = phoneNumber;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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

    @Override
    public String toString() {
        return
                "idUser=" + idUser +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
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