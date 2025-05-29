package org.Esprit.TripNShip.Controllers.ExpeditionManagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.Esprit.TripNShip.Entities.*;

import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ExpeditionStatisticsController implements Initializable {

    @FXML
    private PieChart statusPieChart;

    @FXML
    private BarChart<String, Number> packageTypeBarChart;

    @FXML
    private LineChart<String, Number> expeditionTimelineChart;

    @FXML
    private BarChart<String, Number> topRoutesBarChart;

    @FXML
    private TableView<ExpeditionStatItem> statsTable;

    @FXML
    private TableColumn<ExpeditionStatItem, String> statNameColumn;

    @FXML
    private TableColumn<ExpeditionStatItem, String> statValueColumn;

    @FXML
    private Label totalExpeditionsLabel;

    @FXML
    private Label totalRevenueLabel;

    @FXML
    private Label averageDeliveryTimeLabel;

    @FXML
    private Label pendingExpeditionsLabel;

    @FXML
    private GridPane topTransportersGrid;

    private List<Expedition> expeditionData = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize table columns
        statNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        statValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
    }

    public void setExpeditionData(List<Expedition> expeditions) {
        this.expeditionData = expeditions;

        // Process and display statistics
        updateAllCharts();
        updateSummaryStats();
        updateStatsTable();
    }

    private void updateAllCharts() {
        createStatusPieChart();
        createPackageTypeBarChart();
        createExpeditionTimelineChart();
        createTopRoutesBarChart();
    }

    private void createStatusPieChart() {
        // Group expeditions by status and count them
        Map<PackageStatus, Long> statusCounts = expeditionData.stream()
                .collect(Collectors.groupingBy(Expedition::getPackageStatus, Collectors.counting()));

        // Create pie chart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        statusCounts.forEach((status, count) ->
                pieChartData.add(new PieChart.Data(status.toString() + " (" + count + ")", count))
        );

        // Set data to pie chart
        statusPieChart.setData(pieChartData);
        statusPieChart.setTitle("Expedition Status Distribution");

        // Add custom colors to match the status colors in the application
        int i = 0;
        for (PieChart.Data data : pieChartData) {
            String statusName = data.getName().split(" ")[0];

            String color = switch(PackageStatus.valueOf(statusName)) {
                case PENDING -> "#f39c12";
                case AWAITING_CLIENT_APPROVAL -> "#fd7e14";
                case SHIPPED -> "#9b59b6";
                case IN_TRANSIT -> "#3498db";
                case DELIVERED -> "#2ecc71";
                case CANCELLED -> "#e74c3c";
                default -> "#7f8c8d";
            };

            data.getNode().setStyle("-fx-pie-color: " + color + ";");
            i++;
        }
    }

    private void createPackageTypeBarChart() {
        // Group expeditions by package type and count them
        Map<PackageType, Long> typeCounts = expeditionData.stream()
                .collect(Collectors.groupingBy(Expedition::getPackageType, Collectors.counting()));

        // Create bar chart series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Package Types");

        typeCounts.forEach((type, count) ->
                series.getData().add(new XYChart.Data<>(type.toString(), count))
        );

        // Clear existing data and add new series
        packageTypeBarChart.getData().clear();
        packageTypeBarChart.getData().add(series);
        packageTypeBarChart.setTitle("Package Type Distribution");
    }

    private void createExpeditionTimelineChart() {
        // Group expeditions by month
        Map<String, List<Expedition>> expeditionsByMonth = expeditionData.stream()
                .filter(e -> e.getSendDate() != null)
                .collect(Collectors.groupingBy(expedition -> {
                    // Convert java.sql.Date to a Calendar to extract month and year
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(expedition.getSendDate());
                    return new SimpleDateFormat("MMM yyyy").format(cal.getTime());
                }));

        // Sort months chronologically
        List<String> sortedMonths = new ArrayList<>(expeditionsByMonth.keySet());
        sortedMonths.sort((m1, m2) -> {
            try {
                Date date1 = new SimpleDateFormat("MMM yyyy").parse(m1);
                Date date2 = new SimpleDateFormat("MMM yyyy").parse(m2);
                return date1.compareTo(date2);
            } catch (Exception e) {
                return m1.compareTo(m2);
            }
        });

        // Create series for each status
        Map<PackageStatus, XYChart.Series<String, Number>> seriesMap = new HashMap<>();
        for (PackageStatus status : PackageStatus.values()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(status.toString());
            seriesMap.put(status, series);
        }

        // Populate series with data
        for (String month : sortedMonths) {
            Map<PackageStatus, Long> statusCountsForMonth = expeditionsByMonth.get(month).stream()
                    .collect(Collectors.groupingBy(Expedition::getPackageStatus, Collectors.counting()));

            // Add data points for each status
            for (PackageStatus status : PackageStatus.values()) {
                long count = statusCountsForMonth.getOrDefault(status, 0L);
                seriesMap.get(status).getData().add(new XYChart.Data<>(month, count));
            }
        }

        // Clear existing data and add all series
        expeditionTimelineChart.getData().clear();
        seriesMap.values().forEach(series -> expeditionTimelineChart.getData().add(series));
        expeditionTimelineChart.setTitle("Monthly Expedition Trends");
    }

    private void createTopRoutesBarChart() {
        // Create route strings and count frequency
        Map<String, Long> routeCounts = expeditionData.stream()
                .collect(Collectors.groupingBy(
                        expedition -> expedition.getDepartureCity() + " → " + expedition.getArrivalCity(),
                        Collectors.counting()
                ));

        // Sort routes by count and get top 5
        List<Map.Entry<String, Long>> topRoutes = routeCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        // Create bar chart series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Route Frequency");

        // Add data points for top routes
        for (Map.Entry<String, Long> entry : topRoutes) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        // Clear existing data and add new series
        topRoutesBarChart.getData().clear();
        topRoutesBarChart.getData().add(series);
        topRoutesBarChart.setTitle("Top 5 Popular Routes");
    }

    private void updateSummaryStats() {
        // Total expeditions
        int total = expeditionData.size();
        totalExpeditionsLabel.setText(String.valueOf(total));

        // Total revenue
        double totalRevenue = expeditionData.stream()
                .mapToDouble(Expedition::getShippingCost)
                .sum();
        totalRevenueLabel.setText(currencyFormat.format(totalRevenue));

        // Average delivery time (days)
        double avgDeliveryDays = expeditionData.stream()
                .filter(e -> e.getSendDate() != null && e.getEstimatedDeliveryDate() != null)
                .mapToDouble(e -> {
                    // Calculate difference between dates in days
                    long diffMs = e.getEstimatedDeliveryDate().getTime() - e.getSendDate().getTime();
                    return diffMs / (1000.0 * 60 * 60 * 24);
                })
                .average()
                .orElse(0);
        averageDeliveryTimeLabel.setText(String.format("%.1f days", avgDeliveryDays));

        // Pending expeditions
        long pendingCount = expeditionData.stream()
                .filter(e -> e.getPackageStatus() == PackageStatus.PENDING ||
                        e.getPackageStatus() == PackageStatus.AWAITING_CLIENT_APPROVAL)
                .count();
        pendingExpeditionsLabel.setText(String.valueOf(pendingCount));

        // Top transporters statistics
        Map<Transporter, Long> transporterExpeditionCounts = expeditionData.stream()
                .filter(e -> e.getTransporter() != null)
                .collect(Collectors.groupingBy(Expedition::getTransporter, Collectors.counting()));

        // Sort by count and get top 3
        List<Map.Entry<Transporter, Long>> topTransporters = transporterExpeditionCounts.entrySet().stream()
                .sorted(Map.Entry.<Transporter, Long>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList());

        // Add top transporters to grid
        int row = 0;
        for (Map.Entry<Transporter, Long> entry : topTransporters) {
            if (row < 3) { // We have 3 rows available in the grid
                Transporter transporter = entry.getKey();
                String name = transporter.getFirstName() + " " + transporter.getLastName();
                long count = entry.getValue();

                VBox transporterBox = createTransporterBox(name, count);
                topTransportersGrid.add(transporterBox, 0, row);
                row++;
            }
        }
    }
    private VBox createTransporterBox(String name, long count) {
        VBox box = new VBox(5);
        box.getStyleClass().add("card");
        box.setPrefHeight(100);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("card-title");

        Label countLabel = new Label(count + " expeditions");
        countLabel.getStyleClass().add("card-subtitle");

        box.getChildren().addAll(nameLabel, countLabel);
        return box;
    }

    private void updateStatsTable() {
        List<ExpeditionStatItem> stats = new ArrayList<>();

        // Add various statistics to the table
        stats.add(new ExpeditionStatItem("Total Expeditions", String.valueOf(expeditionData.size())));

        // Calculate average shipping cost
        double avgCost = expeditionData.stream()
                .mapToDouble(Expedition::getShippingCost)
                .average()
                .orElse(0);
        stats.add(new ExpeditionStatItem("Average Shipping Cost", currencyFormat.format(avgCost)));

        // Calculate average package weight
        double avgWeight = expeditionData.stream()
                .mapToDouble(Expedition::getWeight)
                .average()
                .orElse(0);
        stats.add(new ExpeditionStatItem("Average Package Weight", String.format("%.2f kg", avgWeight)));

        // Add status counts
        for (PackageStatus status : PackageStatus.values()) {
            long count = expeditionData.stream()
                    .filter(e -> e.getPackageStatus() == status)
                    .count();
            stats.add(new ExpeditionStatItem(status.toString() + " Expeditions", String.valueOf(count)));
        }

        // Add most popular package type
        Map<PackageType, Long> typeCounts = expeditionData.stream()
                .collect(Collectors.groupingBy(Expedition::getPackageType, Collectors.counting()));

        Optional<Map.Entry<PackageType, Long>> mostPopularType = typeCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        if (mostPopularType.isPresent()) {
            stats.add(new ExpeditionStatItem("Most Popular Package Type",
                    mostPopularType.get().getKey() + " (" + mostPopularType.get().getValue() + ")"));
        }

        // Add most popular route
        Map<String, Long> routeCounts = expeditionData.stream()
                .collect(Collectors.groupingBy(
                        expedition -> expedition.getDepartureCity() + " → " + expedition.getArrivalCity(),
                        Collectors.counting()
                ));

        Optional<Map.Entry<String, Long>> mostPopularRoute = routeCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        if (mostPopularRoute.isPresent()) {
            stats.add(new ExpeditionStatItem("Most Popular Route",
                    mostPopularRoute.get().getKey() + " (" + mostPopularRoute.get().getValue() + ")"));
        }

        // Set data to table
        statsTable.setItems(FXCollections.observableArrayList(stats));
    }

    // Inner class for statistics table
    public static class ExpeditionStatItem {
        private final String name;
        private final String value;

        public ExpeditionStatItem(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
}