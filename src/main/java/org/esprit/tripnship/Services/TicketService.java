package org.esprit.tripnship.Services;

import org.esprit.tripnship.Entities.Status;
import org.esprit.tripnship.Entities.Ticket;
import org.esprit.tripnship.Utils.MyDataBase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class TicketService implements IService<Ticket>{
    private final Connection connection ;

    public TicketService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    public void add(Ticket ticket) {
        String req = "INSERT INTO Ticket (ticketId, planificationId, userId,status) VALUES (' "+ ticket.getTicketId()+"',' " + ticket.getPlanificationId()+"' ,' "+ticket.getUserId()+"','"+ticket.getStatus()+"')";

        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Ticket added successfully !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Ticket ticket) {
        String req = "UPDATE ticket SET userId='"+ticket.getUserId()+"', planificationId='"+ ticket.getPlanificationId()+"',status='"+ticket.getStatus()+"' WHERE ticketId="+ticket.getTicketId();
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Ticket updated successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



    @Override
    public void delete(Ticket ticket) {
        String req = "DELETE FROM ticket WHERE ticketId="+ticket.getTicketId();
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
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
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(req);
                while (rs.next()) {
                    tickets.add(new Ticket(rs.getInt("ticketId"), rs.getInt("userId"), rs.getInt("planificationId"), Status.valueOf(rs.getString("status"))));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            return tickets;

       // return List.of();
    }


}
