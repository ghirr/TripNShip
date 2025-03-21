package org.esprit.tripnship.Entities;

import java.util.ArrayList;
import java.util.List;

public class Accommodation {
    private int idAccommodation;
    private TypeAccommodation type;
    private String name;
    private String address;
    private int capacity;
    private String note;



    private List<Integer> idRoom = new ArrayList<>();;

    public Accommodation() {
    }

    public Accommodation(int idAccommodation, TypeAccommodation type, String name, String address, int capacity, String note) {
        this.idAccommodation = idAccommodation;
        this.type = type;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.note = note;

    }

    public int getIdAccommodation() {
        return idAccommodation;
    }

    public void setIdAccommodation(int idAccommodation) {
        this.idAccommodation = idAccommodation;
    }

    public List<Integer> getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(List<Integer> idRoom) {
        this.idRoom = idRoom;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


    @Override
    public String toString() {
        return "Accommodation{" +
                "idAccommodation=" + idAccommodation +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", capacity=" + capacity +
                ", note='" + note + '\'' +
                ", rooms=" + idRoom +
                '}';
    }
}
