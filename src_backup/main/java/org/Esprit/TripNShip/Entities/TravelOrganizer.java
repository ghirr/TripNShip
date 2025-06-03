package org.Esprit.TripNShip.Entities;


import java.time.LocalDateTime;

public non-sealed class TravelOrganizer extends Employee {

    public TravelOrganizer(int idUser, String firstname, String lastname, Gender gender, String email, String password, String profilePhoto, LocalDateTime birthdayDate, String phoneNumber, String address, double salary, LocalDateTime hireDate) {
        super(idUser, firstname, lastname, gender, Role.TRAVEL_ORGANIZER, email, password, profilePhoto, birthdayDate, phoneNumber, address, salary, hireDate);
    }

    public TravelOrganizer(String firstname, String lastname, Gender gender, String email, String password, String profilePhoto, LocalDateTime birthdayDate, String phoneNumber, String address, double salary, LocalDateTime hireDate) {
        super(firstname, lastname, gender, Role.TRAVEL_ORGANIZER, email, password, profilePhoto, birthdayDate, phoneNumber, address, salary, hireDate);
    }
}
