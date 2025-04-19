package org.Esprit.TripNShip.Tests;

import org.Esprit.TripNShip.Entities.AccommodationBooking;
import org.Esprit.TripNShip.Entities.BookingStatus;
import org.Esprit.TripNShip.Services.AccommodationBookingService;

import java.util.Date;

public class TestAccommodationBookingCRUD {
    public static void main(String[] args) {
        AccommodationBookingService bookingService = new AccommodationBookingService();

        // Creating sample reservations (3 for confirmation, 1 for cancellation)
        AccommodationBooking booking1 = new AccommodationBooking(0, 1, 1, new Date(), new Date(System.currentTimeMillis() + 86400000), BookingStatus.PENDING);
        AccommodationBooking booking2 = new AccommodationBooking(0, 2, 3, new Date(), new Date(System.currentTimeMillis() + 172800000), BookingStatus.PENDING);
        AccommodationBooking booking3 = new AccommodationBooking(0, 3, 103, new Date(), new Date(System.currentTimeMillis() + 259200000), BookingStatus.PENDING);
        AccommodationBooking booking4 = new AccommodationBooking(0, 4, 104, new Date(), new Date(System.currentTimeMillis() + 86400000), BookingStatus.CANCELLED);

        // Add reservations
        bookingService.add(booking1);
        bookingService.add(booking2);
        bookingService.add(booking3);
        bookingService.add(booking4);

        System.out.println("📌 List of reservations after adding:");
        bookingService.getAll().forEach(System.out::println);

        // Update reservations (changing status to CONFIRMED)
        booking1.setStatus(BookingStatus.CONFIRMED);
        booking2.setStatus(BookingStatus.CONFIRMED);
        booking3.setStatus(BookingStatus.CONFIRMED);
        bookingService.update(booking1);
        bookingService.update(booking2);
        bookingService.update(booking3);

        System.out.println("\n📌 List of reservations after update:");
        bookingService.getAll().forEach(System.out::println);

        // Delete reservations (deleting the cancelled reservation)
        bookingService.delete(booking4);

        System.out.println("\n📌 List of reservations after deletion:");
        bookingService.getAll().forEach(System.out::println);
    }
}
