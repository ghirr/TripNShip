package org.Esprit.TripNShip.Entities;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {

    private int idVehicle;
    private String brand;
    private String model;
    private String licensePlate;
    private float dailyPrice;
    private boolean availability;
    private Type type;


    public Vehicle(int idVehicle, String brand, String model, String licensePlate, float dailyPrice, boolean availability, Type type) {
        this.idVehicle = idVehicle;
        this.brand = brand;
        this.model = model;
        this.licensePlate = licensePlate;
        this.dailyPrice = dailyPrice;
        this.availability = availability;
        this.type = type;

    }

    public Vehicle(String brand, String model, String licensePlate, float dailyPrice, boolean availability, Type type) {
        this.brand = brand;
        this.model = model;
        this.licensePlate = licensePlate;
        this.dailyPrice = dailyPrice;
        this.availability = availability;
        this.type = type;

    }

    public Vehicle() {
        
    }

    public Vehicle(int idVehicle) {
    }



    public int getIdVehicle() {
        return idVehicle;
    }

    public void setIdVehicle(int idVehicle) {
        this.idVehicle = idVehicle;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public float getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(float dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }



    @Override
    public String toString() {
        return "Vehicle{" +
                "idVehicle=" + idVehicle +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", dailyPrice=" + dailyPrice +
                ", availability=" + availability +
                ", type=" + type +
                '}';
    }
}
