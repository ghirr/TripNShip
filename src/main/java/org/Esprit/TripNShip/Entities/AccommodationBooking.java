package org.Esprit.TripNShip.Entities;

import java.util.Date;

public class AccommodationBooking {
    private int idBooking;
    private int idUser;
    private int roomId;
    private Date startDate;
    private Date endDate;
    private BookingStatus status;

    public AccommodationBooking() {
    }

    public AccommodationBooking(int idBooking, int idUser, int roomId, Date startDate, Date endDate, BookingStatus status) {
        this.idBooking = idBooking;
        this.idUser = idUser;
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getIdReservation() {
        return idBooking;
    }

    public void setIdReservation(int idReservation) {
        this.idBooking = idReservation;
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

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AccommodationReservation{" +
                "idReservation=" + idBooking +
                ", idUser=" + idUser +
                ", roomId=" + roomId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }
}

