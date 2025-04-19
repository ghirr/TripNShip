package org.Esprit.TripNShip.Tests;

import org.Esprit.TripNShip.Entities.Transport;
import org.Esprit.TripNShip.Entities.TransportType;
import org.Esprit.TripNShip.Services.TransportService;

public class TransportTest {
    public static void main(String[] args) {
        TransportService ts = new TransportService();
        Transport transport1 = new Transport("TAR", TransportType.PLANE,"Tunisair",71777897);
        Transport transport2= new Transport("AF",TransportType.PLANE,"AIR FRANCE");
        Transport transport3= new Transport("NTC",TransportType.FERRY,"CTN",70255655);
        ts.add(transport1);
        ts.add(transport3);
        ts.add(transport2);
        transport3.setCompanyName("CTN");
        ts.update(transport3);
        System.out.println(ts.getAll());
    }
}
