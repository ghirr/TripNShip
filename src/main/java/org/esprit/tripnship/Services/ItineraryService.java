package org.esprit.tripnship.Services;

import org.esprit.tripnship.Entities.Itinerary;
import org.esprit.tripnship.Utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItineraryService implements IService<Itinerary>{
    private final Connection connection;
    public ItineraryService() { connection=MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Itinerary itinerary) {
        String req = "INSERT INTO itinerary (itineraryId, transportId, departureLocation,arrivalLocation,duration,price) VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement pst= connection.prepareStatement(req);
            pst.setInt(1,itinerary.getItineraryId());
            pst.setInt(2,itinerary.getTransportID());
            pst.setString(3,itinerary.getDepartureLocation());
            pst.setString(4, itinerary.getArrivalLocation());
            pst.setString(5,itinerary.getDuration().toString());   //a verifier pour Duration
            pst.setDouble(6,itinerary.getPrice());
            pst.executeUpdate();
            System.out.println("Itinerary added successfully!!");
            }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Itinerary itinerary) {
        String req = "UPDATE itinerary SET transportId=?,departureLocation=?,arrivalLocation=?,duration=?,price=? WHERE itineraryId=?";
        try{
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1,itinerary.getTransportID());
            pst.setString(2, itinerary.getDepartureLocation());
            pst.setString(3, itinerary.getArrivalLocation());
            pst.setString(4,itinerary.getDuration().toString()); //a verifier pour Duration
            pst.setDouble(5,itinerary.getPrice());
            pst.setInt(6,itinerary.getItineraryId());
            pst.executeUpdate();
            System.out.println("Itinerary updated successfully");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void delete(Itinerary itinerary) {
        String req = "DELETE FROM itinerary WHERE itineraryId=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1,itinerary.getItineraryId());
            pst.executeUpdate();
            System.out.println("Itinerary Deleted Successfully");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public List<Itinerary> getAll() {
        List<Itinerary> itineraries = new ArrayList<>();
        String req = "SELECT * FROM itinerary";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                itineraries.add(new Itinerary(rs.getInt("itineraryId"), rs.getInt("transportId"), rs.getString("departureLocation"), rs.getString("arrivalLocation"), rs.getString("duration"), rs.getDouble("price"))); //a verifier pour Duration

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return itineraries;


    }
}
