package org.Esprit.TripNShip.Entities;

import java.time.LocalDateTime;

public class VehicleRental {

    private int idRental;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private float totalPrice;
    private StautCircuit statusCircuit;
    private Vehicle vehicle;
    private RentalAgency rentalAgency;

    public VehicleRental(LocalDateTime startDate, LocalDateTime endDate, float totalPrice,
                         StautCircuit statusCircuit, Vehicle vehicle, RentalAgency rentalAgency) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.statusCircuit = statusCircuit;
        this.vehicle = vehicle;
        this.rentalAgency = rentalAgency;
    }

    public VehicleRental(int idRental, LocalDateTime startDate, LocalDateTime endDate, float totalPrice,
                         StautCircuit statusCircuit, Vehicle vehicle, RentalAgency rentalAgency) {
        this.idRental = idRental;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.statusCircuit = statusCircuit;
        this.vehicle = vehicle;
        this.rentalAgency = rentalAgency;
    }

    public VehicleRental() {

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

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }


    public StautCircuit getStatusCircuit() {
        return statusCircuit;
    }

    public void setStatusCircuit(StautCircuit statusCircuit) {
        this.statusCircuit = statusCircuit;
    }

    public RentalAgency getRentalAgency() {
        return rentalAgency;
    }

    public void setRentalAgency(RentalAgency rentalAgency) {
        this.rentalAgency = rentalAgency;
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
                ", rentalAgency=" + rentalAgency +
                '}';
    }
}
