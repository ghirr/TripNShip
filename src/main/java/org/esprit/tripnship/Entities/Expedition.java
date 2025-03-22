package org.esprit.tripnship.Entities;

import java.util.Date;

public class Expedition {

    private int idExpedition;

    private int idTransporteur;
    private double poids;
    private String typeColis; // Lettre, Document, Colis, Fragile
    private String statut; // En attente, Expédié, En transit, Livré, Annulé
    private double fraisExpedition;
    private Date dateEnvoi;
    private Date dateEstimationLivraison;
    private String villeDepart;
    private String villeArrivee;
    private String localisationActuelle;
    private Date derniereMiseAJour;

    // Constructeur complet
    public Expedition(int idExpedition, int idTransporteur, double poids, String typeColis,
                      String statut, double fraisExpedition, Date dateEnvoi, Date dateEstimationLivraison,
                      String villeDepart, String villeArrivee, String localisationActuelle) {
        this.idExpedition = idExpedition;
        this.idTransporteur = idTransporteur;
        this.poids = poids;
        this.typeColis = typeColis;
        this.statut = statut;
        this.fraisExpedition = fraisExpedition;
        this.dateEnvoi = dateEnvoi;
        this.dateEstimationLivraison = dateEstimationLivraison;
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.localisationActuelle = localisationActuelle;
        this.derniereMiseAJour = derniereMiseAJour;
    }


    public Expedition(int idTransporteur, double poids, String typeColis, String statut,
                      double fraisExpedition, Date dateEnvoi, Date dateEstimationLivraison,
                      String villeDepart, String villeArrivee, String localisationActuelle) {

        this.idTransporteur = idTransporteur;
        this.poids = poids;
        this.typeColis = typeColis;
        this.statut = statut;
        this.fraisExpedition = fraisExpedition;
        this.dateEnvoi = dateEnvoi;
        this.dateEstimationLivraison = dateEstimationLivraison;
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.localisationActuelle = localisationActuelle;
    }

    // Getters et Setters
    public int getIdExpedition() {
        return idExpedition;
    }

    public void setIdExpedition(int idExpedition) {
        this.idExpedition = idExpedition;
    }


    public int getIdTransporteur() {
        return idTransporteur;
    }

    public void setIdTransporteur(int idTransporteur) {
        this.idTransporteur = idTransporteur;
    }

    public double getPoids() {
        return poids;
    }

    public void setPoids(double poids) {
        this.poids = poids;
    }

    public String getTypeColis() {
        return typeColis;
    }

    public void setTypeColis(String typeColis) {
        this.typeColis = typeColis;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public double getFraisExpedition() {
        return fraisExpedition;
    }

    public void setFraisExpedition(double fraisExpedition) {
        this.fraisExpedition = fraisExpedition;
    }

    public Date getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(Date dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public Date getDateEstimationLivraison() {
        return dateEstimationLivraison;
    }

    public void setDateEstimationLivraison(Date dateEstimationLivraison) {
        this.dateEstimationLivraison = dateEstimationLivraison;
    }

    public String getVilleDepart() {
        return villeDepart;
    }

    public void setVilleDepart(String villeDepart) {
        this.villeDepart = villeDepart;
    }

    public String getVilleArrivee() {
        return villeArrivee;
    }

    public void setVilleArrivee(String villeArrivee) {
        this.villeArrivee = villeArrivee;
    }

    public String getLocalisationActuelle() {
        return localisationActuelle;
    }

    public void setLocalisationActuelle(String localisationActuelle) {
        this.localisationActuelle = localisationActuelle;
    }

    public Date getDerniereMiseAJour() {
        return derniereMiseAJour;
    }

    public void setDerniereMiseAJour(Date derniereMiseAJour) {
        this.derniereMiseAJour = derniereMiseAJour;
    }

    @Override
    public String toString() {
        return "Expeditions{" +
                "idExpedition=" + idExpedition +

                ", idTransporteur=" + idTransporteur +
                ", poids=" + poids +
                ", typeColis='" + typeColis + '\'' +
                ", statut='" + statut + '\'' +
                ", fraisExpedition=" + fraisExpedition +
                ", dateEnvoi=" + dateEnvoi +
                ", dateEstimationLivraison=" + dateEstimationLivraison +
                ", villeDepart='" + villeDepart + '\'' +
                ", villeArrivee='" + villeArrivee + '\'' +
                ", localisationActuelle='" + localisationActuelle + '\'' +
                ", derniereMiseAJour=" + derniereMiseAJour +
                '}';
    }

}
