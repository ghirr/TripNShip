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

    public Employee(String firstname, String lastname, Role role, String email, String address, String password,double salary) {
        super(firstname, lastname, role, email,password);
        this.address = address;
        this.salary = salary;
        this.hireDate = LocalDateTime.now();
    }

    public Employee(int idUser, String firstName, String lastName, Gender gender, Role role, String email, String profilePhoto, LocalDateTime birthdayDate, String phoneNumber, String address, double salary, LocalDateTime hireDate) {
        super(idUser, firstName, lastName, gender, role, email, profilePhoto, birthdayDate, phoneNumber);
        this.address = address;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    public Employee(int idUser, String profilePhoto) {
        super(idUser, profilePhoto);
    }

    public Employee(int idUser, String firstName, String lastName, Role role, String email, String address, double salary) {
        super(idUser, firstName, lastName, role, email);
        this.address = address;
        this.salary = salary;
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
