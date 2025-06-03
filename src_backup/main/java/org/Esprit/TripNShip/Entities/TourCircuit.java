package org.Esprit.TripNShip.Entities;


import java.util.ArrayList;
import java.util.List;

public class TourCircuit {

    private int idCircuit;
    private String nameCircuit;
    private String descriptionCircuit;
    private float priceCircuit;
    private String duration;
    private String destination;
    private GuideIncluded guideIncluded;
    private List<TourCircuit> circuitList;


    public TourCircuit(int idCircuit, String nameCircuit, String descriptionCircuit, float priceCircuit, String duration, String destination, GuideIncluded guideIncluded, List<TourCircuit> circuitList) {
        this.idCircuit = idCircuit;
        this.nameCircuit = nameCircuit;
        this.descriptionCircuit = descriptionCircuit;
        this.priceCircuit = priceCircuit;
        this.duration = duration;
        this.destination = destination;
        this.guideIncluded = guideIncluded;
        this.circuitList = new ArrayList<>();
    }

    public TourCircuit(String nameCircuit, String descriptionCircuit, float priceCircuit, String duration, String destination, GuideIncluded guideIncluded, List<TourCircuit> circuitList) {
        this.nameCircuit = nameCircuit;
        this.descriptionCircuit = descriptionCircuit;
        this.priceCircuit = priceCircuit;
        this.duration = duration;
        this.destination = destination;
        this.guideIncluded = guideIncluded;
        this.circuitList = circuitList;
    }

    public int getIdCircuit() {
        return idCircuit;
    }

    public void setIdCircuit(int idCircuit) {
        this.idCircuit = idCircuit;
    }

    public String getNameCircuit() {
        return nameCircuit;
    }

    public void setNameCircuit(String nameCircuit) {
        this.nameCircuit = nameCircuit;
    }

    public String getDescriptionCircuit() {
        return descriptionCircuit;
    }

    public void setDescriptionCircuit(String descriptionCircuit) {
        this.descriptionCircuit = descriptionCircuit;
    }

    public float getPriceCircuit() {
        return priceCircuit;
    }

    public void setPriceCircuit(float priceCircuit) {
        this.priceCircuit = priceCircuit;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public GuideIncluded getGuideIncluded() {
        return guideIncluded;
    }

    public void setGuideIncluded(GuideIncluded guideIncluded) {
        this.guideIncluded = guideIncluded;
    }

    public List<TourCircuit> getCircuitList() {
        return circuitList;
    }

    public void setCircuitList(List<TourCircuit> circuitList) {
        this.circuitList = circuitList;
    }

    @Override
    public String toString() {
        return "TourCircuit{" +
                "idCircuit=" + idCircuit +
                ", nameCircuit='" + nameCircuit + '\'' +
                ", descriptionCircuit='" + descriptionCircuit + '\'' +
                ", priceCircuit=" + priceCircuit +
                ", duration='" + duration + '\'' +
                ", destination='" + destination + '\'' +
                ", guideIncluded=" + guideIncluded +
                ", circuitList=" + circuitList +
                '}';
    }
}
