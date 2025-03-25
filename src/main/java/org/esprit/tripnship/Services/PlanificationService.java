package org.esprit.tripnship.Services;

import org.esprit.tripnship.Entities.Planification;
import org.esprit.tripnship.Utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanificationService implements IService<Planification>{
    private final Connection connection ;
    public PlanificationService(){ connection = MyDataBase.getInstance().getConnection();
    }
    @Override
    public void add(Planification planification) {
        String req = "INSERT INTO planification (planificationId,itineraryId,departureDate,arrivalDate) VALUES (?,?,?,?)";
        try{
            PreparedStatement pst= connection.prepareStatement(req);
            pst.setInt(1,planification.getPlanificationId());
            pst.setInt(2,planification.getItineraryId());
            pst.setDate(3,Date.valueOf(planification.getDepartureDate()));
            pst.setDate(4, Date.valueOf(planification.getArrivalDate()));
            pst.executeUpdate();
            System.out.println("Planification Added");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void update(Planification planification) {
        String req ="UPDATE planification set itineraryId=?, departureDate=?, arrivalDate =?, WHERE planificationId =?";
        try{
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1,planification.getItineraryId());
            pst.setDate(2, Date.valueOf(planification.getDepartureDate()));
            pst.setDate(3, Date.valueOf(planification.getArrivalDate()));
            pst.setInt(4,planification.getPlanificationId());
            pst.executeUpdate();
            System.out.println("Planification UPDATED!!");
        }

            catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

    @Override
    public void delete(Planification planification) {
        String req = "DELETE FROM planification WHERE planificationId=?";
        try{
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1,planification.getPlanificationId());
            pst.executeUpdate();
            System.out.println("planification deleted!!");
             }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Planification> getAll() {
        List<Planification> planifications = new ArrayList<>();
        String req = "SELECT * FROM planification";
        try{
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs= pst.executeQuery();
            while (rs.next()){
                planifications.add(new Planification(rs.getInt("planificationId"),rs.getInt("itineraryId"), rs.getDate("departureDate").toLocalDate(), rs.getDate("arrivalDate").toLocalDate()));
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return planifications;
    }
}
