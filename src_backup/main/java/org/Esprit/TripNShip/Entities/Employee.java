package org.Esprit.TripNShip.Entities;

import java.time.LocalDateTime;

public sealed class Employee extends User permits TravelOrganizer,ShippingCoordinator,AccommodationSpecialist,TourCoordinator {

    private String address;
    private double salary;
    private LocalDateTime hireDate;

    public Employee(int idUser, String firstname, String lastname, Gender gender, Role role, String email, String password, String profilePhoto, LocalDateTime birthdayDate, String phoneNumber, String address, double salary, LocalDateTime hireDate) {
        super(idUser, firstname, lastname, gender, role, email, password, profilePhoto, birthdayDate, phoneNumber);
        this.address = address;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    public Employee(String firstname, String lastname, Gender gender, Role role, String email, String password, String profilePhoto, LocalDateTime birthdayDate, String phoneNumber, String address, double salary, LocalDateTime hireDate) {
        super(firstname, lastname, gender, role, email, password, profilePhoto, birthdayDate, phoneNumber);
        this.address = address;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    public Employee(int idUser, String profilePhoto) {
        super(idUser, profilePhoto);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public LocalDateTime getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDateTime hireDate) {
        this.hireDate = hireDate;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "address='" + address + '\'' +
                ", salary=" + salary +
                ", hireDate=" + hireDate +
                super.toString() + "} ";
    }
}
