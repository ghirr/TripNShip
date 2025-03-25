package org.esprit.tripnship.Tests;

import org.esprit.tripnship.Entities.Itinerary;
import org.esprit.tripnship.Services.ItineraryService;

public class TestItinerary {
    public static void main(String[] args) {
        Itinerary itinerary1 = new Itinerary(1850, 2210, "Tunis", "Gabes", "1:30",120);
        Itinerary itinerary2 = new Itinerary(112,221,"tunis","Jendouba","0:45",64.5);
        ItineraryService is = new ItineraryService();
        //is.add(itinerary1);
        //is.add(itinerary2);
        //itinerary1.setDuration("1:15");
        is.getAll();
        //is.update(itinerary1);
        //is.delete(itinerary1);

    }
}
