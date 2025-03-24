package org.esprit.tripnship.Entities;

public class Transport {
    private int transportId;
    private TypeTransport transportation;
    private String companyName;
    private int companyPhone;
    private String companyEmail;
    private String companyWebsite;

    public Transport(int transportId, TypeTransport transportation, String companyName, int companyPhone, String companyEmail, String companyWebsite) {
        this.transportId = transportId;
        this.transportation = transportation;
        this.companyName = companyName;
        this.companyPhone = companyPhone;
        this.companyEmail = companyEmail;
        this.companyWebsite = companyWebsite;
    }

    public Transport(int transportId, TypeTransport transportation, String companyName) {
        this.transportId = transportId;
        this.transportation = transportation;
        this.companyName = companyName;
    }

    public Transport(int transportId, TypeTransport transportation, String companyName, int companyPhone) {
        this.transportId = transportId;
        this.transportation = transportation;
        this.companyName = companyName;
        this.companyPhone = companyPhone;
    }

    public Transport(String companyEmail, int companyPhone, TypeTransport transportation, int transportId, String companyName) {
        this.companyEmail = companyEmail;
        this.companyPhone = companyPhone;
        this.transportation = transportation;
        this.transportId = transportId;
        this.companyName = companyName;
    }

    public int getTransportId() {
        return transportId;
    }

    public void setTransportId(int transportId) {
        this.transportId = transportId;
    }

    public TypeTransport getTransportation() {
        return transportation;
    }

    public void setTransportation(TypeTransport transportation) {
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

