package org.Esprit.TripNShip.Tests;

import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.TypeAccommodation;
import org.Esprit.TripNShip.Services.AccommodationService;

import java.util.List;

public class TestAccommodationCRUD {
    public static void main(String[] args) {
        AccommodationService accommodationService = new AccommodationService();

        // Chemins vers les répertoires d'images
        String hiltonPhotosPath = "C:\\Users\\YJAZIRI\\Downloads\\Hotel";
        String airbnbPhotosPath = "C:\\Users\\YJAZIRI\\Downloads\\AIRBNA";
        String guesthousePhotosPath = "C:\\Users\\YJAZIRI\\Downloads\\Geust house";

        // Chargement des photos
        List<String> hiltonPhotos = accommodationService.getImagePathsFromDirectory(hiltonPhotosPath);
        List<String> airbnbPhotos = accommodationService.getImagePathsFromDirectory(airbnbPhotosPath);
        List<String> guesthousePhotos = accommodationService.getImagePathsFromDirectory(guesthousePhotosPath);

        // Création des hébergements avec photos
        Accommodation accommodation1 = new Accommodation(1, TypeAccommodation.HOTEL, "Hilton", "Paris, France", 150.0f, 200, hiltonPhotos);
        Accommodation accommodation2 = new Accommodation(2, TypeAccommodation.AIRBNB, "Cozy Studio", "Berlin, Germany", 60.0f, 2, airbnbPhotos);
        Accommodation accommodation3 = new Accommodation(3, TypeAccommodation.GUESTHOUSE, "Riad Marrakech", "Marrakech, Morocco", 80.0f, 10, guesthousePhotos);

        // Ajout dans la base de données
        accommodationService.add(accommodation1);
        accommodationService.add(accommodation2);
        accommodationService.add(accommodation3);

        System.out.println("📌 List of accommodations after adding:");
        accommodationService.getAll().forEach(System.out::println);

        // Mise à jour (par exemple, changer le nom ou autre)
        accommodation1.setName("Hilton Champs-Élysées");
        accommodationService.update(accommodation1);

        System.out.println("\n📌 List of accommodations after update:");
        accommodationService.getAll().forEach(System.out::println);

        // Suppression
        accommodationService.delete(accommodation2);
        accommodationService.delete(accommodation3);

        System.out.println("\n📌 List of accommodations after deletion:");
        accommodationService.getAll().forEach(System.out::println);
    }
}
