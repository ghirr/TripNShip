package org.Esprit.TripNShip.Entities;

public class Asset {

    private int idAsset;
    private Employee employee;
    private  TypeAsset typeAsset;
    private String brand;
    private String registrationNumber;
    private Double price;
    private String assetImage;

    public Asset(int idAsset, Employee employee, TypeAsset typeAsset, String brand, String registrationNumber, Double price, String assetImage) {
        this.idAsset = idAsset;
        this.employee = employee;
        this.typeAsset = typeAsset;
        this.brand = brand;
        this.registrationNumber = registrationNumber;
        this.price = price;
        this.assetImage = assetImage;
    }

    public Asset(Employee employee, TypeAsset typeAsset, String brand, String registrationNumber, Double price, String assetImage) {
        this.employee = employee;
        this.typeAsset = typeAsset;
        this.brand = brand;
        this.registrationNumber = registrationNumber;
        this.price = price;
        this.assetImage = assetImage;
    }

    public int getIdAsset() {
        return idAsset;
    }

    public void setIdAsset(int idAsset) {
        this.idAsset = idAsset;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public TypeAsset getTypeAsset() {
        return typeAsset;
    }

    public void setTypeAsset(TypeAsset typeAsset) {
        this.typeAsset = typeAsset;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getAssetImage() {
        return assetImage;
    }

    public void setAssetImage(String assetImage) {
        this.assetImage = assetImage;
    }

    @Override
    public String toString() {
        return "Asset{" +
                "idAsset=" + idAsset +
                ", employeeID=" + employee.getIdUser() +
                ", employeePhoto=" + employee.getProfilePhoto() +
                ", typeAsset=" + typeAsset +
                ", brand='" + brand + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", price=" + price +
                ", assetImage='" + assetImage + '\'' +
                '}';
    }
}
