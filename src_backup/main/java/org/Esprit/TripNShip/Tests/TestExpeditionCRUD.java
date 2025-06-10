package org.Esprit.TripNShip.Tests;

import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Services.ServiceExpedition;
import org.Esprit.TripNShip.Services.ServiceTransporter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class TestExpeditionCRUD {

    public static void main(String[] args) {
        ServiceTransporter transporterService = new ServiceTransporter();

        // 1. Add new transporters
        Transporter transporter1 = new Transporter(
                "Jean",
                "Dupont",
                Gender.HOMME,
                "laposte@example.com",
                "1234",
                "profile1.png",
                LocalDateTime.of(1985, 5, 20, 0, 0),
                "0123456789",
                TransportType.LA_POSTE,
                "https://www.laposte.fr"
        );

        transporterService.add(transporter1);

        Transporter transporter2 = new Transporter(
                "Marie",
                "Curie",
                Gender.FEMME,
                "dhl@example.com",
                "abcd",
                "profile2.png",
                LocalDateTime.of(1990, 3, 10, 0, 0),
                "0987654321",
                TransportType.DHL,
                "https://www.dhl.com"
        );

        transporterService.add(transporter2);

        // 2. Retrieve all transporters to use real IDs
        List<Transporter> allTransporters = transporterService.getAll();
        if (allTransporters.size() < 2) {
            System.out.println("Error: Not enough transporters found for testing.");
            return;
        }

        // 3. Update transporter
        Transporter transporterToModify = allTransporters.get(0);
        transporterToModify.setPhoneNumber("0000000000");
        transporterToModify.setEmail("updated@laposte.com");
        transporterService.update(transporterToModify);

        // 4. Delete a transporter
        Transporter transporterToDelete = allTransporters.get(1);
        transporterService.delete(transporterToDelete);

        // 5. Display current transporters
        System.out.println("\nList of transporters:");
        for (Transporter t : transporterService.getAll()) {
            System.out.println(t);
        }

        // /////////////////////////////////////////
        ServiceExpedition expeditionService = new ServiceExpedition();

        // 6. Add a new expedition
        System.out.println("\nAdding a new expedition...");
        Transporter assignedTransporter = transporterService.getAll().get(0); // get remaining transporter
        Expedition expedition = new Expedition(
                assignedTransporter,
                5.5,
                PackageType.DOCUMENT,
                PackageStatus.PENDING,
                20.0,
                new Date(),
                new Date(),
                "Paris",
                "Marseille",
                "Warehouse"
        );
        expedition.setLastUpdated(new Date());
        expeditionService.add(expedition);

        // 7. Retrieve and show all expeditions
        List<Expedition> expeditions = expeditionService.getAll();
        System.out.println("\nExpeditions retrieved:");
        for (Expedition exp : expeditions) {
            System.out.println(exp);
        }

        // 8. Update expedition status
        if (!expeditions.isEmpty()) {
            Expedition expeditionToModify = expeditions.get(0);
            expeditionToModify.setPackageStatus(PackageStatus.SHIPPED);
            expeditionToModify.setLastUpdated(new Date());
            expeditionService.update(expeditionToModify);
            System.out.println("\nExpedition updated!");
        }

        // 9. Delete expedition
        if (!expeditions.isEmpty()) {
            Expedition expeditionToDelete = expeditions.get(0);
            expeditionService.delete(expeditionToDelete);
            System.out.println("\nExpedition deleted!");
        }

        // 10. Final list of expeditions
        expeditions = expeditionService.getAll();
        System.out.println("\nFinal list of expeditions:");
        for (Expedition exp : expeditions) {
            System.out.println(exp);
        }
    }
}
