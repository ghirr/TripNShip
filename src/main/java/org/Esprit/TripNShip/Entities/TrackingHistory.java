package org.Esprit.TripNShip.Entities;

import org.Esprit.TripNShip.Entities.Expedition;
import org.Esprit.TripNShip.Entities.PackageStatus;

import java.time.LocalDateTime;

public class TrackingHistory {

    private int id;
    private Expedition expedition;
    private PackageStatus status;
    private String locationNote;
    private LocalDateTime timestamp;
    private Transporter updatedBy;

    public TrackingHistory(int id, Expedition expedition, PackageStatus status, String locationNote, LocalDateTime timestamp, Transporter updatedBy) {
        this.id = id;
        this.expedition = expedition;
        this.status = status;
        this.locationNote = locationNote;
        this.timestamp = timestamp;
        this.updatedBy = updatedBy;
    }

    public TrackingHistory(Expedition expedition, PackageStatus status, String locationNote, LocalDateTime timestamp, Transporter updatedBy) {
        this.expedition = expedition;
        this.status = status;
        this.locationNote = locationNote;
        this.timestamp = timestamp;
        this.updatedBy = updatedBy;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Expedition getExpedition() {
        return expedition;
    }

    public void setExpedition(Expedition expedition) {
        this.expedition = expedition;
    }

    public PackageStatus getStatus() {
        return status;
    }

    public void setStatus(PackageStatus status) {
        this.status = status;
    }

    public String getLocationNote() {
        return locationNote;
    }

    public void setLocationNote(String locationNote) {
        this.locationNote = locationNote;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Transporter getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Transporter updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return "TrackingHistory{" +
                "id=" + id +
                ", expedition=" + expedition.getExpeditionId() +
                ", status=" + status +
                ", locationNote='" + locationNote + '\'' +
                ", timestamp=" + timestamp +
                ", updatedBy=" + updatedBy.getIdUser() +
                '}';
    }
}
