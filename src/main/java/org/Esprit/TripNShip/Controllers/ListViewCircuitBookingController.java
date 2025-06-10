package org.Esprit.TripNShip.Controllers;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.Esprit.TripNShip.Entities.CircuitBooking;
import org.Esprit.TripNShip.Entities.StatusBooking;
import org.Esprit.TripNShip.Entities.TourCircuit;
import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Services.CircuitBookingService;
import org.Esprit.TripNShip.Utils.Shared;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ListViewCircuitBookingController {

    @FXML private TableView<CircuitBooking> circuitBookingTable;
    @FXML private TableColumn<CircuitBooking, String> colBookingDate;
    @FXML private TableColumn<CircuitBooking, String> colCustomerName;
    @FXML private TableColumn<CircuitBooking, String> colTourName;
    @FXML private TableColumn<CircuitBooking, String> colStatus;
    @FXML private TableColumn<CircuitBooking, Void> colActions;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField searchField;
    @FXML private Pagination pagination;
    @FXML private StackPane stackPane;

    private static ListViewCircuitBookingController instance;
    private ObservableList<CircuitBooking> circuitBookings = FXCollections.observableArrayList();
    private List<CircuitBooking> filteredCircuitBookings = new ArrayList<>();

    private static final int ROWS_PER_PAGE = 6;
    private CircuitBookingService circuitBookingService;
    private PauseTransition pause;

    public ListViewCircuitBookingController() {
        instance = this;
    }

    public static ListViewCircuitBookingController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        circuitBookingService = new CircuitBookingService();

        if (searchField != null) {
            VBox.setMargin(searchField, new Insets(0, 0, 20, 0));
        }

        circuitBookingTable.setRowFactory(tv -> {
            TableRow<CircuitBooking> row = new TableRow<>();
            row.setPrefHeight(40);
            return row;
        });

        List<String> statusOptions = new ArrayList<>();
        statusOptions.add("All");
        statusOptions.addAll(Arrays.stream(StatusBooking.values()).map(Enum::name).collect(Collectors.toList()));
        statusComboBox.setItems(FXCollections.observableArrayList(statusOptions));
        statusComboBox.setValue("All");

        reloadCircuitBookingList();
        configureTable();

        pagination.setPageCount((int) Math.ceil((double) circuitBookings.size() / ROWS_PER_PAGE));
        pagination.setCurrentPageIndex(0);
        pagination.setMaxPageIndicatorCount(3);
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> updateTable(newIndex.intValue()));

        pause = new PauseTransition(Duration.millis(100));
    }

    private void loadCircuitBookings() {
        List<CircuitBooking> circuitBookingList = circuitBookingService.getAll();
        circuitBookings.setAll(circuitBookingList);
    }

    private void confirmDelete(CircuitBooking circuitBooking) {
        Optional<ButtonType> result = Shared.deletePopUP("Are you sure to delete this booking?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            circuitBookingService.delete(circuitBooking);
            circuitBookings.remove(circuitBooking);
            refreshCircuitBookingList();
        }
    }

    private void configureTable() {
        colBookingDate.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("statusBooking"));

        colCustomerName.setCellValueFactory(cellData -> {
            String fullName = cellData.getValue().getUser().getFirstName() + " " + cellData.getValue().getUser().getLastName();
            return new SimpleObjectProperty<>(fullName);
        });

        colTourName.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTourCircuit().getNameCircuit()));

        setColActions();
        colActions.setSortable(false);
    }

    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredCircuitBookings.size());
        List<CircuitBooking> pageItems = filteredCircuitBookings.isEmpty() ? Collections.emptyList() : filteredCircuitBookings.subList(fromIndex, toIndex);
        circuitBookingTable.setItems(FXCollections.observableArrayList(pageItems));
        setColActions();
    }

    private void setColActions() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/icons8-edit-64.png")));
            private final ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/icons8-delete-48.png")));
            private final ImageView pdfIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/icons8-export-pdf-30.png")));
            private final HBox hbox = new HBox(10, editIcon, deleteIcon, pdfIcon);

            {
                editIcon.setFitWidth(30); editIcon.setFitHeight(30);
                deleteIcon.setFitWidth(30); deleteIcon.setFitHeight(30);
                pdfIcon.setFitWidth(30); pdfIcon.setFitHeight(30);

                editIcon.setOnMouseClicked(e -> showEditPopup(getTableView().getItems().get(getIndex())));
                deleteIcon.setOnMouseClicked(e -> confirmDelete(getTableView().getItems().get(getIndex())));
                pdfIcon.setOnMouseClicked(e -> generatePdf(getTableView().getItems().get(getIndex())));
            }

            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null); else setGraphic(hbox);
            }
        });
    }

    @FXML
    private void showAddCircuitBookingPopup() {
        try {
            Stage primaryStage = (Stage) stackPane.getScene().getWindow();
            Scene primaryScene = primaryStage.getScene();

            Rectangle overlay = new Rectangle(primaryScene.getWidth(), primaryScene.getHeight());
            overlay.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.4));

            Pane rootPane = (Pane) primaryScene.getRoot();
            rootPane.getChildren().add(overlay);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/AddCircuitBooking.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> rootPane.getChildren().remove(overlay));
            stage.showAndWait();

            refreshCircuitBookingList();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d’ouvrir le formulaire : " + e.getMessage());
        }
    }

    @FXML
    private void showEditPopup(CircuitBooking circuitBooking) {
        try {
            Stage primaryStage = (Stage) stackPane.getScene().getWindow();
            Scene primaryScene = primaryStage.getScene();

            Rectangle overlay = new Rectangle(primaryScene.getWidth(), primaryScene.getHeight());
            overlay.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.4));

            Pane rootPane = (Pane) primaryScene.getRoot();
            rootPane.getChildren().add(overlay);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/UpdateCircuitBooking.fxml"));
            Parent root = loader.load();
            UpdateCircuitBookingController controller = loader.getController();
            controller.setBookingData(circuitBooking);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> rootPane.getChildren().remove(overlay));
            stage.showAndWait();

            refreshCircuitBookingList();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d’ouvrir le formulaire : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void refreshCircuitBookingList() {
        filteredCircuitBookings = new ArrayList<>(circuitBookings);
        pagination.setPageCount((int) Math.ceil((double) filteredCircuitBookings.size() / ROWS_PER_PAGE));
        updateTable(pagination.getCurrentPageIndex());
    }

    public void reloadCircuitBookingList() {
        loadCircuitBookings();
        refreshCircuitBookingList();
    }

    @FXML
    private void handleStatusFilter() {
        String selectedStatus = statusComboBox.getValue();
        if (selectedStatus != null && !selectedStatus.isEmpty() && !selectedStatus.equalsIgnoreCase("Tous")) {
            filteredCircuitBookings = circuitBookings.stream()
                    .filter(c -> c.getStatusBooking().name().equalsIgnoreCase(selectedStatus))
                    .collect(Collectors.toList());
        } else {
            filteredCircuitBookings = new ArrayList<>(circuitBookings);
        }
        pagination.setPageCount((int) Math.ceil((double) filteredCircuitBookings.size() / ROWS_PER_PAGE));
        updateTable(0);
        pagination.setCurrentPageIndex(0);
    }

    public void generatePdf(CircuitBooking booking) {
        try {
            Path downloadsPath = Paths.get(System.getProperty("user.home"), "Downloads");
            if (!Files.exists(downloadsPath)) Files.createDirectories(downloadsPath);

            Path pdfPath = downloadsPath.resolve("circuit_booking_" + booking.getIdBooking() + ".pdf");
            PdfWriter writer = new PdfWriter(pdfPath.toString());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Circuit Booking ID: " + booking.getIdBooking()));
            document.add(new Paragraph("Customer: " + booking.getUser().getFirstName() + " " + booking.getUser().getLastName()));
            document.add(new Paragraph("Tour: " + booking.getTourCircuit().getNameCircuit()));
            document.add(new Paragraph("Booking Date: " + booking.getBookingDate()));
            document.add(new Paragraph("Status: " + booking.getStatusBooking()));
            document.close();

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("PDF généré");
                alert.setHeaderText(null);
                alert.setContentText("Le PDF a été généré dans le dossier Téléchargements.");
                alert.showAndWait();
            });

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfPath.toFile());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
