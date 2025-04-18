package org.Esprit.TripNShip.Tests;



import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Services.ServiceExpedition;
import org.Esprit.TripNShip.Services.ServiceTransporter;

import java.util.Date;
import java.util.List;

public class TestExpeditionCRUD {
    public static void main(String[] args) {
        // Create transporter service
        ServiceTransporter transporterService = new ServiceTransporter();

        // Add new transporters using enum TransportType
        transporterService.add(new Transporter("La Poste", TransportType.LA_POSTE, "0123456789", "contact@laposte.fr", "https://www.laposte.fr"));
        transporterService.add(new Transporter("DHL Express", TransportType.DHL, "0987654321", "contact@dhl.com", "https://www.dhl.com"));

        // Update transporter (example: ID = 1)
        Transporter transporterToModify = new Transporter(1, "La Poste", TransportType.LA_POSTE, "0000000000", "contact@laposte.com", "https://www.laposte.com");
        transporterService.update(transporterToModify);

        // Delete transporter (example: ID = 1)
        Transporter transporterToDelete = new Transporter(1, "Placeholder", TransportType.DHL, "", "", "");
        transporterService.delete(transporterToDelete);

        // Display all transporters
        System.out.println("List of transporters after operations:");
        for (Transporter t : transporterService.getAll()) {
            System.out.println(t);
        }

        // ////////////////////////////////////////////
        ServiceExpedition expeditionService = new ServiceExpedition();

        // Add a new expedition
        System.out.println("\nAdding a new expedition...");
        Expedition expedition1 = new Expedition(
                2,
                5.5,
                PackageType.PARCEL,
                PackageStatus.PENDING,
                20.0,
                new Date(),
                new Date(),
                "Paris",
                "Marseille",
                "Warehouse"
        );
        expedition1.setLastUpdated(new Date());
        expeditionService.add(expedition1);

        // Retrieve all expeditions
        List<Expedition> expeditions = expeditionService.getAll();
        System.out.println("\nExpeditions retrieved from database:");
        for (Expedition exp : expeditions) {
            System.out.println(exp);
        }

        // Modify expedition
        if (!expeditions.isEmpty()) {
            Expedition expeditionToModify = expeditions.get(0);
            expeditionToModify.setPackageStatus(PackageStatus.SHIPPED);
            expeditionToModify.setLastUpdated(new Date());
            expeditionService.update(expeditionToModify);
            System.out.println("\nExpedition updated!");
        }

        // Delete expedition
        if (!expeditions.isEmpty()) {
            Expedition expeditionToDelete = expeditions.get(0);
            expeditionService.delete(expeditionToDelete);
            System.out.println("\nExpedition deleted!");
        }

        // Final list
        expeditions = expeditionService.getAll();
        System.out.println("\nFinal list of expeditions:");
        for (Expedition exp : expeditions) {
            System.out.println(exp);
        }
    }
}
