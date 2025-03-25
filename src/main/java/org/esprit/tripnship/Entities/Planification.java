package org.esprit.tripnship.Entities;

import java.time.LocalDate;

public class Planification {
    private int planificationId;
    private int itineraryId;
    private LocalDate departureDate;
    private LocalDate arrivalDate;

    public Planification(int planificationId, int itineraryId, LocalDate departureDate, LocalDate arrivalDate) {
        this.planificationId = planificationId;
        this.itineraryId = itineraryId;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
    }

    public int getPlanificationId() {
        return planificationId;
    }

    public void setPlanificationId(int planificationId) {
        this.planificationId = planificationId;
    }

    public int getItineraryId() {
        return itineraryId;
    }

    public void setItineraryId(int itineraryId) {
        this.itineraryId = itineraryId;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    @Override
    public String toString() {
        return "Planification{" +
                "planificationId=" + planificationId +
                ", itineraryId=" + itineraryId +
                ", departureDate=" + departureDate +
                ", arrivalDate=" + arrivalDate +
                '}';
    }
}
