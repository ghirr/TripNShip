package com.esprit.services;

import com.esprit.models.expeditions;
import com.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceExpedition implements IService<expeditions> {

    private Connection connection;

    public ServiceExpedition() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(expeditions expedition) {
        String req = "INSERT INTO expeditions (idTransporteur, poids, typeColis, statut, fraisExpedition, " +
                "dateEnvoi, dateEstimationLivraison, villeDepart, villeArrivee, localisationActuelle) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, expedition.getIdTransporteur());
            pst.setDouble(2, expedition.getPoids());
            pst.setString(3, expedition.getTypeColis());
            pst.setString(4, expedition.getStatut());
            pst.setDouble(5, expedition.getFraisExpedition());
            pst.setDate(6, new java.sql.Date(expedition.getDateEnvoi().getTime()));
            pst.setDate(7, new java.sql.Date(expedition.getDateEstimationLivraison().getTime()));
            pst.setString(8, expedition.getVilleDepart());
            pst.setString(9, expedition.getVilleArrivee());
            pst.setString(10, expedition.getLocalisationActuelle());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Expédition ajoutée !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'expédition: " + e.getMessage());
        }
    }

    @Override
    public void modifier(expeditions expedition) {
        String req = "UPDATE expeditions SET idTransporteur=?, poids=?, typeColis=?, statut=?, " +
                "fraisExpedition=?, dateEnvoi=?, dateEstimationLivraison=?, villeDepart=?, villeArrivee=?, " +
                "localisationActuelle=? WHERE idExpedition=?";

        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, expedition.getIdTransporteur());
            pst.setDouble(2, expedition.getPoids());
            pst.setString(3, expedition.getTypeColis());
            pst.setString(4, expedition.getStatut());
            pst.setDouble(5, expedition.getFraisExpedition());
            pst.setDate(6, new java.sql.Date(expedition.getDateEnvoi().getTime()));
            pst.setDate(7, new java.sql.Date(expedition.getDateEstimationLivraison().getTime()));
            pst.setString(8, expedition.getVilleDepart());
            pst.setString(9, expedition.getVilleArrivee());
            pst.setString(10, expedition.getLocalisationActuelle());
            pst.setInt(11, expedition.getIdExpedition());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Expédition modifiée !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de l'expédition: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(expeditions expedition) {
        String req = "DELETE FROM expeditions WHERE idExpedition=?";

        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, expedition.getIdExpedition());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Expédition supprimée !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'expédition: " + e.getMessage());
        }
    }

    @Override
    public List<expeditions> recuperer() {
        List<expeditions> expeditionsList = new ArrayList<>();
        String req = "SELECT * FROM expeditions";

        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                expeditions expedition = new expeditions(
                        rs.getInt("idExpedition"),
                        rs.getInt("idTransporteur"),
                        rs.getDouble("poids"),
                        rs.getString("typeColis"),
                        rs.getString("statut"),
                        rs.getDouble("fraisExpedition"),
                        rs.getDate("dateEnvoi"),
                        rs.getDate("dateEstimationLivraison"),
                        rs.getString("villeDepart"),
                        rs.getString("villeArrivee"),
                        rs.getString("localisationActuelle")
                );
                expeditionsList.add(expedition);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des expéditions: " + e.getMessage());
        }
        return expeditionsList;
    }
}
