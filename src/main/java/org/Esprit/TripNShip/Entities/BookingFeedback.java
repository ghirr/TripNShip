package org.Esprit.TripNShip.Entities;

public class BookingFeedback {

    private int idFeedback;
    private int idAccommodation;
    private int idUser;
    private int rating;
    private String comment;

    public BookingFeedback() {
    }

    public BookingFeedback(int idFeedback, int idAccommodation, int idUser, int rating, String comment) {
        this.idFeedback = idFeedback;
        this.idAccommodation = idAccommodation;
        this.idUser = idUser;
        this.rating = rating;
        this.comment = comment;
    }

    public int getIdFeedback() {
        return idFeedback;
    }

    public void setIdFeedback(int idFeedback) {
        this.idFeedback = idFeedback;
    }

    public int getIdAccommodation() {
        return idAccommodation;
    }

    public void setIdAccommodation(int idAccommodation) {
        this.idAccommodation = idAccommodation;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "BookingFeedback{" +
                "idFeedback=" + idFeedback +
                ", idAccommodation=" + idAccommodation +
                ", idUser=" + idUser +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                '}';
    }
}
