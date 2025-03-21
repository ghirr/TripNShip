package org.esprit.tripnship.Entities;

import java.util.Date;

public class AccommodationReservation {
    private int idReservation;
    private int idUser;
    private int roomId;
    private Date startDate;
    private Date endDate;
    private ReservationStatus status;

    public AccommodationReservation() {
    }

    public AccommodationReservation(int idReservation, int idUser, int roomId, Date startDate, Date endDate, ReservationStatus status) {
        this.idReservation = idReservation;
        this.idUser = idUser;
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AccommodationReservation{" +
                "idReservation=" + idReservation +
                ", idUser=" + idUser +
                ", roomId=" + roomId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }
}
