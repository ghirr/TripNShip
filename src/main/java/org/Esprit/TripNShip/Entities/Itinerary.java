package org.Esprit.TripNShip.Entities;

import java.time.LocalTime;

public class Itinerary {
    private int itineraryId;
    private String itineraryCode;
    private String transporterReference;
    private String departureLocation;
    private String arrivalLocation;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String duration;
    private double price;

    public Itinerary(String itineraryCode, String transporterReference, String departureLocation, LocalTime departureTime , String arrivalLocation,LocalTime arrivalTime, String duration, double price) {
        this.itineraryCode = itineraryCode;
        this.transporterReference = transporterReference;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.price = price;
        updateArrivalTime();
    }

    public Itinerary(String itineraryCode, String transporterReference, String departureLocation, LocalTime departureTime, String arrivalLocation, String duration, double price) {
        this.itineraryCode = itineraryCode;
        this.transporterReference = transporterReference;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.departureTime = departureTime;
        this.duration = duration;
        this.price = price;
        updateArrivalTime();

    }

    public Itinerary(String itineraryCode, String transporterReference, String departureLocation, String arrivalLocation, LocalTime departureTime, LocalTime arrivalTime, String duration) {
        this.itineraryCode = itineraryCode;
        this.transporterReference = transporterReference;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        updateArrivalTime();

    }

    public Itinerary(String itineraryCode, String transporterReference, String departureLocation, String arrivalLocation, LocalTime departureTime, LocalTime arrivalTime) {
        this.itineraryCode = itineraryCode;
        this.transporterReference = transporterReference;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        updateArrivalTime();

    }

    public Itinerary(String itineraryCode, String transporterReference, String departureLocation, String arrivalLocation, LocalTime departureTime, String duration) {
        this.itineraryCode = itineraryCode;
        this.transporterReference = transporterReference;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.departureTime = departureTime;
        this.duration = duration;
        updateArrivalTime();

    }

    public String getItineraryCode() {
        return itineraryCode;
    }

    public void setItineraryCode(String itineraryCode) {
        this.itineraryCode = itineraryCode;
    }

    public String getTransporterReference() {
        return transporterReference;
    }

    public void setTransporterReference(String transporterReference) {
        this.transporterReference = transporterReference;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation = departureLocation;
    }

    public String getArrivalLocation() {
        return arrivalLocation;
    }

    public void setArrivalLocation(String arrivalLocation) {
        this.arrivalLocation = arrivalLocation;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
        updateArrivalTime();
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
        updateArrivalTime();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }


    public int getItineraryId() {
        return itineraryId;
    }

    @Override
    public String toString() {
        return "Itinerary{" +
                "itineraryId=" + itineraryId +
                ", itineraryCode='" + itineraryCode + '\'' +
                ", transporterReference='" + transporterReference + '\'' +
                ", departureLocation='" + departureLocation + '\'' +
                ", arrivalLocation='" + arrivalLocation + '\'' +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                ", duration='" + duration + '\'' +
                ", price=" + price +
                '}';
    }

    private void updateArrivalTime() {
        if (departureTime != null && duration != null && duration.matches("\\d{1,2}:\\d{2}")) {
            String[] parts = duration.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            this.arrivalTime = departureTime.plusHours(hours).plusMinutes(minutes);
        } else {
            this.arrivalTime = null; // pas de calcul possible
        }
    }
}
