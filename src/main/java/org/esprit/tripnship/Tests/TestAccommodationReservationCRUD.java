package org.esprit.tripnship.Tests;

import org.esprit.tripnship.Entities.AccommodationReservation;
import org.esprit.tripnship.Entities.ReservationStatus;
import org.esprit.tripnship.Services.AccommodationReservationService;

import java.util.Date;

public class TestAccommodationReservationCRUD {
    public static void main(String[] args) {
        AccommodationReservationService reservationService = new AccommodationReservationService();

        // Creating sample reservations (3 for confirmation, 1 for cancellation)
        AccommodationReservation reservation1 = new AccommodationReservation(0, 1, 101, new Date(), new Date(System.currentTimeMillis() + 86400000), ReservationStatus.PENDING);
        AccommodationReservation reservation2 = new AccommodationReservation(0, 2, 102, new Date(), new Date(System.currentTimeMillis() + 172800000), ReservationStatus.PENDING);
        AccommodationReservation reservation3 = new AccommodationReservation(0, 3, 103, new Date(), new Date(System.currentTimeMillis() + 259200000), ReservationStatus.PENDING);
        AccommodationReservation reservation4 = new AccommodationReservation(0, 4, 104, new Date(), new Date(System.currentTimeMillis() + 86400000), ReservationStatus.CANCELLED);

        // Add reservations
        reservationService.add(reservation1);
        reservationService.add(reservation2);
        reservationService.add(reservation3);
        reservationService.add(reservation4);

        System.out.println("📌 List of reservations after adding:");
        reservationService.getAll().forEach(System.out::println);

        // Update reservations (changing status to CONFIRMED)
        reservation1.setStatus(ReservationStatus.CONFIRMED);
        reservation2.setStatus(ReservationStatus.CONFIRMED);
        reservation3.setStatus(ReservationStatus.CONFIRMED);
        reservationService.update(reservation1);
        reservationService.update(reservation2);
        reservationService.update(reservation3);

        System.out.println("\n📌 List of reservations after update:");
        reservationService.getAll().forEach(System.out::println);

        // Delete reservations (deleting the cancelled reservation)
        reservationService.delete(reservation4);

        System.out.println("\n📌 List of reservations after deletion:");
        reservationService.getAll().forEach(System.out::println);
    }
}