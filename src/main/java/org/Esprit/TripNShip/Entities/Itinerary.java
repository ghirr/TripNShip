package org.Esprit.TripNShip.Entities;

public class Itinerary {
    private int itineraryId;
    private String itineraryCode;
    private String transporterReference;
    private String departureLocation;
    private String arrivalLocation;
    private String duration;
    private double price;

    public Itinerary(int itineraryId, String itineraryCode, String transporterReference, String departureLocation, String arrivalLocation, String duration, double price) {
        this.itineraryId = itineraryId;
        this.itineraryCode = itineraryCode;
        this.transporterReference = transporterReference;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.duration = duration;
        this.price = price;
    }

    public Itinerary(String itineraryCode, String transporterReference, String departureLocation, String arrivalLocation, String duration) {
        this.itineraryCode = itineraryCode;
        this.transporterReference = transporterReference;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.duration = duration;
    }

    public Itinerary(int itineraryId, String itineraryCode, String transporterReference, String departureLocation, String arrivalLocation, String duration) {
        this.itineraryId = itineraryId;
        this.itineraryCode = itineraryCode;
        this.transporterReference = transporterReference;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.duration = duration;
    }

    public Itinerary(String itineraryCode, String transporterReference, String departureLocation, String arrivalLocation, String duration, double price) {
        this.itineraryCode = itineraryCode;
        this.transporterReference = transporterReference;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.duration = duration;
        this.price = price;
    }

    public String getItineraryCode() {
        return itineraryCode;
    }

    public String getTransporterReference() {
        return transporterReference;
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

    public void setPrice(double price) {
        this.price = price;
    }

    public void setItineraryCode(String itineraryCode) {
        this.itineraryCode = itineraryCode;
    }

    public void setTransporterReference(String transporterReference) {
        this.transporterReference = transporterReference;
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


    @Override
    public String toString() {
        return "Itinerary{" +
                "itineraryId=" + itineraryId +
                ", itineraryCode='" + itineraryCode + '\'' +
                ", transporterReference='" + transporterReference + '\'' +
                ", departureLocation='" + departureLocation + '\'' +
                ", arrivalLocation='" + arrivalLocation + '\'' +
                ", duration='" + duration + '\'' +
                ", price=" + price +
                '}';
    }

    public int getItineraryId() {
        return itineraryId;
    }

}
