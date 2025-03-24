package org.esprit.tripnship.Entities;

import java.util.Date;

public class Planification {
    private int planificationId;
    private int itineraryID;
    private Date departureDate;
    private Date arrivalDate;

    public Planification(int planificationId, int itineraryID, Date departureDate, Date arrivalDate) {
        this.planificationId = planificationId;
        this.itineraryID = itineraryID;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
    }

    public int getPlanificationId() {
        return planificationId;
    }

    public void setPlanificationId(int planificationId) {
        this.planificationId = planificationId;
    }

    public int getItineraryID() {
        return itineraryID;
    }

    public void setItineraryID(int itineraryID) {
        this.itineraryID = itineraryID;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    @Override
    public String toString() {
        return "Planification{" +
                "planificationId=" + planificationId +
                ", itineraryID=" + itineraryID +
                ", departureDate=" + departureDate +
                ", arrivalDate=" + arrivalDate +
                '}';
    }
}
