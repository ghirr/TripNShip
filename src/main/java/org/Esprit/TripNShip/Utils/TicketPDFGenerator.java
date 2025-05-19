package org.Esprit.TripNShip.Utils;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.Esprit.TripNShip.Entities.Itinerary;
import org.Esprit.TripNShip.Entities.Ticket;
import org.Esprit.TripNShip.Services.ItineraryService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class TicketPDFGenerator {

    public static void generatePDF(Ticket ticket, String outputPath) {
        Document document = new Document();
        ItineraryService is = new ItineraryService();
        Itinerary itinerary = is.getItineraryByCode(ticket.getItineraryCode());

        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();

            // Titre
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Travel Ticket", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // ligne vide

            // Format de date et heure
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            // Contenu du ticket
            document.add(new Paragraph("Itinerary : " + ticket.getItineraryCode()));
            document.add(new Paragraph("Email : " + ticket.getUserEmail()));
            document.add(new Paragraph("Departure Location : "+ (itinerary.getDepartureLocation())));
            document.add(new Paragraph("Departure Date : " + ticket.getDepartureDate().format(dateFormatter)));
            document.add(new Paragraph("Departure Time : " + ticket.getDepartureTime().format(timeFormatter)));
            document.add(new Paragraph("Arrival Location : "+itinerary.getArrivalLocation()));
            document.add(new Paragraph("Arrival Date : " + ticket.getArrivalDate().format(dateFormatter)));
            document.add(new Paragraph("Arrival Time : " + ticket.getArrivalTime().format(timeFormatter)));
            document.add(new Paragraph("Price : " + ticket.getPrice() + " TND"));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Travel Safe"));

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}
