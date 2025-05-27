package org.Esprit.TripNShip.Utils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.Esprit.TripNShip.Entities.Itinerary;
import org.Esprit.TripNShip.Entities.Ticket;
import org.Esprit.TripNShip.Services.ItineraryService;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class TicketPDFGenerator {

    public static void generatePDF(Ticket ticket, String outputPath) throws IOException {
        Document document = new Document();
        Image logo = Image.getInstance("src/main/resources/images/logo.png");
        logo.scaleToFit(300,300);
        logo.setAlignment(Element.ALIGN_CENTER);
        ItineraryService is = new ItineraryService();
        Itinerary itinerary = is.getItineraryByCode(ticket.getItineraryCode());
        String uniqueData = "Ticket"+ticket.getTicketId()+"-"+System.currentTimeMillis();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();

            // Titre
            document.add(logo);
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
            document.add(new Paragraph(" "));
            document.add(generateQRCodeImage(ticket.toString()));
            Paragraph footer = new Paragraph("Travel Safe");// pour
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
        } catch (DocumentException | IOException | WriterException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
    public static Image generateQRCodeImage(String data) throws WriterException, IOException, BadElementException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 150, 150);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);
        Image itextImage = Image.getInstance(baos.toByteArray());
        itextImage.setAlignment(Image.ALIGN_CENTER);
        return itextImage;
    }
}
