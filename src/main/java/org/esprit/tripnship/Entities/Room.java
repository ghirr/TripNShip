package org.esprit.tripnship.Entities;

public class Room {
    private int idRoom;
    private TypeRoom type;
    private double price;
    private boolean availability;

    private int idAccommodation;
    public Room() {
    }

    public Room(int idRoom, TypeRoom type, double price, boolean availability, int idAccommodation) {
        this.idRoom = idRoom;
        this.type = type;
        this.price = price;
        this.availability = availability;
        this.idAccommodation = idAccommodation;
    }

    public int getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(int idRoom) {
        this.idRoom = idRoom;
    }

    public TypeRoom getType() {
        return type;
    }

    public void setType(TypeRoom type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getIdAccommodation() {
        return idAccommodation;
    }

    public void setIdAccommodation(int idAccommodation) {
        this.idAccommodation = idAccommodation;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    @Override
    public String toString() {
        return "Room{" +
                "idRoom=" + idRoom +
                ", type=" + type +
                ", price=" + price +
                ", availability=" + availability +
                ", idAccommodation=" + idAccommodation +
                '}';
    }
}
