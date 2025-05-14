package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.Transport;
import org.Esprit.TripNShip.Entities.TransportType;
import org.Esprit.TripNShip.Utils.MyDataBase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransportService implements IService<Transport>{
    private final Connection connection;

    public TransportService() {connection= MyDataBase.getInstance().getConnection(); }

    @Override
    public void add(Transport transport) {
        String req = "INSERT INTO transport (logoPath,transporterReference,transportation,companyName,companyPhone,companyEmail,companyWebsite) VALUES (?,?,?,?,?,?,?) ";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, transport.getLogoPath());
            pst.setString(2, transport.getTransporterReference());
            pst.setString(3,transport.getTransportation().toString());
            pst.setString(4,transport.getCompanyName());
            pst.setInt(5,transport.getCompanyPhone());
            pst.setString(6,transport.getCompanyEmail());
            pst.setString(7,transport.getCompanyWebsite());
            pst.executeUpdate();
            System.out.println("Transport added!!");
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void update(Transport transport) {
        String req = "UPDATE transport set transporterReference=?,transportation=?,companyName= ?,companyPhone= ?,companyEmail= ?,companyWebsite =? WHERE transportId=?";
        try{
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, transport.getTransporterReference());
            pst.setString(2,transport.getTransportation().toString());
            pst.setString(3,transport.getCompanyName());
            pst.setInt(4,transport.getCompanyPhone());
            pst.setString(5,transport.getCompanyEmail());
            pst.setString(6, transport.getCompanyWebsite());
            pst.setInt(7,transport.getTransportId());
            pst.executeUpdate();
            System.out.println("Transport Updated");

        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void delete(Transport transport) {
        String req = "DELETE FROM transport WHERE transportId=?";
        try{
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1,transport.getTransportId());
            pst.executeUpdate();
            System.out.println("Transport deleted!!");
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }

    @Override
    public List<Transport> getAll() {
        List<Transport> transports = new ArrayList<>();
        String req = "SELECT * FROM transport";
        try{
            PreparedStatement pst= connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                transports.add(new Transport(rs.getInt("transportId"),rs.getString("logoPath"),rs.getString("transporterReference"),TransportType.valueOf(rs.getString("transportation").toUpperCase()), rs.getString("companyName"),rs.getInt("companyPhone"),rs.getString("companyEmail"),rs.getString("companyWebsite")));
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return transports;
    }
}

