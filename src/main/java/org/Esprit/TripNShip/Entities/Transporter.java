package org.Esprit.TripNShip.Entities;

public class Transporter {

    private int transporterId;
    private String name;
    private TransportType transportType;
    private String phone;
    private String email;
    private String website;

    public Transporter(int transporterId, String name, TransportType transportType, String phone, String email, String website) {
        this.transporterId = transporterId;
        this.name = name;
        this.transportType = transportType;
        this.phone = phone;
        this.email = email;
        this.website = website;
    }

    public Transporter(String name, TransportType transportType, String phone, String email, String website) {
        this.name = name;
        this.transportType = transportType;
        this.phone = phone;
        this.email = email;
        this.website = website;
    }

    public int getTransporterId() {
        return transporterId;
    }

    public void setTransporterId(int transporterId) {
        this.transporterId = transporterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public void setTransportType(TransportType transportType) {
        this.transportType = transportType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return "Transporter{" +
                "transporterId=" + transporterId +
                ", name='" + name + '\'' +
                ", transportType=" + transportType +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", website='" + website + '\'' +
                '}';
    }
}
