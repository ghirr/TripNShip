package org.esprit.tripnship.Entities;

import java.time.Duration;

public class Itinerary {
    private int itineraryId;
    private int transportId;
    private String departureLocation;
    private String arrivalLocation;
    private String duration;
    private double price;

    public Itinerary(int itineraryId, int transportId, String departureLocation, String arrivalLocation, String duration, double price) {
        this.itineraryId = itineraryId;
        this.transportId = transportId;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.duration = duration;
        this.price = price;
    }

    public int getItineraryId() {
        return itineraryId;
    }

    public int getTransportID() {
        return transportId;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public String getArrivalLocation() {
        return arrivalLocation;
    }

    public String getDuration() {
        return duration;
    }

    public double getPrice() {
        return price;
    }

    public void setItineraryId(int itineraryId) {
        this.itineraryId = itineraryId;
    }

    public void setTransportID(int transportId) {
        this.transportId = transportId;
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation = departureLocation;
    }

    public void setArrivalLocation(String arrivalLocation) {
        this.arrivalLocation = arrivalLocation;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Itinerary{" +
                "itineraryId=" + itineraryId +
                ", transportID=" + transportId +
                ", departureLocation='" + departureLocation + '\'' +
                ", arrivalLocation='" + arrivalLocation + '\'' +
                ", duration=" + duration +
                ", price=" + price +
                '}';
    }
}
