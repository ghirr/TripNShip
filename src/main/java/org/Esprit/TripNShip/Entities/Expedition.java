package org.Esprit.TripNShip.Entities;

import java.util.Date;

public class Expedition {

    private int expeditionId;
    private int carrierId;
    private double weight;
    private PackageType packageType;
    private PackageStatus packageStatus;
    private double shippingCost;
    private Date sendDate;
    private Date estimatedDeliveryDate;
    private String departureCity;
    private String arrivalCity;
    private String currentLocation;
    private Date lastUpdated;

    // Full constructor
    public Expedition(int expeditionId, int carrierId, double weight, PackageType packageType,
                      PackageStatus packageStatus, double shippingCost, Date sendDate,
                      Date estimatedDeliveryDate, String departureCity, String arrivalCity,
                      String currentLocation, Date lastUpdated) {
        this.expeditionId = expeditionId;
        this.carrierId = carrierId;
        this.weight = weight;
        this.packageType = packageType;
        this.packageStatus = packageStatus;
        this.shippingCost = shippingCost;
        this.sendDate = sendDate;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.currentLocation = currentLocation;
        this.lastUpdated = lastUpdated;
    }

    // Constructor without ID
    public Expedition(int carrierId, double weight, PackageType packageType, PackageStatus packageStatus,
                      double shippingCost, Date sendDate, Date estimatedDeliveryDate,
                      String departureCity, String arrivalCity, String currentLocation) {
        this.carrierId = carrierId;
        this.weight = weight;
        this.packageType = packageType;
        this.packageStatus = packageStatus;
        this.shippingCost = shippingCost;
        this.sendDate = sendDate;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.currentLocation = currentLocation;
    }

    // Getters and Setters
    public int getExpeditionId() {
        return expeditionId;
    }

    public void setExpeditionId(int expeditionId) {
        this.expeditionId = expeditionId;
    }

    public int getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(int carrierId) {
        this.carrierId = carrierId;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public PackageType getPackageType() {
        return packageType;
    }

    public void setPackageType(PackageType packageType) {
        this.packageType = packageType;
    }

    public PackageStatus getPackageStatus() {
        return packageStatus;
    }

    public void setPackageStatus(PackageStatus packageStatus) {
        this.packageStatus = packageStatus;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Date getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(Date estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "Expedition{" +
                "expeditionId=" + expeditionId +
                ", carrierId=" + carrierId +
                ", weight=" + weight +
                ", packageType=" + packageType +
                ", packageStatus=" + packageStatus +
                ", shippingCost=" + shippingCost +
                ", sendDate=" + sendDate +
                ", estimatedDeliveryDate=" + estimatedDeliveryDate +
                ", departureCity='" + departureCity + '\'' +
                ", arrivalCity='" + arrivalCity + '\'' +
                ", currentLocation='" + currentLocation + '\'' +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
