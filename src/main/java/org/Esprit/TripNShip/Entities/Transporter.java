package org.Esprit.TripNShip.Entities;

import org.Esprit.TripNShip.Entities.Gender;
import org.Esprit.TripNShip.Entities.Role;
import org.Esprit.TripNShip.Entities.TransportType;
import org.Esprit.TripNShip.Entities.User;

import java.time.LocalDateTime;

public final class Transporter extends User {

    private ShippingType transportType;
    private String website;

    public Transporter(int idUser, String firstName, String lastName, Gender gender, String email, String password,
                       String profilePhoto, LocalDateTime birthdayDate, String phoneNumber,
                       ShippingType transportType, String website) {
        super(idUser, firstName, lastName, gender, Role.TRANSPORTER, email, password, profilePhoto, birthdayDate, phoneNumber);
        this.transportType = transportType;
        this.website = website;
    }

    public Transporter(String firstName, String lastName, Gender gender, String email, String password,
                       String profilePhoto, LocalDateTime birthdayDate, String phoneNumber,
                       ShippingType transportType, String website) {
        super(firstName, lastName, gender, Role.TRANSPORTER, email, password, profilePhoto, birthdayDate, phoneNumber);
        this.transportType = transportType;
        this.website = website;
    }

    public ShippingType getTransportType() {
        return transportType;
    }

    public void setTransportType(ShippingType transportType) {
        this.transportType = transportType;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return "Transporter{" +
                "idUser=" + getIdUser() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", gender=" + getGender() +
                ", role=" + getRole() +
                ", email='" + getEmail() + '\'' +
                ", profilePhoto='" + getProfilePhoto() + '\'' +
                ", birthdayDate=" + getBirthdayDate() +
                ", phoneNumber='" + getPhoneNumber() + '\'' +
                ", transportType=" + transportType +
                ", website='" + website + '\'' +
                '}';
    }
}
