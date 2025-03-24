package org.esprit.tripnship.Services;

import org.esprit.tripnship.Entities.Status;
import org.esprit.tripnship.Entities.Ticket;
import org.esprit.tripnship.Utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class TicketService implements IService<Ticket>{
    private final Connection connection ;

    public TicketService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    public void add(Ticket ticket) {
        String req = "INSERT INTO ticket (ticketId, planificationId, userId,status) VALUES (?,?,?,?)";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1,ticket.getTicketId());
            pst.setInt(2,ticket.getPlanificationId());
            pst.setInt(3,ticket.getUserId());
            pst.setString(4,ticket.getStatus().toString());
            pst.executeUpdate();
            System.out.println("Ticket added successfully !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Ticket ticket) {
        String req = "UPDATE ticket SET userId=?, "+ "planificationId=? ,"+ "status= ? ,"+ " WHERE ticketId= ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1,ticket.getUserId());
            pst.setInt(2,ticket.getPlanificationId());
            pst.setString(3,ticket.getStatus().toString());
            pst.setInt(4,ticket.getTicketId());
            pst.executeUpdate(req);
            System.out.println("Ticket updated successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



    @Override
    public void delete(Ticket ticket) {
        String req = "DELETE FROM ticket WHERE ticketId=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1,ticket.getTicketId());
            pst.executeUpdate(req);
            System.out.println("Ticket deleted successfully !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public List<Ticket> getAll() {

            List<Ticket> tickets = new ArrayList<>();

            String req = "SELECT * FROM ticket";
            try {
                PreparedStatement pst = connection.prepareStatement(req);
                ResultSet rs = pst.executeQuery(req);
                while (rs.next()) {
                    tickets.add(new Ticket(rs.getInt("ticketId"), rs.getInt("userId"), rs.getInt("planificationId"), Status.valueOf(rs.getString("status"))));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            return tickets;


    }


}
