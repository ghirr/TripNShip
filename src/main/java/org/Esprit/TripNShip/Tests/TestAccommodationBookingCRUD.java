package org.Esprit.TripNShip.Tests;

import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Services.AccommodationBookingService;

import java.util.Date;

public class TestAccommodationBookingCRUD {
//    public static void main(String[] args) {
//        AccommodationBookingService bookingService = new AccommodationBookingService();
//
//        // Création manuelle d'un utilisateur (simulé, normalement à récupérer de la DB)
//        User user = new User(1, "islem@gmail.com", "klqsjfrolzejorp", Role.CLIENT);
//        user.setIdUser(1); // ID existant en base
//
//
//        // Création manuelle d'objets Room (normalement tu devrais les récupérer de la DB)
//        Room room1 = new Room(62, "Single Room 101", TypeRoom.SINGLE);
//        Room room2 = new Room(57, "Double 107", TypeRoom.DOUBLE);
//        Room room3 = new Room(55, "Suite 201", TypeRoom.SUITE);
//        Room room4 = new Room(58, "Double 102", TypeRoom.DOUBLE);
//
//        // Création des réservations avec un User correct
//        AccommodationBooking booking1 = new AccommodationBooking(0, user, room1, new Date(), new Date(System.currentTimeMillis() + 86400000), BookingStatus.PENDING);
//        AccommodationBooking booking2 = new AccommodationBooking(0, user, room2, new Date(), new Date(System.currentTimeMillis() + 172800000), BookingStatus.PENDING);
//        AccommodationBooking booking3 = new AccommodationBooking(0, user, room3, new Date(), new Date(System.currentTimeMillis() + 259200000), BookingStatus.PENDING);
//        AccommodationBooking booking4 = new AccommodationBooking(0, user, room4, new Date(), new Date(System.currentTimeMillis() + 259200000), BookingStatus.PENDING);
//
//        // Ajout
//        bookingService.add(booking1);
//        bookingService.add(booking2);
//        bookingService.add(booking3);
//        bookingService.add(booking4);
//
//        System.out.println("📌 List of reservations after adding:");
//        bookingService.getAll().forEach(System.out::println);
//
//        // Mise à jour du statut
//        booking1.setStatus(BookingStatus.CONFIRMED);
//        booking2.setStatus(BookingStatus.CONFIRMED);
//        booking3.setStatus(BookingStatus.CONFIRMED);
//        bookingService.update(booking1);
//        bookingService.update(booking2);
//        bookingService.update(booking3);
//
//        System.out.println("\n📌 List of reservations after update:");
//        bookingService.getAll().forEach(System.out::println);
//
//        // Suppression
//        bookingService.delete(booking4);
//
//        System.out.println("\n📌 List of reservations after deletion:");
//        bookingService.getAll().forEach(System.out::println);
//    }
}
