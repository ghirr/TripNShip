package org.Esprit.TripNShip.Entities;

public class Room {
    private int idRoom;
    private int idAccommodation;
    private TypeRoom type;
    private String nameRoom;
    private double price;
    private boolean availability;

    public Room(int idRoom, int idAccommodation, TypeRoom type, String nameRoom, boolean availability, double price) {
        this.idRoom = idRoom;
        this.idAccommodation = idAccommodation;
        this.type = type;
        this.nameRoom = nameRoom;
        this.availability = availability;
        this.price = price;
    }

    public int getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(int idRoom) {
        this.idRoom = idRoom;
    }

    public int getIdAccommodation() {
        return idAccommodation;
    }

    public void setIdAccommodation(int idAccommodation) {
        this.idAccommodation = idAccommodation;
    }

    public TypeRoom getType() {
        return type;
    }

    public void setType(TypeRoom type) {
        this.type = type;
    }

    public String getNameRoom() {
        return nameRoom;
    }

    public void setNameRoom(String nameRoom) {
        this.nameRoom = nameRoom;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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
                ", idAccommodation=" + idAccommodation +
                ", type=" + type +
                ", nameRoom='" + nameRoom + '\'' +
                ", price=" + price +
                ", availability=" + availability +
                '}';
    }

    public TypeAccommodation getTypeAccommodation() {

        return null;
    }
}
