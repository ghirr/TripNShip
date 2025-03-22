package org.esprit.tripnship.Tests;

public class TestExpeditionCRUD {
    public static void main(String[] args) {
        // Création du service Transporteur
        ServiceTransporteur sp = new ServiceTransporteur();

        // Ajouter un nouveau transporteur
        sp.ajouter(new Transporteur("La Poste", "Poste", "0123456789", "contact@laposte.fr", "https://www.laposte.fr"));

        // Ajouter un autre transporteur
        sp.ajouter(new Transporteur("DHL", "DHL", "0987654321", "contact@dhl.com", "https://www.dhl.com"));

        // Modifier un transporteur (par exemple celui avec idTransporteur = 1)
        Transporteur transporteurToModify = new Transporteur(1, "La Poste", "Poste", "0000000000", "contact@laposte.com", "https://www.laposte.com");
        sp.modifier(transporteurToModify);

        // Supprimer un transporteur (par exemple celui avec idTransporteur = 1)
        Transporteur transporteurToDelete = new Transporteur(1, "", "", "", "", "");
        sp.supprimer(transporteurToDelete);

        // Récupérer et afficher tous les transporteurs
        System.out.println("Liste des transporteurs après les opérations :");
        for (Transporteur t : sp.recuperer()) {
            System.out.println(t);
        }
        /// ////////////////////
        ServiceExpedition serviceExpedition = new ServiceExpedition();

        // Test Ajout d'une nouvelle expedition
        System.out.println("Ajout d'une nouvelle expedition...");
        expeditions exp1 = new expeditions(
                1, 2, 5.5, "Colis", "En attente", 20.0, new java.util.Date(), new java.util.Date(),
                "Paris", "Marseille", null
        );
        serviceExpedition.ajouter(exp1);

        // Test récupération des expeditions
        List<expeditions> expeditions = serviceExpedition.recuperer();
        for (expeditions exp : expeditions) {
            System.out.println(exp);
        }

        // Test modification
        if (!expeditions.isEmpty()) {
            expeditions expToModify = expeditions.get(0);
            expToModify.setStatut("Expédié");
            serviceExpedition.modifier(expToModify);
            System.out.println("\nExpedition modifiée !");
        }

        // Test suppression
        if (!expeditions.isEmpty()) {
            expeditions expToDelete = expeditions.get(0);
            serviceExpedition.supprimer(expToDelete);
            System.out.println("\nExpedition supprimée !");
        }

        // Vérification après suppression
        expeditions = serviceExpedition.recuperer();
        for (expeditions exp : expeditions) {
            System.out.println(exp);
        }

    }
}
