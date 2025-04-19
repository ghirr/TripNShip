package org.Esprit.TripNShip.Entities;

import java.time.LocalDateTime;

public class CircuitBooking {

    private int idBooking;
    private LocalDateTime bookingDate;
    private StatusBooking statusBooking;
    private User user;
    private TourCircuit tourCircuit;

    public CircuitBooking() {

    }
    public CircuitBooking(LocalDateTime bookingDate, StatusBooking statusBooking, User user, TourCircuit tourCircuit) {
        this.bookingDate = bookingDate;
        this.statusBooking = statusBooking;
        this.user = user;
        this.tourCircuit = tourCircuit;
    }

    public CircuitBooking(int idBooking, LocalDateTime bookingDate, StatusBooking statusBooking, User user, TourCircuit tourCircuit) {
        this.idBooking = idBooking;
        this.bookingDate = bookingDate;
        this.statusBooking = statusBooking;
        this.user = user;
        this.tourCircuit = tourCircuit;
    }

    public int getIdBooking() {
        return idBooking;
    }

    public void setIdBooking(int idBooking) {
        this.idBooking = idBooking;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public StatusBooking getStatusBooking() {
        return statusBooking;
    }

    public void setStatusBooking(StatusBooking statusBooking) {
        this.statusBooking = statusBooking;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TourCircuit getTourCircuit() {
        return tourCircuit;
    }

    public void setTourCircuit(TourCircuit tourCircuit) {
        this.tourCircuit = tourCircuit;
    }

    @Override
    public String toString() {
        return "CircuitBooking{" +
                "idBooking=" + idBooking +
                ", bookingDate=" + bookingDate +
                ", statusBooking=" + statusBooking +
                ", user=" + user.getIdUser() +
                ", tourCircuit=" + tourCircuit.getIdCircuit() +
                '}';
    }
}
