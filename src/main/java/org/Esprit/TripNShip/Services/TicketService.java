package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.Ticket;
import org.Esprit.TripNShip.Utils.MyDataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TicketService implements IService<Ticket>{
    private final Connection connection;

    public TicketService() {connection= MyDataBase.getInstance().getConnection(); }

    @Override
    public void add(Ticket ticket) {
        String req = "insert into ticket (itineraryCode,userEmail,departureDate,arrivalDate) values (?,?,?,?) ";
        try{
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, ticket.getItineraryCode());
            pst.setString(2,ticket.getUserEmail());
            pst.setDate(3, Date.valueOf(ticket.getDepartureDate()));
            pst.setDate(4,Date.valueOf(ticket.getArrivalDate()));

            pst.executeUpdate();
            System.out.println("Ticket Added Successfully!!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Ticket ticket) {
        String req =" update ticket set itineraryCode=?,userEmail=?,departureDate=?,arrivalDate=? where ticketId=?";
        try{
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, ticket.getItineraryCode());
            pst.setString(2,ticket.getUserEmail());
            pst.setDate(3,Date.valueOf(ticket.getDepartureDate()));
            pst.setDate(4,Date.valueOf(ticket.getArrivalDate()));

            pst.setInt(5,ticket.getTicketId());
            pst.executeUpdate();
            System.out.println("Ticket updated successfully!!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Ticket ticket) {
        String req = "Delete from ticket where ticketId=?";
        try{
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1,ticket.getTicketId());
            pst.executeUpdate();
            System.out.println("Ticket Deleted Successfully!!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public List<Ticket> getAll() {
        List<Ticket> tickets= new ArrayList<>();
        String req = "Select * from ticket";
        try{
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs= pst.executeQuery();
            while (rs.next()){
                tickets.add(new Ticket(rs.getInt("ticketId"), rs.getString("itineraryCode"),rs.getString("userEmail"), rs.getDate("departureDate").toLocalDate(), rs.getDate("arrivalDate").toLocalDate()));
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return tickets;
    }

    public List<Ticket> getByUserEmail(String email) {
        return getAll().stream()
                .filter(ticket -> ticket.getUserEmail().equalsIgnoreCase(email))
                .collect(Collectors.toList());
    }
}

