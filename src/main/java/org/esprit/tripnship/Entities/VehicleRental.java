package org.esprit.tripnship.Entities;

import java.time.LocalDateTime;


public class VehicleRental {

    private int idRental;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private float totalPrice;
    private Status status;
    private int idVehicle;

    public VehicleRental(LocalDateTime startDate, LocalDateTime endDate, float totalPrice, Status status, int idVehicle) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.idVehicle = idVehicle;
    }


    public VehicleRental(int idRental, LocalDateTime startDate, LocalDateTime endDate, float totalPrice, Status status, int idVehicle) {
        this.idRental = idRental;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = status;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
                ", status=" + status +
                ", idVehicle=" + idVehicle +
                '}';
    }
}
