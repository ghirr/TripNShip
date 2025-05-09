package org.Esprit.TripNShip.Controllers;

import javafx.animation.PauseTransition;
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
import org.Esprit.TripNShip.Services.CircuitBookingService;
import org.Esprit.TripNShip.Utils.Shared;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ListViewCircuitBookingController {

    @FXML
    private TableView<CircuitBooking> circuitBookingTable;
    @FXML
    private TableColumn<CircuitBooking, String> colBookingDate;
    @FXML
    private TableColumn<CircuitBooking, String> colCustomerName;
    @FXML
    private TableColumn<CircuitBooking, String> colTourName;
    @FXML
    private TableColumn<CircuitBooking, Double> colBookingPrice;
    @FXML
    private TableColumn<CircuitBooking, Void> colActions;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TextField searchField;
    @FXML
    private Pagination pagination;
    @FXML
    private StackPane stackPane;

    private static ListViewCircuitBookingController instance;

    private ObservableList<CircuitBooking> circuitBookings = FXCollections.observableArrayList();
    private List<CircuitBooking> filteredCircuitBookings = new ArrayList<>();

    private static final int ROWS_PER_PAGE = 5;
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

        // Vérification que searchField n'est pas null
        if (searchField != null) {
            VBox.setMargin(searchField, new Insets(0, 0, 20, 0));
        }

        statusComboBox.setItems(FXCollections.observableArrayList("EN_ATTENTE", "CONFIRMEE", "ANNULEE"));
        statusComboBox.setValue("EN_ATTENTE"); // Valeur par défaut

        reloadCircuitBookingList();
        configureTable();
        pagination.setPageCount((int) Math.ceil((double) circuitBookings.size() / ROWS_PER_PAGE));
        pagination.setCurrentPageIndex(0);
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
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("statusBooking"));
        colTourName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colBookingPrice.setCellValueFactory(new PropertyValueFactory<>("nameCircuit"));
        setColActions();
        colActions.setSortable(false);
    }

    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredCircuitBookings.size());

        List<CircuitBooking> pageItems = filteredCircuitBookings.isEmpty() ?
                Collections.emptyList() : filteredCircuitBookings.subList(fromIndex, toIndex);

        circuitBookingTable.setItems(FXCollections.observableArrayList(pageItems));
        setColActions();
    }

    @FXML
    private void showAddCircuitBookingPopup() {
        try {
            Stage primaryStage = (Stage) stackPane.getScene().getWindow();
            Scene primaryScene = primaryStage.getScene();

            Rectangle overlay = new Rectangle(primaryScene.getWidth(), primaryScene.getHeight());
            overlay.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.4));

            primaryScene.widthProperty().addListener((obs, oldVal, newVal) -> overlay.setWidth(newVal.doubleValue()));
            primaryScene.heightProperty().addListener((obs, oldVal, newVal) -> overlay.setHeight(newVal.doubleValue()));

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showEditPopup(CircuitBooking circuitBooking) {
        try {
            Stage primaryStage = (Stage) stackPane.getScene().getWindow();
            Scene primaryScene = primaryStage.getScene();

            Rectangle overlay = new Rectangle(primaryScene.getWidth(), primaryScene.getHeight());
            overlay.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.4));

            primaryScene.widthProperty().addListener((obs, oldVal, newVal) -> overlay.setWidth(newVal.doubleValue()));
            primaryScene.heightProperty().addListener((obs, oldVal, newVal) -> overlay.setHeight(newVal.doubleValue()));

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
            e.printStackTrace();
        }
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

        if (selectedStatus != null && !selectedStatus.isEmpty()) {
            filteredCircuitBookings = circuitBookings.stream()
                    .filter(c -> c.getStatusBooking().name().equalsIgnoreCase(selectedStatus))
                    .collect(Collectors.toList());
            pagination.setPageCount((int) Math.ceil((double) filteredCircuitBookings.size() / ROWS_PER_PAGE));
            updateTable(0);
            pagination.setCurrentPageIndex(0);
        } else {
            refreshCircuitBookingList();
        }
    }

    private void setColActions() {
        colActions.setCellFactory(param -> new TableCell<CircuitBooking, Void>() {
            private final ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/editIcon.png")));
            private final ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/deleteIcon.png")));
            private final HBox hbox = new HBox(10, editIcon, deleteIcon);
            {
                editIcon.setFitWidth(30);
                editIcon.setFitHeight(30);
                deleteIcon.setFitWidth(30);
                deleteIcon.setFitHeight(30);

                // Appliquer un curseur "pointer" aux icônes
                editIcon.setStyle("-fx-cursor: hand;");
                deleteIcon.setStyle("-fx-cursor: hand;");


                editIcon.setOnMouseClicked(event -> {
                    CircuitBooking booking = getTableView().getItems().get(getIndex());
                    if (booking != null) showEditPopup(booking);
                });
                deleteIcon.setOnMouseClicked(event -> {
                    CircuitBooking booking = getTableView().getItems().get(getIndex());
                    if (booking != null) confirmDelete(booking);
                });
            }


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(10, editIcon, deleteIcon));
                }
            }
        });
    }
}
