package org.Esprit.TripNShip.Tests;

import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Services.*;

import java.time.LocalDateTime;
import java.util.List;

public class TestCircuitCRUD {

    public static void main(String[] args) {

        // Creating rental agencies
        RentalAgency agency1 = new RentalAgency("Agence A", "Lyon, 34 Rue de Lyon", "contact@agencea.com", 4.0f);
        RentalAgency agency2 = new RentalAgency("Agence B", "Lyon, 34 Rue de Lyon", "contact@agenceb.com", 4.0f);
        RentalAgency agency3 = new RentalAgency("Agence C", "Marseille, 56 Rue de Marseille", "contact@agencec.com", 3.8f);

        // Creating vehicles for the agency Paris
        Vehicle vehicle1 = new Vehicle("Toyota", "Corolla", "123ABC", 50.0f, true, Type.Car, 3);
        Vehicle vehicle2 = new Vehicle("Ford", "Focus", "456DEF", 55.0f, true, Type.Car, 4);

        // Creating services
        RentalAgencyService agencyService = new RentalAgencyService();
        VehicleService vehicleService = new VehicleService();
        VehicleRentalService rentalService = new VehicleRentalService();

        // Associating vehicles to agency A (idAgency = 1)
        vehicle1.setIdAgency(10);
        vehicle2.setIdAgency(11);

        // Adding vehicles to the database
        vehicleService.add(vehicle1);
        vehicleService.add(vehicle2);

        // Adding agency A to the database
        agencyService.add(agency1);


        // Adding other agencies
        agencyService.add(agency2);
        agencyService.add(agency3);

        // --- Test CREATE: Add vehicle rentals ---
        System.out.println("Test CREATE:");

        // Adding vehicle rentals with existing vehicles (idVehicle = 1 and 2)
        VehicleRental rental1 = new VehicleRental(LocalDateTime.of(2025, 3, 22, 8, 0), LocalDateTime.of(2025, 3, 23, 8, 0), 150.0f, StautCircuit.Active, 48);  // Correct ID for vehicle
        VehicleRental rental2 = new VehicleRental(LocalDateTime.of(2025, 4, 1, 10, 0), LocalDateTime.of(2025, 4, 2, 10, 0), 200.0f, StautCircuit.Completed, 49); // Correct ID for vehicle

        rentalService.add(rental1);
        rentalService.add(rental2);

        // Display added rentals
        rentalService.getAll().forEach(rental -> System.out.println(rental));

        // --- Test UPDATE: Update vehicle rentals ---
        System.out.println("\nTest UPDATE:");
        rental1.setTotalPrice(180.0f);  // Updating the total price for rental1
        rental2.setStatus(StautCircuit.Active);  // Updating the status for rental2

        rentalService.update(rental1);
        rentalService.update(rental2);

        // Display updated rentals
        rentalService.getAll().forEach(rental -> System.out.println(rental));

        // --- Test DELETE: Delete vehicle rentals ---
        System.out.println("\nTest DELETE:");
        rentalService.delete(rental1);
        rentalService.delete(rental2);
        agencyService.delete(agency3);

        // Display remaining rentals (should be empty if everything was deleted correctly)
        rentalService.getAll().forEach(rental -> System.out.println(rental));

        // --- Additional Code for TourCircuit and CircuitBooking CRUD ---

        TourCircuitService tourCircuitService = new TourCircuitService();
        CircuitBookingService circuitBookingService = new CircuitBookingService();

        // 1. Add a new tour circuit
        TourCircuit tourCircuit = new TourCircuit(0, "Andalusian Adventure", "Discover the beauty of Spain", 1200.0f, "7 Days", "Spain", GuideIncluded.GUIDE_INCLUDED, null);
        tourCircuitService.add(tourCircuit);



        // Retrieve the ID of the added circuit
        List<TourCircuit> circuits = tourCircuitService.getAll();
        int circuitId = circuits.get(circuits.size() - 1).getIdCircuit();

        // 2. Add a booking for this circuit
        CircuitBooking booking1 = new CircuitBooking(0, LocalDateTime.now(), StatusBooking.Confirmed, circuitId);
        CircuitBooking booking2 = new CircuitBooking(0, LocalDateTime.now().plusDays(2), StatusBooking.Cancelled, circuitId);

        circuitBookingService.add(booking1);
        circuitBookingService.add(booking2);

        // 3. Display all bookings after insertion
        System.out.println("Bookings after addition:");
        circuitBookingService.getAll().forEach(System.out::println);

        // 4. Update a booking
        booking1.setStatusBooking(StatusBooking.Cancelled);
        booking1.setIdBooking(1);
        circuitBookingService.update(booking1);

        // Display all bookings after update
        System.out.println("Bookings after update:");
        circuitBookingService.getAll().forEach(System.out::println);

        // 5. Delete the bookings and the tour circuit
        circuitBookingService.delete(booking1);
        circuitBookingService.delete(booking2);
        tourCircuitService.delete(tourCircuit);

        // 6. Verify that the data has been deleted
        System.out.println("Bookings after deletion:");
        circuitBookingService.getAll().forEach(System.out::println);
    }
}
