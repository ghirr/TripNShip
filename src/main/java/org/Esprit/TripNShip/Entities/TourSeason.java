package org.Esprit.TripNShip.Entities;

import java.time.LocalDateTime;

public class TourSeason {
    private int idSeason;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Float multiplier;

    public TourSeason(LocalDateTime startDate, LocalDateTime endDate, Float multiplier) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.multiplier = multiplier;
    }

    public TourSeason(int idSeason, LocalDateTime startDate, LocalDateTime endDate, Float multiplier) {
        this.idSeason = idSeason;
        this.startDate = startDate;
        this.endDate = endDate;
        this.multiplier = multiplier;
    }

    public int getIdSeason() {
        return idSeason;
    }

    public void setIdSeason(int idSeason) {
        this.idSeason = idSeason;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Float multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public String toString() {
        return "TourSeason{" +
                "idSeason=" + idSeason +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", multiplier=" + multiplier +
                '}';
    }
}
