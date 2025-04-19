package org.Esprit.TripNShip.Tests;

import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.TypeAccommodation;
import org.Esprit.TripNShip.Services.AccommodationService;

public class TestAccommodationCRUD {
    public static void main(String[] args) {
        AccommodationService accommodationService = new AccommodationService();

        Accommodation accommodation1 = new Accommodation(1, TypeAccommodation.HOTEL, "Hilton", "Paris, France", 150.0f, 200);
        Accommodation accommodation2 = new Accommodation(1, TypeAccommodation.AIRBNB, "Cozy Studio", "Berlin, Germany", 60.0f, 2);
        Accommodation accommodation3 = new Accommodation(2, TypeAccommodation.GUESTHOUSE, "Riad Marrakech", "Marrakech, Morocco", 80.0f, 10);

        accommodationService.add(accommodation1);
        accommodationService.add(accommodation2);
        accommodationService.add(accommodation3);

        System.out.println("📌 List of accommodations after adding:");
        accommodationService.getAll().forEach(System.out::println);
        accommodationService.update(accommodation1);

        System.out.println("\n📌 List of accommodations after update:");
        accommodationService.getAll().forEach(System.out::println);

        accommodationService.delete(accommodation2);
        accommodationService.delete(accommodation3);

        System.out.println("\n📌 List of accommodations after deletion:");
        accommodationService.getAll().forEach(System.out::println);
    }
}

