package org.Esprit.TripNShip.Services;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.Esprit.TripNShip.Entities.Ticket;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class TicketPDFGenerator {

    public static void generatePDF(Ticket ticket, String outputPath) {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();

            // Titre
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Ticket de Réservation", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // ligne vide

            // Format de date et heure
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            // Contenu du ticket
            document.add(new Paragraph("Itinéraire : " + ticket.getItineraryCode()));
            document.add(new Paragraph("Email client : " + ticket.getUserEmail()));
            document.add(new Paragraph("Date départ : " + ticket.getDepartureDate().format(dateFormatter)));
            document.add(new Paragraph("Heure départ : " + ticket.getDepartureTime().format(timeFormatter)));
            document.add(new Paragraph("Date arrivée : " + ticket.getArrivalDate().format(dateFormatter)));
            document.add(new Paragraph("Heure arrivée : " + ticket.getArrivalTime().format(timeFormatter)));
            document.add(new Paragraph("Prix : " + ticket.getPrice() + " TND"));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Merci pour votre réservation !"));

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}