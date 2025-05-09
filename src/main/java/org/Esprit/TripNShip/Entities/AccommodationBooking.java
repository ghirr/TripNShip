package org.Esprit.TripNShip.Entities;

import java.util.Date;

public class AccommodationBooking {

    private int idBooking;
    private User user;
    private Room room;
    private Date startDate;
    private Date endDate;
    private BookingStatus status;


    public AccommodationBooking(int idBooking, User user, Room room, Date startDate, Date endDate, BookingStatus status) {
        this.idBooking = idBooking;
        this.user = user;
        this.room = room;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

//    public AccommodationBooking(int idBooking, String emailUser, Room room, java.sql.Date startDate, java.sql.Date endDate, BookingStatus status) {
//        this.idBooking = idBooking;
//        this.user.setEmail(emailUser);
//        this.room = room;
//        this.startDate = startDate;
//        this.endDate = endDate;
//        this.status = status;
//    }

    public int getIdBooking() {
        return idBooking;
    }

    public void setIdBooking(int idBooking) {
        this.idBooking = idBooking;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
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
        return "AccommodationBooking{" +
                "idBooking=" + idBooking +
                ", idUser=" + user +
                ", room=" + room +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }


}
