package org.esprit.tripnship.Entities;

import java.time.Duration;

public class Itinerary {
    private int itineraryId;
    private int transportID;
    private String departureLocation;
    private String arrivalLocation;
    private Duration duration;
    private double price;

    public Itinerary(int itiniraryId, int transportID, String departureLocation, String arrivalLocation, Duration duration, double price) {
        this.itineraryId = itiniraryId;
        this.transportID = transportID;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.duration = duration;
        this.price = price;
    }

    public int getItineraryId() {
        return itineraryId;
    }

    public int getTransportID() {
        return transportID;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public String getArrivalLocation() {
        return arrivalLocation;
    }

    public Duration getDuration() {
        return duration;
    }

    public double getPrice() {
        return price;
    }

    public void setItineraryId(int itineraryId) {
        this.itineraryId = itineraryId;
    }

    public void setTransportID(int transportID) {
        this.transportID = transportID;
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation = departureLocation;
    }

    public void setArrivalLocation(String arrivalLocation) {
        this.arrivalLocation = arrivalLocation;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Itinerary{" +
                "itineraryId=" + itineraryId +
                ", transportID=" + transportID +
                ", departureLocation='" + departureLocation + '\'' +
                ", arrivalLocation='" + arrivalLocation + '\'' +
                ", duration=" + duration +
                ", price=" + price +
                '}';
    }
}
