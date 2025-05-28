package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceExpedition implements IService<Expedition> {

    private final Connection connection;

    public ServiceExpedition() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Expedition expedition) {
        String query = "INSERT INTO expeditions (transporterId, clientId, weight, packageType, packageStatus, shippingCost, " +
                "sendDate, estimatedDeliveryDate, departureCity, arrivalCity, currentLocation, lastUpdated) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Handle null transporter
            if (expedition.getTransporter() == null) {
                pst.setNull(1, java.sql.Types.INTEGER); // Use NULL for transporterId
            } else {
                pst.setInt(1, expedition.getTransporter().getIdUser());
            }

            pst.setInt(2, expedition.getClient().getIdUser());
            pst.setDouble(3, expedition.getWeight());
            pst.setString(4, expedition.getPackageType().name());
            pst.setString(5, expedition.getPackageStatus().name());
            pst.setDouble(6, expedition.getShippingCost());
            pst.setDate(7, new Date(expedition.getSendDate().getTime()));
            pst.setDate(8, new Date(expedition.getEstimatedDeliveryDate().getTime()));
            pst.setString(9, expedition.getDepartureCity());
            pst.setString(10, expedition.getArrivalCity());
            pst.setString(11, expedition.getCurrentLocation());
            pst.setDate(12, new Date(expedition.getLastUpdated().getTime()));

            pst.executeUpdate();

            // Get the generated ID
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                expedition.setExpeditionId(rs.getInt(1));
            }

            System.out.println("Expedition successfully added with ID: " + expedition.getExpeditionId());
        } catch (SQLException e) {
            System.out.println("Error while adding expedition: " + e.getMessage());
        }
    }

    @Override
    public void update(Expedition expedition) {
        String query = "UPDATE expeditions SET transporterId=?, clientId=?, weight=?, packageType=?, packageStatus=?, " +
                "shippingCost=?, sendDate=?, estimatedDeliveryDate=?, departureCity=?, arrivalCity=?, " +
                "currentLocation=?, lastUpdated=? WHERE idExpedition=?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            // Handle null transporter
            if (expedition.getTransporter() == null) {
                pst.setNull(1, java.sql.Types.INTEGER);
            } else {
                pst.setInt(1, expedition.getTransporter().getIdUser());
            }

            pst.setInt(2, expedition.getClient().getIdUser());
            pst.setDouble(3, expedition.getWeight());
            pst.setString(4, expedition.getPackageType().name());
            pst.setString(5, expedition.getPackageStatus().name());
            pst.setDouble(6, expedition.getShippingCost());
            pst.setDate(7, new Date(expedition.getSendDate().getTime()));
            pst.setDate(8, new Date(expedition.getEstimatedDeliveryDate().getTime()));
            pst.setString(9, expedition.getDepartureCity());
            pst.setString(10, expedition.getArrivalCity());
            pst.setString(11, expedition.getCurrentLocation());
            pst.setDate(12, new Date(expedition.getLastUpdated().getTime()));
            pst.setInt(13, expedition.getExpeditionId());

            pst.executeUpdate();
            System.out.println("Expedition successfully updated!");
        } catch (SQLException e) {
            System.out.println("Error while updating expedition: " + e.getMessage());
        }
    }

    @Override
    public void delete(Expedition expedition) {
        // Using the delete1 method implementation
        delete1(expedition);
    }

    public boolean update1(Expedition expedition) {
        String query = "UPDATE expeditions SET " +
                "clientId = ?, transporterId = ?, weight = ?, packageType = ?, " +
                "packageStatus = ?, shippingCost = ?, sendDate = ?, estimatedDeliveryDate = ?, " +
                "departureCity = ?, arrivalCity = ?, currentLocation = ?, lastUpdated = ? " +
                "WHERE idExpedition = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, expedition.getClient().getIdUser());

            // Handle null transporter
            if (expedition.getTransporter() == null) {
                pst.setNull(2, java.sql.Types.INTEGER);
            } else {
                pst.setInt(2, expedition.getTransporter().getIdUser());
            }

            pst.setDouble(3, expedition.getWeight());
            pst.setString(4, expedition.getPackageType().toString());
            pst.setString(5, expedition.getPackageStatus().toString());
            pst.setDouble(6, expedition.getShippingCost());
            pst.setDate(7, new java.sql.Date(expedition.getSendDate().getTime()));
            pst.setDate(8, new java.sql.Date(expedition.getEstimatedDeliveryDate().getTime()));
            pst.setString(9, expedition.getDepartureCity());
            pst.setString(10, expedition.getArrivalCity());
            pst.setString(11, expedition.getCurrentLocation());
            pst.setDate(12, new java.sql.Date(expedition.getLastUpdated().getTime()));
            pst.setInt(13, expedition.getExpeditionId());

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error while updating expedition: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete1(Expedition expedition) {
        String query = "DELETE FROM expeditions WHERE idExpedition=?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, expedition.getExpeditionId());
            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Expedition successfully deleted!");
                return true;
            } else {
                System.out.println("No expedition found with the given ID.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error while deleting expedition: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Expedition> getAll() {
        List<Expedition> expeditionList = new ArrayList<>();
        String query = "SELECT e.*, " +
                "c.firstName as clientFirstName, c.lastName as clientLastName, c.gender as clientGender, " +
                "c.email as clientEmail, c.password as clientPassword, c.profilePhoto as clientProfilePhoto, " +
                "c.birthdayDate as clientBirthdayDate, c.phoneNumber as clientPhoneNumber, " +
                "t.firstName as transporterFirstName, t.lastName as transporterLastName, t.gender as transporterGender, " +
                "t.email as transporterEmail, t.password as transporterPassword, t.profilePhoto as transporterProfilePhoto, " +
                "t.birthdayDate as transporterBirthdayDate, t.phoneNumber as transporterPhoneNumber, " +
                "t.transportType, t.website " +
                "FROM expeditions e " +
                "JOIN user c ON e.clientId = c.idUser AND c.role = 'CLIENT' " +
                "LEFT JOIN user t ON e.transporterId = t.idUser AND t.role = 'TRANSPORTER'"; // Changed to LEFT JOIN

        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                // Create Client object
                Client client = new Client(
                        rs.getInt("clientId"),
                        rs.getString("clientFirstName"),
                        rs.getString("clientLastName"),
                        Gender.valueOf(rs.getString("clientGender")),
                        rs.getString("clientEmail"),
                        rs.getString("clientPassword"),
                        rs.getString("clientProfilePhoto"),
                        rs.getTimestamp("clientBirthdayDate").toLocalDateTime(),
                        rs.getString("clientPhoneNumber")
                );

                // Create Transporter object if transporterId is not null
                Transporter transporter = null;
                if (rs.getObject("transporterId") != null) {
                    transporter = new Transporter(
                            rs.getInt("transporterId"),
                            rs.getString("transporterFirstName"),
                            rs.getString("transporterLastName"),
                            Gender.valueOf(rs.getString("transporterGender")),
                            rs.getString("transporterEmail"),
                            rs.getString("transporterPassword"),
                            rs.getString("transporterProfilePhoto"),
                            rs.getTimestamp("transporterBirthdayDate").toLocalDateTime(),
                            rs.getString("transporterPhoneNumber"),
                            TransportType.valueOf(rs.getString("transportType")),
                            rs.getString("website")
                    );
                }

                // Create Expedition object with Client and possibly null Transporter
                Expedition expedition = new Expedition(
                        rs.getInt("idExpedition"),
                        client,
                        transporter,
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

    public List<Expedition> getExpeditionsForClient(int clientId) {
        List<Expedition> expeditionList = new ArrayList<>();

        String query = "SELECT * FROM expeditions WHERE clientId = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, clientId);
            ResultSet rs = pst.executeQuery();

            UserService userService = new UserService();

            while (rs.next()) {
                try {
                    // Get the client
                    User clientUser = userService.getById(clientId);
                    Client client;

                    if (clientUser instanceof Client) {
                        client = (Client) clientUser;
                    } else {
                        client = new Client(
                                clientUser.getIdUser(),
                                clientUser.getFirstName(),
                                clientUser.getLastName(),
                                clientUser.getGender(),
                                clientUser.getEmail(),
                                clientUser.getPassword(),
                                clientUser.getProfilePhoto(),
                                clientUser.getBirthdayDate(),
                                clientUser.getPhoneNumber()
                        );
                    }

                    // Get the transporter if it exists
                    Transporter transporter = null;
                    if (rs.getObject("transporterId") != null) {
                        int transporterId = rs.getInt("transporterId");
                        User transporterUser = userService.getById(transporterId);

                        if (transporterUser instanceof Transporter) {
                            transporter = (Transporter) transporterUser;
                        } else if (transporterUser != null) {
                            // Default transporter values if we can't get the proper subclass
                            transporter = new Transporter(
                                    transporterUser.getIdUser(),
                                    transporterUser.getFirstName(),
                                    transporterUser.getLastName(),
                                    transporterUser.getGender(),
                                    transporterUser.getEmail(),
                                    transporterUser.getPassword(),
                                    transporterUser.getProfilePhoto(),
                                    transporterUser.getBirthdayDate(),
                                    transporterUser.getPhoneNumber(),
                                    TransportType.DHL,  // Default value
                                    "unknown"           // Default value
                            );
                        }
                    }

                    // Create Expedition object (with potentially null transporter)
                    Expedition expedition = new Expedition(
                            rs.getInt("idExpedition"),
                            client,
                            transporter,
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

                } catch (Exception e) {
                    System.out.println("Error processing expedition: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("Found " + expeditionList.size() + " expeditions for client " + clientId);

        } catch (SQLException e) {
            System.out.println("Error while retrieving client expeditions: " + e.getMessage());
            e.printStackTrace();
        }

        return expeditionList;
    }

    // Get expeditions for a specific transporter
    public List<Expedition> getExpeditionsForTransporter(int transporterId) {
        List<Expedition> expeditionList = new ArrayList<>();
        String query = "SELECT e.*, " +
                "c.firstName as clientFirstName, c.lastName as clientLastName, c.gender as clientGender, " +
                "c.email as clientEmail, c.password as clientPassword, c.profilePhoto as clientProfilePhoto, " +
                "c.birthdayDate as clientBirthdayDate, c.phoneNumber as clientPhoneNumber " +
                "FROM expeditions e " +
                "JOIN user c ON e.clientId = c.idUser AND c.role = 'CLIENT' " +
                "WHERE e.transporterId = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, transporterId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                // Create Client object
                Client client = new Client(
                        rs.getInt("clientId"),
                        rs.getString("clientFirstName"),
                        rs.getString("clientLastName"),
                        Gender.valueOf(rs.getString("clientGender")),
                        rs.getString("clientEmail"),
                        rs.getString("clientPassword"),
                        rs.getString("clientProfilePhoto"),
                        rs.getTimestamp("clientBirthdayDate").toLocalDateTime(),
                        rs.getString("clientPhoneNumber")
                );

                // Get transporter from service
                UserService serviceUser = new UserService();
                Transporter transporter = (Transporter) serviceUser.getById(transporterId);

                // Create Expedition object
                Expedition expedition = new Expedition(
                        rs.getInt("idExpedition"),
                        client,
                        transporter,
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
            System.out.println("Error while retrieving transporter expeditions: " + e.getMessage());
        }

        return expeditionList;
    }

    // Get expeditions with pending status (for agent dashboard)
    public List<Expedition> getPendingExpeditions() {
        List<Expedition> pendingExpeditions = new ArrayList<>();
        String query = "SELECT e.*, " +
                "c.firstName as clientFirstName, c.lastName as clientLastName, c.gender as clientGender, " +
                "c.email as clientEmail, c.password as clientPassword, c.profilePhoto as clientProfilePhoto, " +
                "c.birthdayDate as clientBirthdayDate, c.phoneNumber as clientPhoneNumber " +
                "FROM expeditions e " +
                "JOIN user c ON e.clientId = c.idUser AND c.role = 'CLIENT' " +
                "WHERE e.packageStatus = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, PackageStatus.PENDING.name());
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                // Create Client object
                Client client = new Client(
                        rs.getInt("clientId"),
                        rs.getString("clientFirstName"),
                        rs.getString("clientLastName"),
                        Gender.valueOf(rs.getString("clientGender")),
                        rs.getString("clientEmail"),
                        rs.getString("clientPassword"),
                        rs.getString("clientProfilePhoto"),
                        rs.getTimestamp("clientBirthdayDate").toLocalDateTime(),
                        rs.getString("clientPhoneNumber")
                );

                // Pending expeditions don't have a transporter yet
                Expedition expedition = new Expedition(
                        rs.getInt("idExpedition"),
                        client,
                        null, // No transporter assigned yet
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

                pendingExpeditions.add(expedition);
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving pending expeditions: " + e.getMessage());
        }

        return pendingExpeditions;
    }

    // Get expedition by ID
    public Expedition getById(int expeditionId) {
        String query = "SELECT e.*, " +
                "c.firstName as clientFirstName, c.lastName as clientLastName, c.gender as clientGender, " +
                "c.email as clientEmail, c.password as clientPassword, c.profilePhoto as clientProfilePhoto, " +
                "c.birthdayDate as clientBirthdayDate, c.phoneNumber as clientPhoneNumber, " +
                "t.firstName as transporterFirstName, t.lastName as transporterLastName, t.gender as transporterGender, " +
                "t.email as transporterEmail, t.password as transporterPassword, t.profilePhoto as transporterProfilePhoto, " +
                "t.birthdayDate as transporterBirthdayDate, t.phoneNumber as transporterPhoneNumber, " +
                "t.transportType, t.website " +
                "FROM expeditions e " +
                "JOIN user c ON e.clientId = c.idUser AND c.role = 'CLIENT' " +
                "LEFT JOIN user t ON e.transporterId = t.idUser AND t.role = 'TRANSPORTER' " + // Changed to LEFT JOIN
                "WHERE e.idExpedition = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, expeditionId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // Create Client object
                Client client = new Client(
                        rs.getInt("clientId"),
                        rs.getString("clientFirstName"),
                        rs.getString("clientLastName"),
                        Gender.valueOf(rs.getString("clientGender")),
                        rs.getString("clientEmail"),
                        rs.getString("clientPassword"),
                        rs.getString("clientProfilePhoto"),
                        rs.getTimestamp("clientBirthdayDate").toLocalDateTime(),
                        rs.getString("clientPhoneNumber")
                );

                // Create Transporter object if transporterId is not null
                Transporter transporter = null;
                if (rs.getObject("transporterId") != null) {
                    transporter = new Transporter(
                            rs.getInt("transporterId"),
                            rs.getString("transporterFirstName"),
                            rs.getString("transporterLastName"),
                            Gender.valueOf(rs.getString("transporterGender")),
                            rs.getString("transporterEmail"),
                            rs.getString("transporterPassword"),
                            rs.getString("transporterProfilePhoto"),
                            rs.getTimestamp("transporterBirthdayDate").toLocalDateTime(),
                            rs.getString("transporterPhoneNumber"),
                            TransportType.valueOf(rs.getString("transportType")),
                            rs.getString("website")
                    );
                }

                // Create and return the Expedition object with potentially null transporter
                return new Expedition(
                        rs.getInt("idExpedition"),
                        client,
                        transporter,
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
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving expedition: " + e.getMessage());
        }

        return null;
    }
}