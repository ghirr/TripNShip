package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.Itinerary;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItineraryService implements IService<Itinerary>{
    private List<Itinerary> itineraries;
    private final Connection connection;
    public ItineraryService() { connection=MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Itinerary itinerary) {
        String req = "INSERT INTO itinerary (itineraryCode, transporterReference, departureLocation,departureTime,arrivalLocation,arrivalTime,duration,price) VALUES (?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement pst= connection.prepareStatement(req);
            pst.setString(1,itinerary.getItineraryCode());
            pst.setString(2,itinerary.getTransporterReference());
            pst.setString(3,itinerary.getDepartureLocation());
            pst.setTime(4, Time.valueOf(itinerary.getDepartureTime()));
            pst.setString(5, itinerary.getArrivalLocation());
            pst.setTime(6,Time.valueOf(itinerary.getArrivalTime()));
            pst.setString(7,itinerary.getDuration());
            pst.setDouble(8,itinerary.getPrice());//a verifier pour Duration
            pst.executeUpdate();
            System.out.println("Itinerary added successfully!!");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Itinerary itinerary) {
        String req = "UPDATE itinerary SET itineraryCode=?,transporterReference=?,departureLocation=?,departureTime=?,arrivalLocation=?,arrivalTime=?,duration=?,price=? WHERE itineraryId=?";
        try{
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, itinerary.getItineraryCode());
            pst.setString(2,itinerary.getTransporterReference());
            pst.setString(3, itinerary.getDepartureLocation());
            pst.setTime(4,Time.valueOf(itinerary.getDepartureTime()));
            pst.setString(5, itinerary.getArrivalLocation());
            pst.setTime(6,Time.valueOf(itinerary.getArrivalTime()));
            pst.setString(7,itinerary.getDuration());
            pst.setDouble(8,itinerary.getPrice());//a verifier pour Duration
            pst.setInt(9,itinerary.getItineraryId());
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
                itineraries.add(new Itinerary(rs.getInt("itineraryId"),rs.getString("itineraryCode"), rs.getString("transporterReference"), rs.getString("departureLocation"),rs.getTime("departureTime").toLocalTime(), rs.getString("arrivalLocation"), rs.getTime("arrivalTime").toLocalTime(),rs.getString("duration"),rs.getDouble("price"))); //a verifier pour Duration

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return itineraries;
    }

    public Itinerary getItineraryByCode(String code){
        String req = "SELECT * FROM itinerary WHERE itineraryCode = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, code);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Itinerary(
                        rs.getString("itineraryCode"),
                        rs.getString("transporterReference"),
                        rs.getString("departureLocation"),
                        rs.getTime("departureTime").toLocalTime(),
                        rs.getString("arrivalLocation"),
                        rs.getTime("arrivalTime").toLocalTime(),
                        rs.getString("duration"),
                        rs.getDouble("price")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
