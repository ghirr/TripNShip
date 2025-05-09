package org.Esprit.TripNShip.Tests;

import org.Esprit.TripNShip.Entities.Accommodation;
import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Entities.TypeRoom;
import org.Esprit.TripNShip.Services.AccommodationService;
import org.Esprit.TripNShip.Services.RoomService;

import java.util.List;

public class TestRoomCRUD {
    public static void main(String[] args) {
        RoomService roomService = new RoomService();
        AccommodationService accommodationService = new AccommodationService();

        // Récupération de tous les hébergements pour lier les chambres
        List<Accommodation> accommodations = accommodationService.getAll();
        if (accommodations.size() < 3) {
            System.out.println("⚠️ Veuillez insérer au moins 3 hébergements dans la base de données avant de tester les rooms.");
            return;
        }

        Accommodation acc1 = accommodations.get(7); // Ex: Hotel
        Accommodation acc2 = accommodations.get(8); // Ex: Guesthouse
        Accommodation acc3 = accommodations.get(6); // Ex: Airbnb

        // Création de rooms avec leurs hébergements respectifs
        Room room1 = new Room(0, acc1, TypeRoom.SINGLE, "Single Room 101", 80.0, true);
        Room room2 = new Room(0, acc2, TypeRoom.DOUBLE, "Double Room 102", 120.0, true);
        Room room3 = new Room(0, acc3, TypeRoom.SUITE, "Suite 201", 250.0, false);

        // Ajout des chambres
        roomService.add(room1);
        roomService.add(room2);
        roomService.add(room3);

        System.out.println("📌 List of rooms after adding:");
        roomService.getAll().forEach(System.out::println);

        // Mettre à jour la première chambre
        List<Room> allRooms = roomService.getAll();
        if (!allRooms.isEmpty()) {
            Room roomToUpdate = allRooms.get(0);
            roomToUpdate.setNameRoom("Updated Single Room 101");
            roomToUpdate.setPrice(90.0);
            roomService.update(roomToUpdate);
        }

        System.out.println("\n📌 List of rooms after update:");
        roomService.getAll().forEach(System.out::println);

        // Suppression des deux dernières rooms
        allRooms = roomService.getAll();
        if (allRooms.size() > 2) {
            roomService.delete(allRooms.get(1));
            roomService.delete(allRooms.get(2));
        }

        System.out.println("\n📌 List of rooms after deletion:");
        roomService.getAll().forEach(System.out::println);
    }
}
