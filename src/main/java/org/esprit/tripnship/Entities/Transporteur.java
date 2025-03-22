package com.esprit.models;

public class Transporteur {

    private int idTransporteur;
    private String nom;
    private String typeTransport;
    private String telephone;
    private String email;
    private String siteWeb;


    // Constructeur avec tous les paramètres (idTransporteur, nom, typeTransport, téléphone, email, siteWeb)
    public Transporteur(int idTransporteur, String nom, String typeTransport, String telephone, String email, String siteWeb) {
        this.idTransporteur = idTransporteur;
        this.nom = nom;
        this.typeTransport = typeTransport;
        this.telephone = telephone;
        this.email = email;
        this.siteWeb = siteWeb;


    }

    // Constructeur sans idTransporteur (utile lors de l'ajout d'un nouveau transporteur)
    public Transporteur(String nom, String typeTransport, String telephone, String email, String siteWeb) {
        this.nom = nom;
        this.typeTransport = typeTransport;
        this.telephone = telephone;
        this.email = email;
        this.siteWeb = siteWeb;

    }

    // Getters et Setters
    public int getIdTransporteur() {
        return idTransporteur;
    }

    public void setIdTransporteur(int idTransporteur) {
        this.idTransporteur = idTransporteur;
    }

    public String getNom() {
        return nom;
    }



    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTypeTransport() {
        return typeTransport;
    }

    public void setTypeTransport(String typeTransport) {
        this.typeTransport = typeTransport;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSiteWeb() {
        return siteWeb;
    }

    public void setSiteWeb(String siteWeb) {
        this.siteWeb = siteWeb;
    }

    @Override
    public String toString() {
        return "Transporteur{" +
                "idTransporteur=" + idTransporteur +
                ", nom='" + nom + '\'' +
                ", typeTransport='" + typeTransport + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", siteWeb='" + siteWeb + '\'' +

                '}';
    }
}
