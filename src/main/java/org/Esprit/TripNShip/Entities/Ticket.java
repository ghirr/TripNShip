package org.Esprit.TripNShip.Entities;

import java.time.LocalDate;
import java.time.LocalTime;

public class Ticket {
    private int ticketId;
    private String itineraryCode;
    private String userEmail;
    private LocalDate departureDate;
    private LocalDate arrivalDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }


    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    private double price;

    public Ticket(String itineraryCode, String userEmail, LocalDate departureDate, LocalDate arrivalDate, double price) {
        this.itineraryCode = itineraryCode;
        this.userEmail = userEmail;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.price = price;
    }

    public Ticket(int ticketId, String itineraryCode, String userEmail, LocalDate departureDate, LocalDate arrivalDate, double price) {
        this.ticketId = ticketId;
        this.itineraryCode = itineraryCode;
        this.userEmail = userEmail;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.price = price;
    }

    public Ticket(String itineraryCode, String userEmail, LocalDate departureDate, LocalDate arrivalDate) {
        this.itineraryCode = itineraryCode;
        this.userEmail = userEmail;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
    }

    public Ticket(int ticketId, String itineraryCode, String userEmail, LocalDate departureDate, LocalDate arrivalDate, LocalTime departureTime, LocalTime arrivalTime, double price) {
        this.ticketId = ticketId;
        this.itineraryCode = itineraryCode;
        this.userEmail = userEmail;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
    }

    public Ticket(String itineraryCode, String userEmail, LocalDate departureDate, LocalDate arrivalDate, LocalTime departureTime, LocalTime arrivalTime, double price) {
        this.itineraryCode = itineraryCode;
        this.userEmail = userEmail;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
    }

    public Ticket(String itineraryCode, String userEmail, LocalDate departureDate, LocalTime departureTime, LocalDate arrivalDate, LocalTime arrivalTime, double price) {
        this.itineraryCode = itineraryCode;
        this.userEmail = userEmail;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.arrivalDate = arrivalDate;
        this.arrivalTime = arrivalTime;
        this.price = price;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getItineraryCode() {
        return itineraryCode;
    }

    public void setItineraryCode(String itineraryCode) {
        this.itineraryCode = itineraryCode;
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
                ", itineraryCode='" + itineraryCode + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", departureDate=" + departureDate +
                ", arrivalDate=" + arrivalDate +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                ", price=" + price +
                '}';
    }

}
