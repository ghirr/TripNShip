package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.Asset;
import org.Esprit.TripNShip.Entities.Employee;
import org.Esprit.TripNShip.Entities.TypeAsset;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssetsService implements IService<Asset> {

    private final Connection connection ;

    public AssetsService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Asset asset) {
        String req = "INSERT INTO asset (\n" +
                " idAsset , type, brand, registrationNumber, image, idEmployee)"+
                " VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, asset.getIdAsset());
            ps.setString(2, asset.getTypeAsset().toString());
            ps.setString(3,asset.getBrand());
            ps.setString(4, asset.getRegistrationNumber());
            ps.setString(5, asset.getAssetImage());
            ps.setInt(6,asset.getEmployee().getIdUser());
            ps.executeUpdate();
            System.out.println("Asset added !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Asset asset) {
        String req = "UPDATE asset SET " +
                "type = ?, " +
                "brand = ?, " +
                "registrationNumber = ?, " +
                "idEmployee = ?, " +
                "image = ? " +
                "WHERE idAsset = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1, asset.getTypeAsset().toString());
            ps.setString(2, asset.getBrand());
            ps.setString(3,asset.getRegistrationNumber());
            ps.setInt(4, asset.getEmployee().getIdUser());
            ps.setString(5, asset.getAssetImage());
            ps.setInt(6, asset.getIdAsset());
            ps.executeUpdate();
            System.out.println("Asset updated !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Asset asset) {
        String req = "DELETE FROM asset WHERE idAsset=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, asset.getIdAsset());
            pst.executeUpdate();
            System.out.println("Asset deleted successfully !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public List<Asset> getAll() {

        List<Asset> assets = new ArrayList<>();

        String req = "SELECT asset.idAsset, asset.type, asset.brand, asset.registrationNumber, asset.price, asset.image , " +
                "user.id , user.profilePhoto " +
                "FROM asset " +
                "JOIN user ON asset.idEmployee = user.id ;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                assets.add(new Asset(
                        rs.getInt("idAsset"),
                        new Employee(rs.getInt("id"),rs.getString("profilePhoto")),
                        TypeAsset.valueOf(rs.getString("type")),
                        rs.getString("brand"),
                        rs.getString("registrationNumber"),
                        rs.getDouble("price"),
                        rs.getString("image"))
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return assets;
    }
}
