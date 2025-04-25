package org.Esprit.TripNShip.Tests;

import org.Esprit.TripNShip.Entities.Itinerary;
import org.Esprit.TripNShip.Services.ItineraryService;

public class ItineraryTest {
    public static void main(String[] args) {
        ItineraryService is = new ItineraryService();
        Itinerary itnerary1 = new Itinerary("TUN01", "TAR", "TUN", "ORY", "2:15");
        is.add(itnerary1);
        Itinerary itnerary2 = new Itinerary("TUN02", "TAR", "ORY", "TUN", "2:00");
        itnerary2.setTransporterReference("AIRFRANCE");
        is.update(itnerary2);
    }
}
