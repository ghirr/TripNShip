package org.Esprit.TripNShip.Entities;

public class Room {
    private int idRoom;
    private Accommodation accommodation; // Foreign key to Accommodation
    private TypeRoom type;
    private String nameRoom;
    private double price;
    private boolean availability;

    public Room() {
    }

    public Room(int idRoom, Accommodation accommodation, TypeRoom type, String nameRoom, double price, boolean availability) {
        this.idRoom = idRoom;
        this.accommodation = accommodation;
        this.type = type;
        this.nameRoom = nameRoom;
        this.price = price;
        this.availability = availability;
    }

    public Room(int i, String s, TypeRoom typeRoom) {
    }

    public int getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(int idRoom) {
        this.idRoom = idRoom;
    }

    public Accommodation getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
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

    // Getter pour le type d’hébergement
    public TypeAccommodation getTypeAccommodation() {
        if (accommodation != null) {
            return accommodation.getType();
        }
        return null;
    }

    public String getAccommodationName() {
        if (accommodation != null) {
            return accommodation.getName(); // Récupérer le nom de l'hébergement depuis l'entité Accommodation
        }
        return null;
    }

    @Override
    public String toString() {
        return "Room{" +
                "idRoom=" + idRoom +
                ", accommodation=" + accommodation +
                ", type=" + type +
                ", nameRoom='" + nameRoom + '\'' +
                ", price=" + price +
                ", availability=" + availability +
                '}';
    }
}
