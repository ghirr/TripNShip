package org.Esprit.TripNShip.Entities;

public class Transport {
    private String transportId;
    private TransportType transportation;
    private String companyName;
    private int companyPhone;
    private String companyEmail;
    private String companyWebsite;

    public Transport(String transportId, TransportType transportation, String companyName, int companyPhone, String companyEmail, String companyWebsite) {
        this.transportId = transportId;
        this.transportation = transportation;
        this.companyName = companyName;
        this.companyPhone = companyPhone;
        this.companyEmail = companyEmail;
        this.companyWebsite = companyWebsite;
    }

    public Transport(String transportId, TransportType transportation, String companyName) {
        this.transportId = transportId;
        this.transportation = transportation;
        this.companyName = companyName;
    }

    public Transport(String transportId, TransportType transportation, String companyName, int companyPhone) {
        this.transportId = transportId;
        this.transportation = transportation;
        this.companyName = companyName;
        this.companyPhone = companyPhone;
    }

    public Transport(String companyEmail, int companyPhone, TransportType transportation, String transportId, String companyName) {
        this.companyEmail = companyEmail;
        this.companyPhone = companyPhone;
        this.transportation = transportation;
        this.transportId = transportId;
        this.companyName = companyName;
    }

    public String getTransportId() {
        return transportId;
    }

    public void setTransportId(String transportId) {
        this.transportId = transportId;
    }

    public TransportType getTransportation() {
        return transportation;
    }

    public void setTransportation(TransportType transportation) {
        this.transportation = transportation;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(int companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    @Override
    public String toString() {
        return "Transport{" +
                "transportId=" + transportId +
                ", transportation=" + transportation +
                ", companyName='" + companyName + '\'' +
                ", companyPhone=" + companyPhone +
                ", companyEmail='" + companyEmail + '\'' +
                ", companyWebsite='" + companyWebsite + '\'' +
                '}';
    }
}

