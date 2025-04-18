package org.Esprit.TripNShip.Services;


import org.Esprit.TripNShip.Entities.Expedition;
import org.Esprit.TripNShip.Entities.PackageStatus;
import org.Esprit.TripNShip.Entities.PackageType;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceExpedition implements IService<Expedition> {

    private Connection connection;

    public ServiceExpedition() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Expedition expedition) {
        String query = "INSERT INTO expeditions (carrierId, weight, packageType, packageStatus, shippingCost, " +
                "sendDate, estimatedDeliveryDate, departureCity, arrivalCity, currentLocation, lastUpdated) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, expedition.getCarrierId());
            pst.setDouble(2, expedition.getWeight());
            pst.setString(3, expedition.getPackageType().name());
            pst.setString(4, expedition.getPackageStatus().name());
            pst.setDouble(5, expedition.getShippingCost());
            pst.setDate(6, new Date(expedition.getSendDate().getTime()));
            pst.setDate(7, new Date(expedition.getEstimatedDeliveryDate().getTime()));
            pst.setString(8, expedition.getDepartureCity());
            pst.setString(9, expedition.getArrivalCity());
            pst.setString(10, expedition.getCurrentLocation());
            pst.setDate(11, new Date(expedition.getLastUpdated().getTime()));

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Expedition successfully added!");
            }
        } catch (SQLException e) {
            System.out.println("Error while adding expedition: " + e.getMessage());
        }
    }

    @Override
    public void update(Expedition expedition) {
        String query = "UPDATE expeditions SET carrierId=?, weight=?, packageType=?, packageStatus=?, " +
                "shippingCost=?, sendDate=?, estimatedDeliveryDate=?, departureCity=?, arrivalCity=?, " +
                "currentLocation=?, lastUpdated=? WHERE expeditionId=?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, expedition.getCarrierId());
            pst.setDouble(2, expedition.getWeight());
            pst.setString(3, expedition.getPackageType().name());
            pst.setString(4, expedition.getPackageStatus().name());
            pst.setDouble(5, expedition.getShippingCost());
            pst.setDate(6, new Date(expedition.getSendDate().getTime()));
            pst.setDate(7, new Date(expedition.getEstimatedDeliveryDate().getTime()));
            pst.setString(8, expedition.getDepartureCity());
            pst.setString(9, expedition.getArrivalCity());
            pst.setString(10, expedition.getCurrentLocation());
            pst.setDate(11, new Date(expedition.getLastUpdated().getTime()));
            pst.setInt(12, expedition.getExpeditionId());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Expedition successfully updated!");
            }
        } catch (SQLException e) {
            System.out.println("Error while updating expedition: " + e.getMessage());
        }
    }

    @Override
    public void delete(Expedition expedition) {
        String query = "DELETE FROM expeditions WHERE expeditionId=?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, expedition.getExpeditionId());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Expedition successfully deleted!");
            }
        } catch (SQLException e) {
            System.out.println("Error while deleting expedition: " + e.getMessage());
        }
    }

    @Override
    public List<Expedition> getAll() {
        List<Expedition> expeditionList = new ArrayList<>();
        String query = "SELECT * FROM expeditions";

        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Expedition expedition = new Expedition(
                        rs.getInt("expeditionId"),
                        rs.getInt("carrierId"),
                        rs.getDouble("weight"),
                        PackageType.valueOf(rs.getString("packageType").toUpperCase()),
                        PackageStatus.valueOf(rs.getString("packageStatus").toUpperCase()),
                        rs.getDouble("shippingCost"),
                        rs.getDate("sendDate"),
                        rs.getDate("estimatedDeliveryDate"),
                        rs.getString("departureCity"),
                        rs.getString("arrivalCity"),
                        rs.getString("currentLocation"),
                        rs.getDate("lastUpdated")
                );
                expeditionList.add(expedition);
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving expeditions: " + e.getMessage());
        }
        return expeditionList;
    }
}
