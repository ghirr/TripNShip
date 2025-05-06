package org.Esprit.TripNShip.Tests;

import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Services.*;

import java.time.LocalDateTime;
import java.util.List;

public class TestCircuitCRUD {

    public static void main(String[] args) {

        // === Services ===
        RentalAgencyService agencyService = new RentalAgencyService();
        VehicleService vehicleService = new VehicleService();
        VehicleRentalService rentalService = new VehicleRentalService();
        TourCircuitService tourCircuitService = new TourCircuitService();
        CircuitBookingService bookingService = new CircuitBookingService();
        UserService userService = new UserService();

        // === Créer des agences ===
        RentalAgency agency1 = new RentalAgency("Agence A", "Lyon, 34 Rue de Lyon", "contact@agencea.com", 4.0f);
        RentalAgency agency2 = new RentalAgency("Agence B", "Paris, 21 Rue de Paris", "contact@agenceb.com", 4.5f);
        RentalAgency agency3 = new RentalAgency("Agence C", "Marseille, 56 Rue de Marseille", "contact@agencec.com", 3.8f);

        agencyService.add(agency1);
        agencyService.add(agency2);
        agencyService.add(agency3);

        // === Créer des véhicules ===
        Vehicle vehicle1 = new Vehicle("Toyota", "Corolla", "123ABC", 50.0f, true, Type.Car, agency2);
        Vehicle vehicle2 = new Vehicle("Ford", "Focus", "456DEF", 55.0f, true, Type.Car, agency3);

        vehicleService.add(vehicle1);
        vehicleService.add(vehicle2);

        // === Récupérer un véhicule par ID depuis la liste ===
        int desiredVehicleId = vehicle1.getIdVehicle(); // exemple
        Vehicle vehicleFromDB = vehicleService.getAll().stream()
                .filter(v -> v.getIdVehicle() == desiredVehicleId)
                .findFirst()
                .orElse(null);

        // === Récupérer un utilisateur par ID depuis la liste ===
        int desiredUserId = 8;
        User userFromDB = userService.getAll().stream()
                .filter(u -> u.getIdUser() == desiredUserId)
                .findFirst()
                .orElse(null);

        if (vehicleFromDB != null && userFromDB != null) {
            // === Créer des locations ===
            VehicleRental rental1 = new VehicleRental(
                    LocalDateTime.of(2025, 3, 22, 8, 0),
                    LocalDateTime.of(2025, 3, 23, 8, 0),
                    150.0f,
                    StautCircuit.Active,
                    vehicleFromDB,
                    userFromDB
            );

            VehicleRental rental2 = new VehicleRental(
                    LocalDateTime.of(2025, 4, 1, 10, 0),
                    LocalDateTime.of(2025, 4, 2, 10, 0),
                    200.0f,
                    StautCircuit.Completed,
                    vehicleFromDB,
                    userFromDB
            );

            rentalService.add(rental1);
            rentalService.add(rental2);

            System.out.println("📦 All Rentals:");
            rentalService.getAll().forEach(System.out::println);

            // === Update Rentals ===
            rental1.setTotalPrice(180.0f);
            rental2.setStatusCircuit(StautCircuit.Active);

            rentalService.update(rental1);
            rentalService.update(rental2);

            System.out.println("\n✏️ Updated Rentals:");
            rentalService.getAll().forEach(System.out::println);

            // === Delete Rentals ===
            rentalService.delete(rental1);
            rentalService.delete(rental2);

            System.out.println("\n🗑️ Remaining Rentals after deletion:");
            rentalService.getAll().forEach(System.out::println);
        } else {
            System.out.println("❌ Impossible de récupérer le véhicule ou l’utilisateur.");
        }

        // === Créer un circuit touristique ===
        TourCircuit tourCircuit = new TourCircuit(0, "Andalusian Adventure", "Discover Spain's beauty",
                1200.0f, "7 Days", "Spain", true);
        tourCircuitService.add(tourCircuit);

        // Récupérer le dernier ajouté
        List<TourCircuit> circuits = tourCircuitService.getAll();
        if (!circuits.isEmpty()) {
            TourCircuit latestCircuit = circuits.get(circuits.size() - 1);

            // === Réservation du circuit ===
            User bookingUser = userService.getAll().stream()
                    .filter(u -> u.getIdUser() == 7)
                    .findFirst()
                    .orElse(null);

            if (bookingUser != null) {
                CircuitBooking booking = new CircuitBooking();
                booking.setBookingDate(LocalDateTime.now());
                booking.setStatusBooking(StatusBooking.Confirmed);
                booking.setTourCircuit(latestCircuit);
                booking.setUser(bookingUser);

                bookingService.add(booking);

                System.out.println("\n📘 All Bookings:");
                bookingService.getAll().forEach(b -> System.out.println("ID: " + b.getIdBooking()
                        + ", Date: " + b.getBookingDate()
                        + ", Status: " + b.getStatusBooking()));

                // === Update Booking ===
                List<CircuitBooking> bookings = bookingService.getAll();
                if (!bookings.isEmpty()) {
                    CircuitBooking toUpdate = bookings.get(0);
                    toUpdate.setStatusBooking(StatusBooking.Cancelled);
                    bookingService.update(toUpdate);
                }

                System.out.println("\n🔄 Bookings after update:");
                bookingService.getAll().forEach(System.out::println);
            } else {
                System.out.println("❌ Utilisateur pour la réservation introuvable.");
            }
        } else {
            System.out.println("⚠️ Aucun circuit disponible pour réservation.");
        }
    }
}