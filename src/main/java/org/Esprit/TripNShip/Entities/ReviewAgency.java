package org.Esprit.TripNShip.Entities;

public class ReviewAgency {
    private int idReview;
    private String comment;
    private int rating;
    private User user;
    private VehicleRental rental;


    public ReviewAgency(String comment, int rating, User user, VehicleRental rental) {
        this.comment = comment;
        this.rating = rating;
        this.user = user;
        this.rental = rental;
    }

    public ReviewAgency(int idReview, String comment, int rating, User user, VehicleRental rental) {
        this.idReview = idReview;
        this.comment = comment;
        this.rating = rating;
        this.user = user;
        this.rental = rental;
    }

    public int getIdReview() {
        return idReview;
    }

    public void setIdReview(int idReview) {
        this.idReview = idReview;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public VehicleRental getRental() {
        return rental;
    }

    public void setRental(VehicleRental rental) {
        this.rental = rental;
    }

    @Override
    public String toString() {
        return "Review{" +
                "idReview=" + idReview +
                ", comment='" + comment + '\'' +
                ", rating=" + rating +
                ", user=" + user +
                ", rental=" + rental +
                '}';
    }
}
