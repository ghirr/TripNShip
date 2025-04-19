package org.Esprit.TripNShip.Tests;

import org.Esprit.TripNShip.Entities.Room;
import org.Esprit.TripNShip.Entities.TypeRoom;

import java.util.ArrayList;
import java.util.List;

public class TestRoomCRUD {

    // Sample data storage
    private static final List<Room> rooms = new ArrayList<>();


    public static void main(String[] args) {
        // Test CRUD for Room
        Room room1 = new Room(3, 7, TypeRoom.SINGLE, "Single101", true, 100.0);
        Room room2 = new Room(4, 8, TypeRoom.DOUBLE, "Double201", true, 150.0);
        Room room3 = new Room(5, 9, TypeRoom.SUITE, "Suite301", true, 200.0);

        // Add rooms
        addRoom(room1);
        addRoom(room2);
        addRoom(room3);

        System.out.println("📌 List of rooms after adding:");
        rooms.forEach(System.out::println);

        // Update room1
        room1.setPrice(120.0);
        room1.setAvailability(false);
        updateRoom(room1);

        System.out.println("\n📌 List of rooms after update:");
        rooms.forEach(System.out::println);

        // Delete room2
        deleteRoom(room2);

        System.out.println("\n📌 List of rooms after deletion:");
        rooms.forEach(System.out::println);
    }

    // Add a room
    public static void addRoom(Room room) {
        room.setIdRoom(rooms.size() + 1);  // Set unique ID (based on size of list)
        rooms.add(room);
    }

    // Update a room
    public static void updateRoom(Room room) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getIdRoom() == room.getIdRoom()) {
                rooms.set(i, room);  // Replace room with updated room
                return;
            }
        }
    }

    // Delete a room
    public static void deleteRoom(Room room) {
        rooms.removeIf(r -> r.getIdRoom() == room.getIdRoom());  // Remove room by ID
    }
}

