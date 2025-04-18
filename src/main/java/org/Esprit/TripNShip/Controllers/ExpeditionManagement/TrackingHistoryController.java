package org.Esprit.TripNShip.Controllers.ExpeditionManagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import org.Esprit.TripNShip.Entities.Expedition;
import org.Esprit.TripNShip.Entities.PackageStatus;
import org.Esprit.TripNShip.Entities.TrackingHistory;
import org.Esprit.TripNShip.Services.ServiceTrackingHistory;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TrackingHistoryController implements Initializable {

    @FXML
    private TableView<TrackingHistory> historyTable;

    @FXML
    private TableColumn<TrackingHistory, Integer> idColumn;

    @FXML
    private TableColumn<TrackingHistory, PackageStatus> statusColumn;

    @FXML
    private TableColumn<TrackingHistory, String> locationColumn;

    @FXML
    private TableColumn<TrackingHistory, LocalDateTime> timestampColumn;

    @FXML
    private TableColumn<TrackingHistory, String> updatedByColumn;

    @FXML
    private Button closeBtn;

    private Expedition expedition;
    private ServiceTrackingHistory trackingService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        trackingService = new ServiceTrackingHistory();

        // Configure the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("locationNote"));

        // Custom cell factory for timestamp formatting
        timestampColumn.setCellValueFactory(cellData -> {
            LocalDateTime timestamp = cellData.getValue().getTimestamp();
            return javafx.beans.binding.Bindings.createObjectBinding(() -> timestamp);
        });

        timestampColumn.setCellFactory(column -> {
            return new javafx.scene.control.TableCell<TrackingHistory, LocalDateTime>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(formatter.format(item));
                    }
                }
            };
        });

        // Custom cell factory for transporter name
        updatedByColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getUpdatedBy() != null) {
                String name = cellData.getValue().getUpdatedBy().getFirstName() + " " +
                        cellData.getValue().getUpdatedBy().getLastName();
                return javafx.beans.binding.Bindings.createStringBinding(() -> name);
            } else {
                return javafx.beans.binding.Bindings.createStringBinding(() -> "System");
            }
        });

        // Set button action
        closeBtn.setOnAction(event -> ((Stage) closeBtn.getScene().getWindow()).close());
    }

    public void setExpedition(Expedition expedition) {
        this.expedition = expedition;
        loadTrackingHistory();
    }

    private void loadTrackingHistory() {
        if (expedition == null) {
            return;
        }

        try {
            List<TrackingHistory> historyList = trackingService.getTrackingByExpedition(expedition.getExpeditionId());

            if (historyList.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No History",
                        "No tracking history available for this expedition.");
            }

            historyTable.setItems(FXCollections.observableArrayList(historyList));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load tracking history: " + e.getMessage());
            e.printStackTrace();
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