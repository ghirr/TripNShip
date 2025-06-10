package org.Esprit.TripNShip.Controllers.ExpeditionManagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Services.EmailService;
import org.Esprit.TripNShip.Services.ServiceExpedition;
import org.Esprit.TripNShip.Services.UserService;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AgentPendingController implements Initializable {

    @FXML
    private TableView<Expedition> expeditionTable;

    @FXML
    private TableColumn<Expedition, Integer> idColumn;

    @FXML
    private TableColumn<Expedition, String> clientColumn;

    @FXML
    private TableColumn<Expedition, PackageType> packageTypeColumn;

    @FXML
    private TableColumn<Expedition, Double> weightColumn;

    @FXML
    private TableColumn<Expedition, String> routeColumn;

    @FXML
    private TableColumn<Expedition, String> dateColumn;

    @FXML
    private TableColumn<Expedition, Void> actionColumn;

    @FXML
    private Button refreshBtn;

    @FXML
    private Label timestampLabel;

    private ServiceExpedition expeditionService;
    private UserService userService;
    private EmailService emailService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        expeditionService = new ServiceExpedition();
        userService = new UserService();
        emailService = new EmailService();

        // Configure the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("expeditionId"));
        packageTypeColumn.setCellValueFactory(new PropertyValueFactory<>("packageType"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));

        // Custom client display
        clientColumn.setCellValueFactory(cellData -> {
            Client client = cellData.getValue().getClient();
            String clientName = (client != null) ? client.getFirstName() + " " + client.getLastName() : "Unknown";
            return javafx.beans.binding.Bindings.createStringBinding(() -> clientName);
        });

        // Custom route display
        routeColumn.setCellValueFactory(cellData -> {
            String from = cellData.getValue().getDepartureCity();
            String to = cellData.getValue().getArrivalCity();
            return javafx.beans.binding.Bindings.createStringBinding(() -> from + " → " + to);
        });

        // Custom date display
        dateColumn.setCellValueFactory(cellData -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String date = sdf.format(cellData.getValue().getSendDate());
            return javafx.beans.binding.Bindings.createStringBinding(() -> date);
        });

        // Setup the action column with the assign button
        setupActionColumn();

        // Load pending expeditions
        loadPendingExpeditions();

        // Set refresh button handler
        refreshBtn.setOnAction(event -> loadPendingExpeditions());

        // Initialize timestamp
        updateTimestamp();
    }

    private void setupActionColumn() {
        Callback<TableColumn<Expedition, Void>, TableCell<Expedition, Void>> cellFactory =
                new Callback<>() {
                    @Override
                    public TableCell<Expedition, Void> call(final TableColumn<Expedition, Void> param) {
                        return new TableCell<>() {
                            private final Button assignBtn = new Button("Assign Transporter");
                            {
                                assignBtn.getStyleClass().add("secondary-button");
                                assignBtn.setOnAction(event -> {
                                    Expedition expedition = getTableView().getItems().get(getIndex());
                                    assignTransporter(expedition);
                                });
                            }

                            @Override
                            public void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    setGraphic(assignBtn);
                                }
                            }
                        };
                    }
                };

        actionColumn.setCellFactory(cellFactory);
    }

    private void loadPendingExpeditions() {
        // Get all expeditions
        List<Expedition> allExpeditions = expeditionService.getAll();

        // Filter only PENDING status
        List<Expedition> pendingExpeditions = allExpeditions.stream()
                .filter(e -> e.getPackageStatus() == PackageStatus.PENDING)
                .collect(Collectors.toList());

        expeditionTable.setItems(FXCollections.observableArrayList(pendingExpeditions));

        // Update timestamp
        updateTimestamp();
    }

    private void updateTimestamp() {
        if (timestampLabel != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            timestampLabel.setText("Last updated: " + dateFormat.format(new Date()));
        }
    }

    private void assignTransporter(Expedition expedition) {
        // Get all transporters
        List<Transporter> transporters = userService.getAllTransporters();

        if (transporters.isEmpty()) {
            // If no transporters, show error
            showAlert(Alert.AlertType.WARNING, "No Transporters",
                    "There are no transporters available in the system.");
            return;
        }

        // Create a dialog for transporter selection
        ChoiceDialog<Transporter> dialog = new ChoiceDialog<>(transporters.get(0), transporters);
        dialog.setTitle("Assign Transporter");
        dialog.setHeaderText("Choose a transporter for Expedition #" + expedition.getExpeditionId());
        dialog.setContentText("Select transporter:");

        // Set the dialog width
        dialog.getDialogPane().setPrefWidth(400);

        // Create a custom StringConverter for displaying transporters
        javafx.util.StringConverter<Transporter> stringConverter = new javafx.util.StringConverter<>() {
            @Override
            public String toString(Transporter transporter) {
                if (transporter == null) return "Select a transporter";
                return transporter.getFirstName() + " " + transporter.getLastName() +
                        " (" + transporter.getTransportType() + ")";
            }

            @Override
            public Transporter fromString(String string) {
                return null; // Not needed for our use case
            }
        };

        // Try to set the converter on the ComboBox if possible
        try {
            // Find the ComboBox or ChoiceBox in the dialog
            dialog.getDialogPane().lookupAll(".combo-box").forEach(node -> {
                if (node instanceof ComboBox) {
                    ((ComboBox<Transporter>) node).setConverter(stringConverter);
                }
            });

            dialog.getDialogPane().lookupAll(".choice-box").forEach(node -> {
                if (node instanceof ChoiceBox) {
                    ((ChoiceBox<Transporter>) node).setConverter(stringConverter);
                }
            });
        } catch (Exception e) {
            // If we can't set the converter, just log it - the dialog will still work
            System.out.println("Could not set custom converter: " + e.getMessage());
        }

        // Get the selected transporter
        Optional<Transporter> result = dialog.showAndWait();

        if (result.isPresent()) {
            Transporter selectedTransporter = result.get();

            // Assign transporter to expedition
            expedition.setTransporter(selectedTransporter);
            expedition.setPackageStatus(PackageStatus.AWAITING_CLIENT_APPROVAL);

            // Save changes
            try {
                expeditionService.update(expedition);

                // Get client details
                Client client = expedition.getClient();
                if (client != null && client.getEmail() != null && !client.getEmail().isEmpty()) {
                    // Send notification email to client
                    String clientName = client.getFirstName() + " " + client.getLastName();
                    String transporterName = selectedTransporter.getFirstName() + " " + selectedTransporter.getLastName();
                    String route = expedition.getDepartureCity() + " → " + expedition.getArrivalCity();

                    // Send email in a separate thread to prevent UI freezing
                    new Thread(() -> {
                        boolean emailSent = emailService.sendTransporterAssignmentEmail(
                                client.getEmail(),
                                clientName,
                                expedition.getExpeditionId(),
                                transporterName,
                                selectedTransporter.getTransportType().toString(),
                                route
                        );

                        // Log email status but don't block UI
                        if (emailSent) {
                            System.out.println("Email notification sent to client: " + clientName);
                        } else {
                            System.err.println("Failed to send email notification to: " + clientName);
                        }
                    }).start();
                }

                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Transporter assigned successfully. The client will be notified to approve.");

                // Refresh the table
                loadPendingExpeditions();

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Failed to assign transporter: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
