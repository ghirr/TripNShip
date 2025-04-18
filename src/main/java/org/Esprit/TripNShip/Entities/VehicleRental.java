package org.Esprit.TripNShip.Entities;

import java.time.LocalDateTime;

public class VehicleRental {

    private int idRental;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private float totalPrice;
    private StautCircuit statusCircuit;
    private Vehicle vehicle;
    private User user;


    public VehicleRental(LocalDateTime startDate, LocalDateTime endDate, float totalPrice,
                         StautCircuit statusCircuit, Vehicle vehicle, User user) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.statusCircuit = statusCircuit;
        this.vehicle = vehicle;
        this.user = user;
    }

    public VehicleRental(int idRental, LocalDateTime startDate, LocalDateTime endDate, float totalPrice,
                         StautCircuit statusCircuit, Vehicle vehicle, User user) {
        this.idRental = idRental;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.statusCircuit = statusCircuit;
        this.vehicle = vehicle;
        this.user = user;
    }

    public int getIdRental() {
        return idRental;
    }

    public void setIdRental(int idRental) {
        this.idRental = idRental;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public StautCircuit getStatus() {
        return statusCircuit;
    }

    public void setStatus(StautCircuit status) {
        this.statusCircuit = status;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "VehicleRental{" +
                "idRental=" + idRental +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", totalPrice=" + totalPrice +
                ", statusCircuit=" + statusCircuit +
                ", vehicle=" + vehicle +
                ", user=" + user +
                '}';
    }
}
