package org.Esprit.TripNShip.Entities;


import java.time.LocalDateTime;


public class CircuitBooking {

    private int idBooking;
    private LocalDateTime bookingDate;
    private StatusBooking statusBooking;
    private int idCircuit;

    public CircuitBooking(LocalDateTime bookingDate, StatusBooking statusBooking, int idCircuit) {
        this.bookingDate = bookingDate;
        this.statusBooking = statusBooking;
        this.idCircuit = idCircuit;
    }

    public CircuitBooking(int idBooking, LocalDateTime bookingDate, StatusBooking statusBooking, int idCircuit) {
        this.idBooking = idBooking;
        this.bookingDate = bookingDate;
        this.statusBooking = statusBooking;
        this.idCircuit = idCircuit;
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

    public int getIdCircuit() {
        return idCircuit;
    }

    public void setIdCircuit(int idCircuit) {
        this.idCircuit = idCircuit;
    }

    @Override
    public String toString() {
        return "CircuitBooking{" +
                "idBooking=" + idBooking +
                ", bookingDate=" + bookingDate +
                ", statusBooking=" + statusBooking +
                ", idCircuit=" + idCircuit +
                '}';
    }
}
