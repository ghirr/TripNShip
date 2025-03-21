package org.esprit.tripnship.Tests;

import org.esprit.tripnship.Entities.Room;
import org.esprit.tripnship.Entities.TypeRoom;

import java.util.ArrayList;
import java.util.List;

public class TestRoomCRUD {

    // Sample data storage
    private static final List<Room> rooms = new ArrayList<>();


    public static void main(String[] args) {
        // Test CRUD for Room
        Room room1 = new Room(0, TypeRoom.SINGLE, 100.0, true, 1); // Room 1
        Room room2 = new Room(0, TypeRoom.DOUBLE, 150.0, true, 1); // Room 2
        Room room3 = new Room(0, TypeRoom.SUITE, 200.0, true, 2); // Room 3

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

