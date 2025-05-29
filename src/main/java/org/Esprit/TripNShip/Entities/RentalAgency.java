package org.Esprit.TripNShip.Entities;


public class RentalAgency {


    private int idAgency;
    private String nameAgency;
    private String addressAgency;
    private String contactAgency;
    private float rating;


    public RentalAgency(String nameAgency, String addressAgency, String contactAgency, float rating) {
        this.nameAgency = nameAgency;
        this.addressAgency = addressAgency;
        this.contactAgency = contactAgency;
        this.rating = rating;

    }
    public RentalAgency(int idAgency, String nameAgency, String addressAgency, String contactAgency, float rating) {
        this.idAgency = idAgency;
        this.nameAgency = nameAgency;
        this.addressAgency = addressAgency;
        this.contactAgency = contactAgency;
        this.rating = rating;

    }

    public RentalAgency() {

    }

    public RentalAgency(int idAgency) {
    }


    public int getIdAgency() {
        return idAgency;
    }

    public void setIdAgency(int idAgency) {
        this.idAgency = idAgency;
    }

    public String getNameAgency() {
        return nameAgency;
    }

    public void setNameAgency(String nameAgency) {
        this.nameAgency = nameAgency;
    }

    public String getAddressAgency() {
        return addressAgency;
    }

    public void setAddressAgency(String addressAgency) {
        this.addressAgency = addressAgency;
    }

    public String getContactAgency() {
        return contactAgency;
    }

    public void setContactAgency(String contactAgency) {
        this.contactAgency = contactAgency;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }



    @Override
    public String toString() {
        return "RentalAgency{" +
                "idAgency=" + idAgency +
                ", nameAgency='" + nameAgency + '\'' +
                ", addressAgency='" + addressAgency + '\'' +
                ", contactAgency='" + contactAgency + '\'' +
                ", rating=" + rating +
                '}';
    }

}
