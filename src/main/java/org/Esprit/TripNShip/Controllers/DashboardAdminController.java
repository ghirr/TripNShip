package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DashboardAdminController implements Initializable {

    // KPI Labels
    @FXML private Label totalUsersLabel;
    @FXML private Label activeBookingsLabel;
    @FXML private Label revenueLabel;
    @FXML private Label expeditionsLabel;
    @FXML private Label totalAccommodationsLabel;
    @FXML private Label availableVehiclesLabel;
    @FXML private Label activeRoutesLabel;
    @FXML private Label tourCircuitsLabel;
    @FXML private Label rentalAgenciesLabel;
    @FXML private Label averageRatingLabel;

    // Charts
    @FXML private PieChart bookingStatusChart;
    @FXML private PieChart transportationTypesChart;
    @FXML private PieChart expeditionStatusChart;
    @FXML private AreaChart<String, Number> revenueChart;
    @FXML private LineChart<String, Number> userGrowthChart;
    @FXML private LineChart<String, Number> dailyBookingsChart;
    @FXML private BarChart<String, Number> vehicleTypesChart;
    @FXML private BarChart<String, Number> accommodationTypesChart;
    @FXML private BarChart<String, Number> tourDestinationsChart;
    @FXML private BarChart<String, Number> revenueByServiceChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeKPIs();
        initializePieCharts();
        initializeLineCharts();
        initializeAreaCharts();
        initializeBarCharts();
    }

    private void initializeKPIs() {
        // Update KPI values with realistic fake data
        totalUsersLabel.setText("2,847");
        activeBookingsLabel.setText("542");
        revenueLabel.setText("$48,750");
        expeditionsLabel.setText("127");
        totalAccommodationsLabel.setText("234");
        availableVehiclesLabel.setText("156");
        activeRoutesLabel.setText("45");
        tourCircuitsLabel.setText("18");
        rentalAgenciesLabel.setText("23");
        averageRatingLabel.setText("4.7★");
    }

    private void initializePieCharts() {
        // Booking Status Distribution
        ObservableList<PieChart.Data> bookingStatusData = FXCollections.observableArrayList(
                new PieChart.Data("Confirmed", 65),
                new PieChart.Data("Pending", 20),
                new PieChart.Data("Cancelled", 10),
                new PieChart.Data("Completed", 5)
        );
        bookingStatusChart.setData(bookingStatusData);
        bookingStatusChart.setTitle("Booking Statuses");

        // Transportation Types
        ObservableList<PieChart.Data> transportationData = FXCollections.observableArrayList(
                new PieChart.Data("Car Rental", 40),
                new PieChart.Data("Bus", 25),
                new PieChart.Data("Flight", 20),
                new PieChart.Data("Train", 10),
                new PieChart.Data("Taxi", 5)
        );
        transportationTypesChart.setData(transportationData);
        transportationTypesChart.setTitle("Transportation Distribution");

        // Expedition Status
        ObservableList<PieChart.Data> expeditionData = FXCollections.observableArrayList(
                new PieChart.Data("In Progress", 45),
                new PieChart.Data("Planned", 30),
                new PieChart.Data("Completed", 20),
                new PieChart.Data("Cancelled", 5)
        );
        expeditionStatusChart.setData(expeditionData);
        expeditionStatusChart.setTitle("Expedition Status");

        // Style pie charts
        stylePieChart(bookingStatusChart);
        stylePieChart(transportationTypesChart);
        stylePieChart(expeditionStatusChart);
    }

    private void initializeLineCharts() {
        // User Growth Chart
        XYChart.Series<String, Number> userSeries = new XYChart.Series<>();
        userSeries.setName("New Users");

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"};
        int[] userGrowth = {150, 220, 180, 280, 350, 420};

        for (int i = 0; i < months.length; i++) {
            userSeries.getData().add(new XYChart.Data<>(months[i], userGrowth[i]));
        }
        userGrowthChart.getData().add(userSeries);
        userGrowthChart.setTitle("Monthly User Registration");
        userGrowthChart.setCreateSymbols(true);

        // Daily Bookings Chart
        XYChart.Series<String, Number> bookingSeries = new XYChart.Series<>();
        bookingSeries.setName("Daily Bookings");

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            int bookings = (int) (Math.random() * 50) + 10; // Random bookings between 10-60
            bookingSeries.getData().add(new XYChart.Data<>(date.format(formatter), bookings));
        }
        dailyBookingsChart.getData().add(bookingSeries);
        dailyBookingsChart.setTitle("Daily Booking Activity");
        dailyBookingsChart.setCreateSymbols(false);
    }

    private void initializeAreaCharts() {
        // Revenue Chart
        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("Monthly Revenue");

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"};
        double[] revenues = {25000, 32000, 28000, 35000, 42000, 48750};

        for (int i = 0; i < months.length; i++) {
            revenueSeries.getData().add(new XYChart.Data<>(months[i], revenues[i]));
        }
        revenueChart.getData().add(revenueSeries);
        revenueChart.setTitle("Revenue Trend");
    }

    private void initializeBarCharts() {
        // Vehicle Types Chart
        XYChart.Series<String, Number> vehicleSeries = new XYChart.Series<>();
        vehicleSeries.setName("Available Vehicles");

        vehicleSeries.getData().add(new XYChart.Data<>("Sedan", 45));
        vehicleSeries.getData().add(new XYChart.Data<>("SUV", 35));
        vehicleSeries.getData().add(new XYChart.Data<>("Van", 25));
        vehicleSeries.getData().add(new XYChart.Data<>("Bus", 18));
        vehicleSeries.getData().add(new XYChart.Data<>("Luxury", 12));
        vehicleSeries.getData().add(new XYChart.Data<>("Motorcycle", 21));

        vehicleTypesChart.getData().add(vehicleSeries);
        vehicleTypesChart.setTitle("Vehicle Fleet Distribution");

        // Accommodation Types Chart
        XYChart.Series<String, Number> accommodationSeries = new XYChart.Series<>();
        accommodationSeries.setName("Accommodations");

        accommodationSeries.getData().add(new XYChart.Data<>("Hotel", 85));
        accommodationSeries.getData().add(new XYChart.Data<>("Resort", 42));
        accommodationSeries.getData().add(new XYChart.Data<>("Hostel", 38));
        accommodationSeries.getData().add(new XYChart.Data<>("Apartment", 52));
        accommodationSeries.getData().add(new XYChart.Data<>("Villa", 17));

        accommodationTypesChart.getData().add(accommodationSeries);
        accommodationTypesChart.setTitle("Accommodation Types");

        // Tour Destinations Chart
        XYChart.Series<String, Number> destinationSeries = new XYChart.Series<>();
        destinationSeries.setName("Bookings");

        destinationSeries.getData().add(new XYChart.Data<>("Paris", 125));
        destinationSeries.getData().add(new XYChart.Data<>("Rome", 98));
        destinationSeries.getData().add(new XYChart.Data<>("Barcelona", 87));
        destinationSeries.getData().add(new XYChart.Data<>("London", 76));
        destinationSeries.getData().add(new XYChart.Data<>("Amsterdam", 65));
        destinationSeries.getData().add(new XYChart.Data<>("Berlin", 54));

        tourDestinationsChart.getData().add(destinationSeries);
        tourDestinationsChart.setTitle("Popular Destinations");

        // Revenue by Service Type Chart
        XYChart.Series<String, Number> serviceRevenueSeries = new XYChart.Series<>();
        serviceRevenueSeries.setName("Revenue ($)");

        serviceRevenueSeries.getData().add(new XYChart.Data<>("Transportation", 18500));
        serviceRevenueSeries.getData().add(new XYChart.Data<>("Accommodation", 15200));
        serviceRevenueSeries.getData().add(new XYChart.Data<>("Tours", 8900));
        serviceRevenueSeries.getData().add(new XYChart.Data<>("Expeditions", 4200));
        serviceRevenueSeries.getData().add(new XYChart.Data<>("Insurance", 1950));

        revenueByServiceChart.getData().add(serviceRevenueSeries);
        revenueByServiceChart.setTitle("Service Revenue Distribution");

        // Style bar charts
        styleBarChart(vehicleTypesChart);
        styleBarChart(accommodationTypesChart);
        styleBarChart(tourDestinationsChart);
        styleBarChart(revenueByServiceChart);
    }

    private void stylePieChart(PieChart chart) {
        chart.setLegendVisible(true);
        chart.setLabelsVisible(true);
        chart.setStartAngle(90);

        // Apply colors to pie chart data
        chart.getData().forEach(data -> {
            data.nameProperty().addListener((observable, oldValue, newValue) -> {
                // Colors will be applied by CSS or can be set programmatically
            });
        });
    }

    private void styleBarChart(BarChart<String, Number> chart) {
        chart.setLegendVisible(false);
        chart.setCategoryGap(10);
        chart.setBarGap(5);

        // Rotate category axis labels if needed
        chart.getXAxis().setTickLabelRotation(45);
    }

    // Method to refresh all charts with new data (can be called from other controllers)
    public void refreshDashboard() {
        // Clear existing data
        clearAllCharts();

        // Reinitialize with fresh data
        initializeKPIs();
        initializePieCharts();
        initializeLineCharts();
        initializeAreaCharts();
        initializeBarCharts();
    }

    private void clearAllCharts() {
        if (bookingStatusChart != null) bookingStatusChart.getData().clear();
        if (transportationTypesChart != null) transportationTypesChart.getData().clear();
        if (expeditionStatusChart != null) expeditionStatusChart.getData().clear();
        if (revenueChart != null) revenueChart.getData().clear();
        if (userGrowthChart != null) userGrowthChart.getData().clear();
        if (dailyBookingsChart != null) dailyBookingsChart.getData().clear();
        if (vehicleTypesChart != null) vehicleTypesChart.getData().clear();
        if (accommodationTypesChart != null) accommodationTypesChart.getData().clear();
        if (tourDestinationsChart != null) tourDestinationsChart.getData().clear();
        if (revenueByServiceChart != null) revenueByServiceChart.getData().clear();
    }

    // Utility methods for external data updates
    public void updateKPI(String kpiType, String value) {
        switch (kpiType.toLowerCase()) {
            case "users":
                if (totalUsersLabel != null) totalUsersLabel.setText(value);
                break;
            case "bookings":
                if (activeBookingsLabel != null) activeBookingsLabel.setText(value);
                break;
            case "revenue":
                if (revenueLabel != null) revenueLabel.setText(value);
                break;
            case "expeditions":
                if (expeditionsLabel != null) expeditionsLabel.setText(value);
                break;
            case "accommodations":
                if (totalAccommodationsLabel != null) totalAccommodationsLabel.setText(value);
                break;
            case "vehicles":
                if (availableVehiclesLabel != null) availableVehiclesLabel.setText(value);
                break;
            case "routes":
                if (activeRoutesLabel != null) activeRoutesLabel.setText(value);
                break;
            case "circuits":
                if (tourCircuitsLabel != null) tourCircuitsLabel.setText(value);
                break;
            case "agencies":
                if (rentalAgenciesLabel != null) rentalAgenciesLabel.setText(value);
                break;
            case "rating":
                if (averageRatingLabel != null) averageRatingLabel.setText(value);
                break;
        }
    }

    // Method to simulate real-time data updates
    public void simulateRealTimeUpdates() {
        // This method can be called periodically to update charts with new data
        // For example, using a Timeline or ScheduledExecutorService

        // Update some KPIs with slight variations
        int currentUsers = Integer.parseInt(totalUsersLabel.getText().replace(",", ""));
        int newUsers = currentUsers + (int)(Math.random() * 10);
        totalUsersLabel.setText(String.format("%,d", newUsers));

        // Update active bookings
        int currentBookings = Integer.parseInt(activeBookingsLabel.getText());
        int newBookings = currentBookings + (int)(Math.random() * 5) - 2; // Can go up or down
        activeBookingsLabel.setText(String.valueOf(Math.max(0, newBookings)));

        // You can add more real-time updates here
    }
}