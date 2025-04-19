package org.Esprit.TripNShip.Tests;

import org.Esprit.TripNShip.Entities.Ticket;
import org.Esprit.TripNShip.Services.TicketService;

import java.time.LocalDate;

public class TicketTest {
    public static void main(String[] args) {
        TicketService ts = new TicketService();
        Ticket ticket1 = new Ticket(254555,"AF01",12547, LocalDate.of(2025,05,12),LocalDate.of(2025,2,1),255.600);
        ts.add(ticket1);
    }
}
