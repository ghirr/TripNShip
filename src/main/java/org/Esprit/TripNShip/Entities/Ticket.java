package org.Esprit.TripNShip.Entities;

import java.time.LocalDate;

public class Ticket {
    private int ticketId;
    private String itineraryId;
    private String userEmail;
    private LocalDate departureDate;
    private LocalDate arrivalDate;
    private double price;

    public Ticket(String itineraryId, String userEmail, LocalDate departureDate, LocalDate arrivalDate, double price) {
        this.itineraryId = itineraryId;
        this.userEmail = userEmail;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.price = price;
    }

    public Ticket(int ticketId, String itineraryId, String userEmail, LocalDate departureDate, LocalDate arrivalDate, double price) {
        this.ticketId = ticketId;
        this.itineraryId = itineraryId;
        this.userEmail = userEmail;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.price = price;
    }

    public Ticket(String itineraryId, String userEmail, LocalDate departureDate, LocalDate arrivalDate) {
        this.itineraryId = itineraryId;
        this.userEmail = userEmail;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getItineraryId() {
        return itineraryId;
    }

    public void setItineraryId(String itineraryId) {
        this.itineraryId = itineraryId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId=" + ticketId +
                ", itineraryId='" + itineraryId + '\'' +
                ", userEmail=" + userEmail +
                ", departureDate=" + departureDate +
                ", arrivalDate=" + arrivalDate +
                ", price=" + price +
                '}';
    }
}
