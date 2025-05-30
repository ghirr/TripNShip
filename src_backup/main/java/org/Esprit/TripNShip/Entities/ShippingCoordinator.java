package org.Esprit.TripNShip.Entities;

import java.time.LocalDateTime;

public non-sealed class ShippingCoordinator extends Employee{


    public ShippingCoordinator(int idUser, String firstname, String lastname, Gender gender, String email, String password, String profilePhoto, LocalDateTime birthdayDate, String phoneNumber, String address, double salary, LocalDateTime hireDate) {
        super(idUser, firstname, lastname, gender, Role.SHIPPING_COORDINATOR, email, password, profilePhoto, birthdayDate, phoneNumber, address, salary, hireDate);
    }

    public ShippingCoordinator(String firstname, String lastname, Gender gender, String email, String password, String profilePhoto, LocalDateTime birthdayDate, String phoneNumber, String address, double salary, LocalDateTime hireDate) {
        super(firstname, lastname, gender, Role.SHIPPING_COORDINATOR, email, password, profilePhoto, birthdayDate, phoneNumber, address, salary, hireDate);
    }
}
