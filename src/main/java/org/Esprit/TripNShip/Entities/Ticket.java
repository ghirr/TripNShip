package org.Esprit.TripNShip.Entities;

import org.Esprit.TripNShip.Services.ItineraryService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Ticket {
    private int ticketId;
    private String itineraryCode;
    private String userEmail;
    private LocalDate departureDate;
    private LocalDate arrivalDate;

    public Ticket(int ticketId, String itineraryCode, String userEmail, LocalDate departureDate, LocalDate arrivalDate) {
        this.ticketId = ticketId;
        this.itineraryCode = itineraryCode;
        this.userEmail = userEmail;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
    }

    public Ticket(String itineraryCode, String userEmail, LocalDate departureDate) {
        this.itineraryCode = itineraryCode;
        this.userEmail = userEmail;
        this.departureDate = departureDate;
        this.arrivalDate = updateArrivalDate();
    }

    public Ticket(String itineraryCode, String userEmail, LocalDate departureDate, LocalDate arrivalDate) {
        this.itineraryCode = itineraryCode;
        this.userEmail = userEmail;
        this.departureDate = departureDate;
        this.arrivalDate = updateArrivalDate();
    }

    public Ticket(int ticketId, String itineraryCode, String userEmail, LocalDate departureDate) {
        this.ticketId = ticketId;
        this.itineraryCode = itineraryCode;
        this.userEmail = userEmail;
        this.departureDate = departureDate;
        this.arrivalDate = updateArrivalDate();
    }


    public Ticket(int ticketId, String itineraryCode, String userEmail, LocalDate departureDate, LocalTime departureTime, LocalTime arrivalTime) {
        this.ticketId = ticketId;
        this.itineraryCode = itineraryCode;
        this.userEmail = userEmail;
        this.departureDate = departureDate;
        this.arrivalDate = updateArrivalDate();

    }

    public Ticket(String itineraryCode, String userEmail, LocalDate departureDate, LocalDate arrivalDate, LocalTime departureTime, LocalTime arrivalTime) {
        this.itineraryCode = itineraryCode;
        this.userEmail = userEmail;
        this.departureDate = departureDate;
        this.arrivalDate = updateArrivalDate();

    }


    public int getTicketId() {
        return ticketId;
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


    private LocalDate updateArrivalDate() {
        ItineraryService is = new ItineraryService();
        Itinerary itinerary = is.getItineraryByCode(itineraryCode);
        if (itinerary != null) {
            return calculateArrivalDate(departureDate, itinerary.getDepartureTime(), itinerary.getDuration());
        } else {
            return null;
        }
    }


    public static LocalDate calculateArrivalDate(LocalDate departureDate, LocalTime departureTime, String duration) {
        if (departureDate == null || departureTime == null || duration == null || !duration.matches("\\d{1,2}:\\d{2}")) {
            return null;
        }

        String[] parts = duration.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        Duration dur = Duration.ofHours(hours).plusMinutes(minutes);

        // Crée un LocalDateTime combiné
        LocalDateTime departureDateTime = LocalDateTime.of(departureDate, departureTime);

        // Calcule l'heure d'arrivée
        LocalDateTime arrivalDateTime = departureDateTime.plus(dur);

        return arrivalDateTime.toLocalDate(); // extrait la date seulement
    }



    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId=" + ticketId +
                ", itineraryCode='" + itineraryCode + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", departureDate=" + departureDate +
                ", arrivalDate=" + arrivalDate +

                '}';
    }

}
