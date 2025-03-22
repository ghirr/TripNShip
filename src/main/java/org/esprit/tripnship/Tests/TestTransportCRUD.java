package org.esprit.tripnship.Tests;

import org.esprit.tripnship.Entities.Status;
import org.esprit.tripnship.Entities.Ticket;
import org.esprit.tripnship.Services.TicketService;

public class TestTransportCRUD {
    public static void main(String[] args) {
        Ticket ticket1 = new Ticket(1,12,1013, Status.CONFIRMED);
        Ticket ticket2 = new Ticket(2,24,18, Status.PENDING);
        TicketService ts = new TicketService();
        ts.add(ticket1);
        ts.add(ticket2);
        ticket2.setStatus(Status.CANCELED);
        ts.update(ticket2);
       // ts.delete(ticket1);
    }
}
