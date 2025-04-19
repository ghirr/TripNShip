package org.Esprit.TripNShip.Entities;

public class Itinerary {
    private String itineraryId;
    private String transportId;
    private String departureLocation;
    private String arrivalLocation;
    private String duration;

    public Itinerary(String itineraryId, String transportId, String departureLocation, String arrivalLocation, String duration) {
        this.itineraryId = itineraryId;
        this.transportId = transportId;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.duration = duration;
    }

    public String getItineraryId() {
        return itineraryId;
    }

    public String getTransportId() {
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

    public void setItineraryId(String itineraryId) {
        this.itineraryId = itineraryId;
    }

    public void setTransportId(String transportId) {
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


    @Override
    public String toString() {
        return "Itinerary{" +
                "itineraryId=" + itineraryId +
                ", transportID=" + transportId +
                ", departureLocation='" + departureLocation + '\'' +
                ", arrivalLocation='" + arrivalLocation + '\'' +
                ", duration=" + duration +
                '}';
    }
}
