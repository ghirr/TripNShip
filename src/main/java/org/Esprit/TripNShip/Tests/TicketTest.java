package org.Esprit.TripNShip.Tests;

import org.Esprit.TripNShip.Entities.Ticket;
import org.Esprit.TripNShip.Services.TicketService;
import java.time.LocalDate;

public class TicketTest {
    public static void main(String[] args) {
        TicketService ts = new TicketService();
        Ticket ticket1 = new Ticket("GAB55","fnjl@user.com",LocalDate.of(2025,03,23),LocalDate.of(2025,04,22),120.22);
        Ticket ticket2= new Ticket("TAR187","user1@user.net",LocalDate.of(2025,04,22),LocalDate.of(2025,05,02),4005.33);
        ts.add(ticket1);
    }
}
