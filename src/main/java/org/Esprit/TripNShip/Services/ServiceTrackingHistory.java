package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.Esprit.TripNShip.Utils.Shared.getEnumOrNull;

public class ServiceTrackingHistory {

    private final Connection connection;

    public ServiceTrackingHistory() {
        connection = MyDataBase.getInstance().getConnection();
    }

    public void addTrackingEntry(TrackingHistory tracking) {
        String query = "INSERT INTO tracking_history (expeditionId, status, locationNote, timestamp, updatedBy) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, tracking.getExpedition().getExpeditionId());
            pst.setString(2, tracking.getStatus().name());
            pst.setString(3, tracking.getLocationNote());
            pst.setTimestamp(4, Timestamp.valueOf(tracking.getTimestamp()));
            pst.setInt(5, tracking.getUpdatedBy().getIdUser());

            pst.executeUpdate();
            System.out.println("Tracking entry added!");
        } catch (SQLException e) {
            System.out.println("Error while adding tracking entry: " + e.getMessage());
        }
    }

    public List<TrackingHistory> getTrackingByExpedition(int expeditionId) {
        List<TrackingHistory> list = new ArrayList<>();
        String query = "SELECT th.*, u.firstName, u.lastName, u.gender, u.email, u.password, u.profilePhoto, u.birthdayDate, u.phoneNumber, u.transportType " +
                "FROM tracking_history th " +
                "JOIN user u ON th.updatedBy = u.id " +
                "WHERE th.expeditionId = ? " +
                "ORDER BY th.timestamp ASC";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, expeditionId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Transporter transporter = new Transporter(
                        rs.getInt("updatedBy"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        getEnumOrNull(Gender.class, rs.getString("gender")),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("profilePhoto"),
                        rs.getTimestamp("birthdayDate").toLocalDateTime(),
                        rs.getString("phoneNumber"),
                        TransportType.valueOf(rs.getString("transportType"))
                );

                TrackingHistory tracking = new TrackingHistory(
                        rs.getInt("idTracking"),
                        new Expedition(expeditionId, null, 0, null, null, 0, null, null, null, null, null, null), // partial stub
                        PackageStatus.valueOf(rs.getString("status")),
                        rs.getString("locationNote"),
                        rs.getTimestamp("timestamp").toLocalDateTime(),
                        transporter
                );

                list.add(tracking);
            }

        } catch (SQLException e) {
            System.out.println("Error while retrieving tracking history: " + e.getMessage());
        }

        return list;
    }
    public boolean updateTrackingEntry(TrackingHistory tracking) {
        String query = "UPDATE tracking_history SET status = ?, locationNote = ? WHERE idTracking = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, tracking.getStatus().name());
            pst.setString(2, tracking.getLocationNote());
            pst.setInt(3, tracking.getId());

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error while updating tracking entry: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTrackingEntry(int trackingId) {
        String query = "DELETE FROM tracking_history WHERE idTracking = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, trackingId);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error while deleting tracking entry: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Utility method to get a complete expedition object for a tracking entry
    public Expedition getExpeditionForTracking(int expeditionId) {
        ServiceExpedition expeditionService = new ServiceExpedition();
        return expeditionService.getById(expeditionId);
    }
}
