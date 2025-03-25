package org.esprit.tripnship.Tests;

import org.esprit.tripnship.Entities.Transport;
import org.esprit.tripnship.Entities.TypeTransport;
import org.esprit.tripnship.Services.TransportService;

public class TestTransport {
    public static void main(String[] args) {
        Transport transport1 = new Transport(114, TypeTransport.BUS,"SNTRI");
        Transport transport2 = new Transport(236,TypeTransport.FERRY,"CTN",71516987);
        TransportService ts = new TransportService();
        ts.add(transport1);
        ts.add(transport2);
        transport2.setTransportation(TypeTransport.PLANE);
        ts.update(transport2);
        transport2.setTransportation(TypeTransport.FERRY);
        ts.update(transport2);
        Transport transport3 = new Transport(454,TypeTransport.BUS,"traveltodo");
        ts.add(transport3);
        ts.delete(transport3);
        ts.getAll();
    }
}
