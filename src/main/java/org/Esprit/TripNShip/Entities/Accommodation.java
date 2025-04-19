package org.Esprit.TripNShip.Entities;

import java.util.ArrayList;
import java.util.List;

public class Accommodation {
    private int idAccommodation;
    private TypeAccommodation type;
    private String name;
    private String address;
    private float priceNight;
    private int capacity;





    private List<Integer> idRoom = new ArrayList<>();

    public Accommodation(int idAccommodation, TypeAccommodation type, String name, String address, float priceNight, int capacity) {
        this.idAccommodation = idAccommodation;
        this.type = type;
        this.name = name;
        this.address = address;
        this.priceNight = priceNight;
        this.capacity = capacity;
    }

    public Accommodation() {

    }

    public int getIdAccommodation() {
        return idAccommodation;
    }

    public void setIdAccommodation(int idAccommodation) {
        this.idAccommodation = idAccommodation;
    }

    public TypeAccommodation getType() {
        return type;
    }

    public void setTypeAccommodation(TypeAccommodation typeAccommodation) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getPriceNight() {
        return priceNight;
    }

    public void setPriceNight(float priceNight) {
        this.priceNight = priceNight;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<Integer> getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(List<Integer> idRoom) {
        this.idRoom = idRoom;
    }

    @Override
    public String toString() {
        return "Accommodation{" +
                "idAccommodation=" + idAccommodation +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", priceNight=" + priceNight +
                ", capacity=" + capacity +
                ", idRoom=" + idRoom +
                '}';
    }
}

