package org.Esprit.TripNShip.Entities;

import org.Esprit.TripNShip.Entities.Gender;
import org.Esprit.TripNShip.Entities.Role;
import org.Esprit.TripNShip.Entities.TransportType;
import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Services.TransportEXType;

import java.time.LocalDateTime;

public final class Transporter extends User {

    private TransportEXType transportType;

    public Transporter(int idUser, String firstName, String lastName, Gender gender, String email, String password,
                       String profilePhoto, LocalDateTime birthdayDate, String phoneNumber,
                       TransportEXType transportType) {
        super(idUser, firstName, lastName, gender, Role.TRANSPORTER, email, password, profilePhoto, birthdayDate, phoneNumber);
        this.transportType = transportType;
    }

    public Transporter(String firstName, String lastName, Gender gender, String email, String password,
                       String profilePhoto, LocalDateTime birthdayDate, String phoneNumber,
                       TransportEXType transportType) {
        super(firstName, lastName, gender, Role.TRANSPORTER, email, password, profilePhoto, birthdayDate, phoneNumber);
        this.transportType = transportType;
    }

    public TransportEXType getTransportType() {
        return transportType;
    }

    public void setTransportType(TransportEXType transportType) {
        this.transportType = transportType;
    }

//    }
//
//    }

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
                '}';
    }
}
