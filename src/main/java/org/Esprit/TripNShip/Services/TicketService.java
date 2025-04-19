package org.Esprit.TripNShip.Services;

import com.mysql.cj.ServerPreparedQuery;
import org.Esprit.TripNShip.Entities.Ticket;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TicketService implements IService<Ticket>{
    private final Connection connection;

    public TicketService() {connection= MyDataBase.getInstance().getConnection(); }

    @Override
    public void add(Ticket ticket) {
        String req = "insert into ticket (ticketId,itineraryId,userId,departureDate,arrivalDate,price) values (?,?,?,?,?,?) ";
        try{
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1,ticket.getTicketId());
            pst.setString(2, ticket.getItineraryId());
            pst.setInt(3,ticket.getUserId());
            pst.setDate(4, Date.valueOf(ticket.getDepartureDate()));
            pst.setDate(5,Date.valueOf(ticket.getArrivalDate()));
            pst.setDouble(6,ticket.getPrice());
            pst.executeUpdate();
            System.out.println("Ticket Added Successfully!!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void update(Ticket ticket) {
        String req =" update ticket set itineraryId=?,userId=?,departureDate=?,arrivalDate=?,price=? where ticketId=?";
        try{
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, ticket.getItineraryId());
            pst.setInt(2,ticket.getUserId());
            pst.setDate(3,Date.valueOf(ticket.getDepartureDate()));
            pst.setDate(4,Date.valueOf(ticket.getArrivalDate()));
            pst.setDouble(5,ticket.getPrice());
            pst.setInt(6,ticket.getTicketId());
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
                tickets.add(new Ticket(rs.getInt("ticketId"),rs.getString("itineraryId"),rs.getInt("userId"), rs.getDate("departureDate").toLocalDate(), rs.getDate("arrivalDate").toLocalDate(),rs.getDouble("price")));
            }

        } catch (Exception e) {
            System.out.println("Ticket deleted successfully!!");
        }
        return tickets;
    }
}
