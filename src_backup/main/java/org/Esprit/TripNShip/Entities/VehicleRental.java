package org.Esprit.TripNShip.Entities;

import java.time.LocalDateTime;


public class VehicleRental {

    private int idRental;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private float totalPrice;
    private StautCircuit statusCircuit;
    private int idVehicle;

    public VehicleRental(LocalDateTime startDate, LocalDateTime endDate, float totalPrice, StautCircuit statusCircuit, int idVehicle) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.statusCircuit = statusCircuit;
        this.idVehicle = idVehicle;
    }


    public VehicleRental(int idRental, LocalDateTime startDate, LocalDateTime endDate, float totalPrice, StautCircuit statusCircuit, int idVehicle) {
        this.idRental = idRental;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.statusCircuit = statusCircuit;
        this.idVehicle = idVehicle;
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

    public int getIdVehicle() {
        return idVehicle;
    }

    public void setIdVehicle(int idVehicle) {
        this.idVehicle = idVehicle;
    }

    @Override
    public String toString() {
        return "VehicleRental{" +
                "idRental=" + idRental +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", totalPrice=" + totalPrice +
                ", statusCircuit=" + statusCircuit +
                ", idVehicle=" + idVehicle +
                '}';
    }
}
