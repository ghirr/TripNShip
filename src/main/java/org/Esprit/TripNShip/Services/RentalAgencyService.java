package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.RentalAgency;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalAgencyService implements IService<RentalAgency> {

    private final Connection connection;

    public RentalAgencyService() {
        connection = MyDataBase.getInstance().getConnection();
    }

     @Override
    public void add(RentalAgency rentalAgency) {
        String req = "INSERT INTO rentalagency(nameAgency, addressAgency, contactAgency, rating) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            pst.setFloat(4, rentalAgency.getRating());
            pst.setString(3, rentalAgency.getContactAgency());
            pst.setString(2, rentalAgency.getAddressAgency());
            pst.setString(1, rentalAgency.getNameAgency());
            pst.executeUpdate();
            // 🔽 Récupération de l’ID généré
            ResultSet res = pst.getGeneratedKeys();
            if (res.next()) {
                int generatedId = res.getInt(1);
                rentalAgency.setIdAgency(generatedId); // Mise à jour de l'objet Java avec l'ID généré
            }
            System.out.println("Rental Agency added !");

        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
     }

    @Override
    public void update(RentalAgency rentalAgency) {
        String req = "UPDATE rentalagency SET nameAgency=?, addressAgency=?, contactAgency=?, rating=? WHERE idAgency=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, rentalAgency.getNameAgency());
            pst.setString(2, rentalAgency.getAddressAgency());
            pst.setString(3, rentalAgency.getContactAgency());
            pst.setFloat(4, rentalAgency.getRating());
            pst.setInt(5, rentalAgency.getIdAgency());
            pst.executeUpdate();
            System.out.println("Rental Agency Updated !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void delete(RentalAgency rentalAgency) {
        String req = "DELETE FROM rentalagency WHERE idAgency=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, rentalAgency.getIdAgency());
            pst.executeUpdate();
            System.out.println("Rental Agency deleted !");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<RentalAgency> getAll() {
        List<RentalAgency> rentalAgencies = new ArrayList<>();
        String req = "SELECT * FROM rentalagency";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()){
                rentalAgencies.add(new RentalAgency(rs.getInt("idAgency"),rs.getString("nameAgency"), rs.getString("addressAgency"), rs.getString("contactAgency"), rs.getFloat("rating")));
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return rentalAgencies;
    }

}
