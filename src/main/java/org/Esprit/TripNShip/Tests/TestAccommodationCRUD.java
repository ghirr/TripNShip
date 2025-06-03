package org.Esprit.TripNShip.Tests;

import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.TypeAccommodation;
import org.Esprit.TripNShip.Services.AccommodationService;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;


public class TestAccommodationCRUD {

    public static void main(String[] args) {
        AccommodationService accommodationService = new AccommodationService();

        String sourceFolder = "C:\\Users\\YJAZIRI\\Desktop\\AllRoomImage";
        String xamppFolder = "C:\\xampp\\htdocs\\tripnship\\accommodationImages";
        String baseUrl = "http://localhost/tripnship/accommodationImages";

        // 🔄 Étape 1 : Copier les images et créer une map nom → URL
        Map<String, String> accommodationPhotoMap = uploadImagesToXamppMap(sourceFolder, xamppFolder, baseUrl);

        if (accommodationPhotoMap.size() < 6) {
            System.out.println("⚠️ Pas assez d'images disponibles. Veuillez en ajouter dans le dossier : " + sourceFolder);
            return;
        }

        // 🔄 Étape 2 : Création des hébergements
        List<Accommodation> accommodations = List.of(
                //new Accommodation(TypeAccommodation.HOTEL, "Chiraton", "Tunis, Tunis", 200, String sourceFolder = "C:\\Users\\YJAZIRI\\Desktop\\AllRoomImage"),
                //new Accommodation(TypeAccommodation.AIRBNB, "Cozy Studio1", "Dortmund, Germany", 2, getPhotoList(accommodationPhotoMap, "cozy_studio1")),
                //new Accommodation(TypeAccommodation.GUESTHOUSE, "Ribat Marrakech", "Marrakech, Morocco", 10, getPhotoList(accommodationPhotoMap, "ribat_marrakech"))
        );
        // 🔄 Étape 3 : Ajout dans la base
        for (Accommodation accommodation : accommodations) {
            accommodationService.add(accommodation);
        }

        System.out.println("📌 Liste des hébergements après ajout :");
        accommodationService.getAll().forEach(System.out::println);

        // 🔄 Étape 4 : Mise à jour
        List<Accommodation> allAccommodations = accommodationService.getAll();
        if (!allAccommodations.isEmpty()) {
            Accommodation accommodationToUpdate = allAccommodations.get(0);
            accommodationToUpdate.setName("Hilton Champs-Élysées");
            accommodationToUpdate.setCapacity(300);
            accommodationService.update(accommodationToUpdate);
        }

        System.out.println("\n📌 Liste des hébergements après mise à jour :");
        accommodationService.getAll().forEach(System.out::println);

        // 🔄 Étape 5 : Suppression
        allAccommodations = accommodationService.getAll();
        if (allAccommodations.size() > 2) {
            accommodationService.delete(allAccommodations.get(2));
            accommodationService.delete(allAccommodations.get(1));
        }

        System.out.println("\n📌 Liste des hébergements après suppression :");
        accommodationService.getAll().forEach(System.out::println);
    }

    // 🔧 Méthode utilitaire pour copier les images et générer une map nom → URL
    private static Map<String, String> uploadImagesToXamppMap(String sourceFolder, String xamppFolder, String baseUrl) {
        Map<String, String> imageMap = new HashMap<>();

        File srcDir = new File(sourceFolder);
        File destDir = new File(xamppFolder);

        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        File[] images = srcDir.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".jpg") ||
                        name.toLowerCase().endsWith(".jpeg") ||
                        name.toLowerCase().endsWith(".png")
        );

        if (images == null || images.length == 0) {
            System.out.println("⚠️ Aucun fichier image trouvé dans le dossier source.");
            return imageMap;
        }

        for (File image : images) {
            try {
                Path sourcePath = image.toPath();
                Path destinationPath = Paths.get(destDir.getAbsolutePath(), image.getName());
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

                String nameWithoutExt = image.getName().replaceFirst("[.][^.]+$", ""); // retire l'extension
                String imageUrl = baseUrl + "/" + image.getName();
                imageMap.put(nameWithoutExt.toLowerCase(), imageUrl); // nom en minuscule pour la correspondance
            } catch (IOException e) {
                System.err.println("❌ Erreur de copie pour : " + image.getName() + " -> " + e.getMessage());
            }
        }

        return imageMap;
    }

    // 🔧 Méthode utilitaire pour récupérer une photo ou une liste vide
    private static List<String> getPhotoList(Map<String, String> photoMap, String nameKey) {
        String url = photoMap.get(nameKey.toLowerCase());
        if (url == null) {
            System.out.println("⚠️ Image manquante pour : " + nameKey);
            return Collections.emptyList();
        }
        return List.of(url);
    }
}
