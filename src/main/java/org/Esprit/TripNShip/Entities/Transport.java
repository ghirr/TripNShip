package org.Esprit.TripNShip.Entities;

public class Transport {
    private int transportId;
    private String logoPath;
    private String transporterReference;
    private TransportType transportation;
    private String companyName;
    private int companyPhone;
    private String companyEmail;
    private String companyWebsite;

    public Transport(int transportId, String logoPath, String transporterReference, TransportType transportation, String companyName, int companyPhone, String companyEmail, String companyWebsite) {
        this.transportId = transportId;
        this.logoPath = logoPath;
        this.transporterReference = transporterReference;
        this.transportation = transportation;
        this.companyName = companyName;
        this.companyPhone = companyPhone;
        this.companyEmail = companyEmail;
        this.companyWebsite = companyWebsite;
    }

    public Transport(String logoPath, String transporterReference, TransportType transportation, String companyName, int companyPhone, String companyEmail, String companyWebsite) {
        this.logoPath = logoPath;
        this.transporterReference = transporterReference;
        this.transportation = transportation;
        this.companyName = companyName;
        this.companyPhone = companyPhone;
        this.companyEmail = companyEmail;
        this.companyWebsite = companyWebsite;
    }

    public Transport(String transporterReference, TransportType transportation, String companyName, int companyPhone, String companyEmail, String companyWebsite) {
        this.transporterReference = transporterReference;
        this.transportation = transportation;
        this.companyName = companyName;
        this.companyPhone = companyPhone;
        this.companyEmail = companyEmail;
        this.companyWebsite = companyWebsite;
    }

    public Transport(String transporterReference, TransportType transportation, String companyName) {
        this.transporterReference = transporterReference;
        this.transportation = transportation;
        this.companyName = companyName;
    }

    public Transport(String transporterReference, TransportType transportation, String companyName, int companyPhone) {
        this.transporterReference = transporterReference;
        this.transportation = transportation;
        this.companyName = companyName;
        this.companyPhone = companyPhone;
    }

    public Transport(String transporterReference, String companyEmail, int companyPhone, TransportType transportation, String companyName) {
        this.companyEmail = companyEmail;
        this.companyPhone = companyPhone;
        this.transportation = transportation;
        this.transporterReference = transporterReference;
        this.companyName = companyName;
    }

    public Transport(int transportId, String transporterReference, TransportType transportation, String companyName, int companyPhone, String companyEmail, String companyWebsite) {
        this.transportId = transportId;
        this.transporterReference = transporterReference;
        this.transportation = transportation;
        this.companyName = companyName;
        this.companyPhone = companyPhone;
        this.companyEmail = companyEmail;
        this.companyWebsite = companyWebsite;
    }

    public int getTransportId() {
        return transportId;
    }

    public void setTransportId(int transportId) {
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


    public String getTransporterReference() {
        return transporterReference;
    }

    public void setTransporterReference(String transporterReference) {
        this.transporterReference = transporterReference;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    @Override
    public String toString() {
        return "Transport{" +
                "transportId=" + transportId +
                ", transporterReference='" + transporterReference + '\'' +
                ", transportation=" + transportation +
                ", companyName='" + companyName + '\'' +
                ", companyPhone=" + companyPhone +
                ", companyEmail='" + companyEmail + '\'' +
                ", companyWebsite='" + companyWebsite + '\'' +
                ", logoPath='" + logoPath + '\'' +
                '}';
    }
}

