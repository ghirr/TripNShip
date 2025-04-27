package org.Esprit.TripNShip.Entities;

public class Itinerary {
    private int itineraryId;
    private String itineraryCode;
    private String transporterReference;
    private String departureLocation;
    private String arrivalLocation;
    private String duration;

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
                "itineraryId=" + itineraryId+
                "itineraryCode=" + itineraryCode +
                ", transporterReference=" + transporterReference +
                ", departureLocation='" + departureLocation + '\'' +
                ", arrivalLocation='" + arrivalLocation + '\'' +
                ", duration=" + duration +
                '}';
    }

    public int getItineraryId() {
        return itineraryId;
    }

    public void setItineraryId(int itineraryId) {
        this.itineraryId = itineraryId;
    }
}
