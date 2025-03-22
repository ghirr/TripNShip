package org.esprit.tripnship.Tests;

import org.esprit.tripnship.Entities.Expedition;
import org.esprit.tripnship.Entities.Transporter;
import org.esprit.tripnship.Services.ServiceExpedition;
import org.esprit.tripnship.Services.ServiceTransporter;

import java.util.List;

public class TestExpeditionCRUD {
    public static void main(String[] args) {
        // Création du service Transporteur
        ServiceTransporter sp = new ServiceTransporter();

        // Ajouter un nouveau transporteur
        sp.add(new Transporter("La Poste", "Poste", "0123456789", "contact@laposte.fr", "https://www.laposte.fr"));

        // Ajouter un autre transporteur
        sp.add(new Transporter("DHL", "DHL", "0987654321", "contact@dhl.com", "https://www.dhl.com"));

        // Modifier un transporteur (par exemple celui avec idTransporteur = 1)
        Transporter transporteurToModify = new Transporter(1, "La Poste", "Poste", "0000000000", "contact@laposte.com", "https://www.laposte.com");
        sp.update(transporteurToModify);

        // Supprimer un transporteur (par exemple celui avec idTransporteur = 1)
        Transporter transporteurToDelete = new Transporter(1, "", "", "", "", "");
        sp.delete(transporteurToDelete);

        // Récupérer et afficher tous les transporteurs
        System.out.println("Liste des transporteurs après les opérations :");
        for (Transporter t : sp.getAll()) {
            System.out.println(t);
        }
        /// ////////////////////
        ServiceExpedition serviceExpedition = new ServiceExpedition();

        // Test Ajout d'une nouvelle expedition
        System.out.println("Ajout d'une nouvelle expedition...");
        Expedition exp1 = new Expedition(
                1, 2, 5.5, "Colis", "En attente", 20.0, new java.util.Date(), new java.util.Date(),
                "Paris", "Marseille", null
        );
        serviceExpedition.add(exp1);

        // Test récupération des expeditions
        List<Expedition> expeditions = serviceExpedition.getAll();
        for (Expedition exp : expeditions) {
            System.out.println(exp);
        }

        // Test modification
        if (!expeditions.isEmpty()) {
            Expedition expToModify = expeditions.get(0);
            expToModify.setStatut("Expédié");
            serviceExpedition.update(expToModify);
            System.out.println("\nExpedition modifiée !");
        }

        // Test suppression
        if (!expeditions.isEmpty()) {
            Expedition expToDelete = expeditions.get(0);
            serviceExpedition.delete(expToDelete);
            System.out.println("\nExpedition supprimée !");
        }

        // Vérification après suppression
        expeditions = serviceExpedition.getAll();
        for (Expedition exp : expeditions) {
            System.out.println(exp);
        }

    }
}
