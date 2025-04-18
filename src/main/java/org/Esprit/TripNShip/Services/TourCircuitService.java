package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.TourCircuit;
import org.Esprit.TripNShip.Utils.MyDataBase;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TourCircuitService implements IService<TourCircuit> {

    private final Connection connection;

    public TourCircuitService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(TourCircuit tourCircuit) {
        String req = "INSERT INTO tourcircuit(nameCircuit, descriptionCircuit, priceCircuit, duration, destination, guideIncluded) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, tourCircuit.getNameCircuit());
            pst.setString(2, tourCircuit.getDescriptionCircuit());
            pst.setFloat(3, tourCircuit.getPriceCircuit());
            pst.setString(4, tourCircuit.getDuration());
            pst.setString(5, tourCircuit.getDestination());
            pst.setBoolean(6, tourCircuit.getGuideIncluded());
            pst.executeUpdate();
            System.out.println("Tour circuit added!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(TourCircuit tourCircuit) {
        String req = "UPDATE tourcircuit SET nameCircuit=?, descriptionCircuit=?, priceCircuit=?, duration=?, destination=?, guideIncluded=? WHERE idCircuit=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, tourCircuit.getNameCircuit());
            pst.setString(2, tourCircuit.getDescriptionCircuit());
            pst.setFloat(3, tourCircuit.getPriceCircuit());
            pst.setString(4, tourCircuit.getDuration());
            pst.setString(5, tourCircuit.getDestination());
            pst.setBoolean(6, tourCircuit.getGuideIncluded());
            pst.setInt(7, tourCircuit.getIdCircuit());
            pst.executeUpdate();
            System.out.println("Tour circuit updated!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(TourCircuit tourCircuit) {
        String req = "DELETE FROM tourcircuit WHERE idCircuit=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, tourCircuit.getIdCircuit());
            pst.executeUpdate();
            System.out.println("Tour circuit deleted!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<TourCircuit> getAll() {
        List<TourCircuit> tourCircuits = new ArrayList<>();
        String req = "SELECT * FROM tourcircuit";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                TourCircuit tourCircuit = new TourCircuit(
                        rs.getInt("idCircuit"),
                        rs.getString("nameCircuit"),
                        rs.getString("descriptionCircuit"),
                        rs.getFloat("priceCircuit"),
                        rs.getString("duration"),
                        rs.getString("destination"),
                        rs.getBoolean("guideIncluded"),
                        new ArrayList<>()
                );
                tourCircuits.add(tourCircuit);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return tourCircuits;
    }

}
