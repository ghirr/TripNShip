package org.Esprit.TripNShip.Tests;

import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Entities.TypeRoom;
import org.Esprit.TripNShip.Services.AccommodationService;
import org.Esprit.TripNShip.Services.RoomService;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class TestRoomCRUD {

    public static void main(String[] args) {
        RoomService roomService = new RoomService();
        AccommodationService accommodationService = new AccommodationService();

        String sourceFolder = "C:\\Users\\YJAZIRI\\Desktop\\AllRoomImage";
        String xamppFolder = "C:\\xampp\\htdocs\\tripnship\\roomImages";
        String baseUrl = "http://localhost/tripnship/roomImages";

        // 🔄 Étape 1 : Copier les images et créer une map nom → URL
        Map<String, String> roomPhotoMap = uploadImagesToXamppMap(sourceFolder, xamppFolder, baseUrl);

        if (roomPhotoMap.size() < 6) {
            System.out.println("⚠️ Pas assez d'images disponibles. Veuillez en ajouter dans le dossier : " + sourceFolder);
            return;
        }

        // 🔄 Étape 2 : Récupérer les hébergements
        List<Accommodation> accommodations = accommodationService.getAll();
        if (accommodations.size() < 9) {
            System.out.println("⚠️ Veuillez insérer au moins 9 hébergements dans la base de données.");
            return;
        }

        Accommodation accommodation7 = findAccommodationById(accommodations, 7);
        Accommodation accommodation5 = findAccommodationById(accommodations, 5);

        if (accommodation7 == null || accommodation5 == null) {
            System.out.println("❌ Hébergement 5 ou 7 introuvable.");
            return;
        }

        // 🔄 Étape 3 : Création des objets Room
        List<Room> rooms = List.of(

        );


        // 🔄 Étape 4 : Ajout dans la base
        for (Room room : rooms) {
            roomService.add(room);
        }

        System.out.println("📌 Liste des chambres après ajout :");
        roomService.getAll().forEach(System.out::println);

        // 🔄 Étape 5 : Mise à jour
        List<Room> allRooms = roomService.getAll();
        if (!allRooms.isEmpty()) {
            Room roomToUpdate = allRooms.get(0);
            roomToUpdate.setNameRoom("Updated Single Room 101");
            roomToUpdate.setPrice(90.0);
            roomService.update(roomToUpdate);
        }

        System.out.println("\n📌 Liste des chambres après mise à jour :");
        roomService.getAll().forEach(System.out::println);

        // 🔄 Étape 6 : Suppression
        allRooms = roomService.getAll();
        if (allRooms.size() > 2) {
            roomService.delete(allRooms.get(2));
            roomService.delete(allRooms.get(1));
        }

        System.out.println("\n📌 Liste des chambres après suppression :");
        roomService.getAll().forEach(System.out::println);
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
                imageMap.put(nameWithoutExt, imageUrl);
            } catch (IOException e) {
                System.err.println("❌ Erreur de copie pour : " + image.getName() + " -> " + e.getMessage());
            }
        }

        return imageMap;
    }

    // 🔧 Méthode utilitaire pour retrouver une accommodation par ID
    private static Accommodation findAccommodationById(List<Accommodation> list, int id) {
        return list.stream()
                .filter(a -> a.getIdAccommodation() == id)
                .findFirst()
                .orElse(null);
    }

    // 🔧 Méthode utilitaire pour récupérer une photo ou une liste vide
    private static List<String> getPhotoList(Map<String, String> photoMap, String roomName) {
        String url = photoMap.get(roomName);
        if (url == null) {
            System.out.println("⚠️ Image manquante pour : " + roomName);
            return Collections.emptyList();
        }
        return List.of(url);
    }
}
