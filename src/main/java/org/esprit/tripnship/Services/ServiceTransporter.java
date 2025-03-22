package org.esprit.tripnship.Services;

import org.esprit.tripnship.Entities.Transporter;
import org.esprit.tripnship.Services.IService;
import org.esprit.tripnship.Utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceTransporter implements IService<Transporter> {

    private Connection connection;

    public ServiceTransporter() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Transporter transporteur) {
        String req = "INSERT INTO transporteurs (nom, typeTransport, telephone, email, siteWeb) VALUES ('" +
                transporteur.getNom() + "', '" +
                transporteur.getTypeTransport() + "', '" +
                transporteur.getTelephone() + "', '" +
                transporteur.getEmail() + "', '" +
                transporteur.getSiteWeb() + "')";
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Transporteur ajouté !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Transporter transporteur) {
        String req = "UPDATE transporteurs SET nom='" + transporteur.getNom() +
                "', typeTransport='" + transporteur.getTypeTransport() +
                "', telephone='" + transporteur.getTelephone() +
                "', email='" + transporteur.getEmail() +
                "', siteWeb='" + transporteur.getSiteWeb() +
                "' WHERE idTransporteur=" + transporteur.getIdTransporteur();
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Transporteur modifié !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Transporter transporteur) {
        String req = "DELETE FROM transporteurs WHERE idTransporteur=" + transporteur.getIdTransporteur();
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Transporteur supprimé !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Transporter> getAll() {
        List<Transporter> transporteurs = new ArrayList<>();

        String req = "SELECT * FROM transporteurs";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                transporteurs.add(new Transporter(
                        rs.getInt("idTransporteur"),
                        rs.getString("nom"),
                        rs.getString("typeTransport"),
                        rs.getString("telephone"),
                        rs.getString("email"),
                        rs.getString("siteWeb")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return transporteurs;
    }
}
