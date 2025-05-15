package org.Esprit.TripNShip.Tests;

import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Entities.TypeRoom;
import org.Esprit.TripNShip.Services.AccommodationService;
import org.Esprit.TripNShip.Services.RoomService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestRoomCRUD {

    public static void main(String[] args) {
        RoomService roomService = new RoomService();
        AccommodationService accommodationService = new AccommodationService();

        // Récupération de tous les hébergements pour lier les chambres
        List<Accommodation> accommodations = accommodationService.getAll();
        if (accommodations.size() < 9) {
            System.out.println("⚠️ Veuillez insérer au moins 9 hébergements dans la base de données.");
            return;
        }

        // Récupérer des chemins de photos
        List<String> photos = getPhotoPathsFromFolders();

        // Création de chambres avec hébergements et photos
        Room room1 = new Room(0, accommodations.get(7), TypeRoom.SINGLE, "Single Room 101", 80.0, true, photos);
        Room room2 = new Room(0, accommodations.get(8), TypeRoom.DOUBLE, "Double Room 102", 120.0, true, photos);
        Room room3 = new Room(0, accommodations.get(6), TypeRoom.SUITE, "Suite 201", 250.0, false, photos);

        // ➕ Ajout des rooms
        roomService.add(room1);
        roomService.add(room2);
        roomService.add(room3);

        System.out.println("📌 List of rooms after adding:");
        roomService.getAll().forEach(System.out::println);

        // ✏️ Mise à jour de la première chambre
        List<Room> allRooms = roomService.getAll();
        if (!allRooms.isEmpty()) {
            Room roomToUpdate = allRooms.get(0);
            roomToUpdate.setNameRoom("Updated Single Room 101");
            roomToUpdate.setPrice(90.0);
            roomService.update(roomToUpdate);
        }

        System.out.println("\n📌 List of rooms after update:");
        roomService.getAll().forEach(System.out::println);

        // ❌ Suppression des deux dernières
        allRooms = roomService.getAll();
        if (allRooms.size() > 2) {
            roomService.delete(allRooms.get(1));
            roomService.delete(allRooms.get(2));
        }

        System.out.println("\n📌 List of rooms after deletion:");
        roomService.getAll().forEach(System.out::println);
    }

    // ✅ Récupère les chemins d'images valides depuis les dossiers spécifiés
    private static List<String> getPhotoPathsFromFolders() {
        List<String> folders = Arrays.asList(
                "C:\\Users\\YJAZIRI\\Desktop\\ho",
                "C:\\Users\\YJAZIRI\\Desktop\\AR",
                "C:\\Users\\YJAZIRI\\Desktop\\GU"
        );

        List<String> photoPaths = new ArrayList<>();

        for (String folderPath : folders) {
            File folder = new File(folderPath);
            if (folder.exists() && folder.isDirectory()) {
                File[] imageFiles = folder.listFiles((dir, name) ->
                        name.toLowerCase().endsWith(".jpg") ||
                                name.toLowerCase().endsWith(".jpeg") ||
                                name.toLowerCase().endsWith(".png")
                );
                if (imageFiles != null) {
                    for (File image : imageFiles) {
                        photoPaths.add(image.getAbsolutePath());
                    }
                }
            } else {
                System.out.println("⚠️ Dossier introuvable : " + folderPath);
            }
        }

        return photoPaths;
    }
}
