package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController {

    @FXML
    private Label totalBookingsLabel;

    @FXML
    private Label confirmedLabel;

    @FXML
    private Label pendingLabel;

    @FXML
    private PieChart bookingPieChart;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        int total = 124;
        int confirmed = 87;
        int pending = 37;

        totalBookingsLabel.setText(String.valueOf(total));
        confirmedLabel.setText(String.valueOf(confirmed));
        pendingLabel.setText(String.valueOf(pending));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Confirmées", confirmed),
                new PieChart.Data("En attente", pending)
        );
        bookingPieChart.setData(pieChartData);
    }
}
