package org.Esprit.TripNShip.Entities;

import java.time.LocalDateTime;

public non-sealed class Client extends User {

    public Client(int idUser, String firstname, String lastname, Gender gender, String email, String password, String profilePhoto, LocalDateTime birthdayDate, String phoneNumber) {
        super(idUser, firstname, lastname, gender, Role.CLIENT, email, password, profilePhoto, birthdayDate, phoneNumber);
    }

    public Client(String firstname, String lastname, Gender gender, String email, String password, String profilePhoto, LocalDateTime birthdayDate, String phoneNumber) {
        super(firstname, lastname, gender, Role.CLIENT, email, password, profilePhoto, birthdayDate, phoneNumber);
    }

    @Override
    public String toString() {
        return "Client{ " + super.toString() + " }";
    }
}
