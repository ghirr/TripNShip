package org.esprit.tripnship.Tests;

import org.esprit.tripnship.Entities.Accommodation;
import org.esprit.tripnship.Entities.TypeAccommodation;
import org.esprit.tripnship.Services.AccommodationService;

public class TestAccommodationCRUD {
    public static void main(String[] args) {
        AccommodationService accommodationService = new AccommodationService();

        Accommodation accommodation1 = new Accommodation(0, TypeAccommodation.HOTEL, "Hotel Lux", "10 Paris Street", 100, "Very comfortable");
        Accommodation accommodation2 = new Accommodation(0, TypeAccommodation.AIRBNB, "Cozy Airbnb", "5 Champs Avenue", 4, "Modern and well-located");
        Accommodation accommodation3 = new Accommodation(0, TypeAccommodation.GUESTHOUSE, "Oasis Guesthouse", "3 Desert Street", 8, "Warm atmosphere");

        accommodationService.add(accommodation1);
        accommodationService.add(accommodation2);
        accommodationService.add(accommodation3);

        System.out.println("📌 List of accommodations after adding:");
        accommodationService.getAll().forEach(System.out::println);
        accommodation1.setNote("Improved service and renovated");
        accommodationService.update(accommodation1);

        System.out.println("\n📌 List of accommodations after update:");
        accommodationService.getAll().forEach(System.out::println);

        accommodationService.delete(accommodation2);
        accommodationService.delete(accommodation3);

        System.out.println("\n📌 List of accommodations after deletion:");
        accommodationService.getAll().forEach(System.out::println);
    }
}
