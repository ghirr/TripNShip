package org.Esprit.TripNShip.Entities;

import java.util.ArrayList;
import java.util.List;

public class Accommodation {
    private int idAccommodation;
    private TypeAccommodation type;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private int capacity;
    private String photosAccommodation;
    private List<Integer> idRoom = new ArrayList<>();

    public Accommodation(int idAccommodation, TypeAccommodation type, String name, String address,  int capacity, String photosAccommodation) {
        this.idAccommodation = idAccommodation;
        this.type = type;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.photosAccommodation = photosAccommodation;
    }

    public Accommodation(int idAccommodation, TypeAccommodation type, String name, String address, double latitude, double longitude, int capacity, String photosAccommodation) {
        this.idAccommodation = idAccommodation;
        this.type = type;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacity = capacity;
        this.photosAccommodation = photosAccommodation;
    }

    public Accommodation(int idAccommodation, TypeAccommodation type, String name, double latitude, double longitude, int capacity, String photosAccommodation) {
        this.idAccommodation = idAccommodation;
        this.type = type;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacity = capacity;
        this.photosAccommodation = photosAccommodation;
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

    public void setType(TypeAccommodation type) {
        this.type = type;
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

    public String getPhotosAccommodation() {
        return photosAccommodation;
    }

    public void setPhotosAccommodation(String photosAccommodation) {
        this.photosAccommodation = photosAccommodation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Accommodation{" +
                "idAccommodation=" + idAccommodation +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", capacity=" + capacity +
                ", idRoom=" + idRoom +
                ", photosAccommodation=" + photosAccommodation +
                '}';
    }
}
