package org.Esprit.TripNShip.Services;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import org.Esprit.TripNShip.Entities.Expedition;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFExportService {

    private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(142, 68, 173); // #8e44ad
    private static final DeviceRgb SECONDARY_COLOR = new DeviceRgb(52, 152, 219); // #3498db
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();

    /**
     * Exports a list of expeditions to a PDF file
     *
     * @param expeditions List of expeditions to export
     * @param file Target file
     * @param filterDescription Description of the filters applied
     * @throws IOException If an error occurs during PDF creation
     */
    public static void exportToPDF(List<Expedition> expeditions, File file, String filterDescription) throws IOException {
        // Create PDF document
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4.rotate()); // Landscape mode for more columns
        document.setMargins(36, 36, 36, 36); // 0.5 inch margins

        // Fonts
        PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
        PdfFont regularFont = PdfFontFactory.createFont("Helvetica");

        // Add logo and header
        addHeader(document, boldFont);

        // Add report info
        addReportInfo(document, regularFont, boldFont, filterDescription, expeditions.size());

        // Create table
        addExpeditionsTable(document, expeditions, boldFont, regularFont);

        // Add footer
        addFooter(document, regularFont);

        document.close();
    }

    private static void addHeader(Document document, PdfFont boldFont) throws IOException {
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{0.2f, 0.8f}));
        headerTable.setWidth(UnitValue.createPercentValue(100));

        // Add logo
        try {
            // Try to load from resources
            URL logoUrl = PDFExportService.class.getResource("/images/logo.png");
            if (logoUrl != null) {
                Image logo = new Image(ImageDataFactory.create(logoUrl));
                logo.setWidth(80);
                logo.setHeight(60);

                Cell logoCell = new Cell().add(logo).setBorder(Border.NO_BORDER)
                        .setPadding(5).setTextAlignment(TextAlignment.CENTER);
                headerTable.addCell(logoCell);
            } else {
                // Fallback if logo not found
                Cell logoCell = new Cell().add(new Paragraph("TripNShip"))
                        .setBorder(Border.NO_BORDER).setFontColor(PRIMARY_COLOR)
                        .setFontSize(20).setBold().setPadding(5);
                headerTable.addCell(logoCell);
            }
        } catch (Exception e) {
            // Fallback if loading fails
            Cell logoCell = new Cell().add(new Paragraph("TripNShip"))
                    .setBorder(Border.NO_BORDER).setFontColor(PRIMARY_COLOR)
                    .setFontSize(20).setBold().setPadding(5);
            headerTable.addCell(logoCell);
        }

        // Add title
        Paragraph title = new Paragraph("Expeditions Report")
                .setFont(boldFont)
                .setFontSize(24)
                .setFontColor(PRIMARY_COLOR);

        Cell titleCell = new Cell().add(title)
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);

        headerTable.addCell(titleCell);
        document.add(headerTable);

        // Add separator line
        SolidBorder line = new SolidBorder(PRIMARY_COLOR, 2);
        document.add(new Paragraph("").setBorderBottom(line).setMarginBottom(10));
    }

    private static void addReportInfo(Document document, PdfFont regularFont, PdfFont boldFont,
                                      String filterDescription, int expeditionCount) {
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{0.5f, 0.5f}));
        infoTable.setWidth(UnitValue.createPercentValue(100));
        infoTable.setBorder(Border.NO_BORDER);

        // Generated date
        String generatedDate = "Generated on: " + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Paragraph dateParagraph = new Paragraph(generatedDate)
                .setFont(regularFont)
                .setFontSize(10)
                .setFontColor(ColorConstants.DARK_GRAY);

        // Filter info
        Paragraph filterParagraph = new Paragraph(filterDescription)
                .setFont(regularFont)
                .setFontSize(10)
                .setFontColor(ColorConstants.DARK_GRAY);

        // Expedition count
        Paragraph countParagraph = new Paragraph("Total Expeditions: " + expeditionCount)
                .setFont(boldFont)
                .setFontSize(12)
                .setFontColor(SECONDARY_COLOR);

        Cell leftCell = new Cell()
                .add(dateParagraph)
                .add(filterParagraph)
                .setBorder(Border.NO_BORDER);

        Cell rightCell = new Cell()
                .add(countParagraph)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);

        infoTable.addCell(leftCell);
        infoTable.addCell(rightCell);

        document.add(infoTable);
        document.add(new Paragraph("\n"));
    }

    private static void addExpeditionsTable(Document document, List<Expedition> expeditions,
                                            PdfFont boldFont, PdfFont regularFont) {
        // Create table with columns
        Table table = new Table(UnitValue.createPercentArray(new float[]{0.6f, 0.8f, 1.2f, 1.2f, 0.8f, 0.8f, 1.5f, 1.0f, 1.0f, 0.8f}));
        table.setWidth(UnitValue.createPercentValue(100));

        // Add table headers
        String[] headers = {"ID", "Status", "Client", "Transporter", "Type", "Weight (kg)", "Route", "Send Date", "Delivery Date", "Cost"};

        for (String header : headers) {
            table.addHeaderCell(
                    new Cell().add(new Paragraph(header)
                                    .setFont(boldFont)
                                    .setFontSize(11))
                            .setBackgroundColor(PRIMARY_COLOR)
                            .setFontColor(ColorConstants.WHITE)
                            .setPadding(5)
                            .setTextAlignment(TextAlignment.CENTER)
            );
        }

        // Add data rows
        boolean alternate = false;
        for (Expedition expedition : expeditions) {
            DeviceRgb rowColor = alternate ? new DeviceRgb(240, 240, 240) : (DeviceRgb) ColorConstants.WHITE;

            // ID
            Cell idCell = new Cell().add(new Paragraph("#" + expedition.getExpeditionId())
                            .setFont(regularFont).setFontSize(10))
                    .setBackgroundColor(rowColor);

            // Status with color coding
            Cell statusCell = createStatusCell(expedition, regularFont, rowColor);

            // Client
            String clientName = expedition.getClient() != null ?
                    expedition.getClient().getFirstName() + " " + expedition.getClient().getLastName() : "N/A";
            Cell clientCell = new Cell().add(new Paragraph(clientName)
                            .setFont(regularFont).setFontSize(10))
                    .setBackgroundColor(rowColor);

            // Transporter
            String transporterName = expedition.getTransporter() != null ?
                    expedition.getTransporter().getFirstName() + " " +
                            expedition.getTransporter().getLastName() : "Not Assigned";
            Cell transporterCell = new Cell().add(new Paragraph(transporterName)
                            .setFont(regularFont).setFontSize(10))
                    .setBackgroundColor(rowColor);

            // Package type
            Cell typeCell = new Cell().add(new Paragraph(expedition.getPackageType().toString())
                            .setFont(regularFont).setFontSize(10))
                    .setBackgroundColor(rowColor);

            // Weight
            Cell weightCell = new Cell().add(new Paragraph(String.format("%.1f", expedition.getWeight()))
                            .setFont(regularFont).setFontSize(10))
                    .setBackgroundColor(rowColor);

            // Route
            String route = expedition.getDepartureCity() + " → " + expedition.getArrivalCity();
            Cell routeCell = new Cell().add(new Paragraph(route)
                            .setFont(regularFont).setFontSize(10))
                    .setBackgroundColor(rowColor);

            // Send date
            String sendDate = DATE_FORMAT.format(expedition.getSendDate());
            Cell sendDateCell = new Cell().add(new Paragraph(sendDate)
                            .setFont(regularFont).setFontSize(10))
                    .setBackgroundColor(rowColor);

            // Delivery date
            String deliveryDate = DATE_FORMAT.format(expedition.getEstimatedDeliveryDate());
            Cell deliveryDateCell = new Cell().add(new Paragraph(deliveryDate)
                            .setFont(regularFont).setFontSize(10))
                    .setBackgroundColor(rowColor);

            // Cost
            String cost = CURRENCY_FORMAT.format(expedition.getShippingCost());
            Cell costCell = new Cell().add(new Paragraph(cost)
                            .setFont(regularFont).setFontSize(10))
                    .setBackgroundColor(rowColor);

            // Add cells to row
            table.addCell(idCell);
            table.addCell(statusCell);
            table.addCell(clientCell);
            table.addCell(transporterCell);
            table.addCell(typeCell);
            table.addCell(weightCell);
            table.addCell(routeCell);
            table.addCell(sendDateCell);
            table.addCell(deliveryDateCell);
            table.addCell(costCell);

            alternate = !alternate;
        }

        document.add(table);
    }

    private static Cell createStatusCell(Expedition expedition, PdfFont font, DeviceRgb rowColor) {
        String status = expedition.getPackageStatus().toString();

        DeviceRgb statusColor;
        switch (expedition.getPackageStatus()) {
            case PENDING:
                statusColor = new DeviceRgb(243, 156, 18); // Orange
                break;
            case SHIPPED:
                statusColor = new DeviceRgb(155, 89, 182); // Purple
                break;
            case IN_TRANSIT:
                statusColor = new DeviceRgb(52, 152, 219); // Blue
                break;
            case DELIVERED:
                statusColor = new DeviceRgb(46, 204, 113); // Green
                break;
            case CANCELLED:
                statusColor = new DeviceRgb(231, 76, 60); // Red
                break;
            default:
                statusColor = new DeviceRgb(127, 140, 141); // Gray
        }

        Paragraph statusParagraph = new Paragraph(status)
                .setFont(font)
                .setFontSize(10)
                .setFontColor(ColorConstants.WHITE);

        Table statusContainer = new Table(1)
                .setBorder(Border.NO_BORDER)
                .setWidth(UnitValue.createPercentValue(90))
                .setHorizontalAlignment(HorizontalAlignment.CENTER);

        Cell innerCell = new Cell()
                .add(statusParagraph)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(3)
                .setBackgroundColor(statusColor);

        statusContainer.addCell(innerCell);

        Cell outerCell = new Cell()
                .add(statusContainer)
                .setBackgroundColor(rowColor)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setPadding(5);

        return outerCell;
    }

    private static void addFooter(Document document, PdfFont font) {
        String footerText = "© " + LocalDateTime.now().getYear() + " TripNShip - All rights reserved | Generated by TripNShip Admin System";

        document.add(new Paragraph("\n"));
        SolidBorder line = new SolidBorder(ColorConstants.LIGHT_GRAY, 1);
        document.add(new Paragraph("").setBorderTop(line).setMarginBottom(5));

        Paragraph footer = new Paragraph(footerText)
                .setFont(font)
                .setFontSize(8)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER);

        document.add(footer);
    }
}
