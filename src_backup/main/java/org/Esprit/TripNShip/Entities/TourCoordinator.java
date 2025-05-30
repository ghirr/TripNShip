package org.Esprit.TripNShip.Entities;

import java.time.LocalDateTime;

public non-sealed class TourCoordinator extends Employee {
    public TourCoordinator(int idUser, String firstname, String lastname, Gender gender, String email, String password, String profilePhoto, LocalDateTime birthdayDate, String phoneNumber, String address, double salary, LocalDateTime hireDate) {
        super(idUser, firstname, lastname, gender, Role.TOUR_COORDINATOR, email, password, profilePhoto, birthdayDate, phoneNumber, address, salary, hireDate);
    }

    public TourCoordinator(String firstname, String lastname, Gender gender, String email, String password, String profilePhoto, LocalDateTime birthdayDate, String phoneNumber, String address, double salary, LocalDateTime hireDate) {
        super(firstname, lastname, gender, Role.TOUR_COORDINATOR, email, password, profilePhoto, birthdayDate, phoneNumber, address, salary, hireDate);
    }
}
