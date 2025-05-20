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
import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Services.VehicleRentalService;
import org.Esprit.TripNShip.Utils.Shared;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ListViewVehicleRentalController {

    @FXML private TableView<VehicleRental> vehicleRentalTable;
    @FXML private TableColumn<VehicleRental, String> colstartDate;
    @FXML private TableColumn<VehicleRental, String> colendDate;
    @FXML private TableColumn<VehicleRental, String> coltotalPrice;
    @FXML private TableColumn<VehicleRental, String> colstatusCircuit;
    @FXML private TableColumn<VehicleRental, String> colvehicle;
    @FXML private TableColumn<VehicleRental, String> coluser;
    @FXML private TableColumn<VehicleRental, Void> colActions;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField searchField;
    @FXML private Pagination pagination;
    @FXML private StackPane stackPane;

    private static ListViewVehicleRentalController instance;
    private final ObservableList<VehicleRental> vehicleRentals = FXCollections.observableArrayList();
    private List<VehicleRental> filteredVehicleRentals = new ArrayList<>();
    private static final int ROWS_PER_PAGE = 6;
    private VehicleRentalService vehicleRentalService;
    private PauseTransition pause;

    public ListViewVehicleRentalController() {
        instance = this;
    }

    public static ListViewVehicleRentalController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        vehicleRentalService = new VehicleRentalService();

        if (searchField != null) {
            VBox.setMargin(searchField, new Insets(0, 0, 20, 0));
        }

        vehicleRentalTable.setRowFactory(tv -> {
            TableRow<VehicleRental> row = new TableRow<>();
            row.setPrefHeight(40);
            return row;
        });

        List<String> statusOptions = new ArrayList<>();
        statusOptions.add("All");
        statusOptions.addAll(Arrays.stream(StautCircuit.values())
                .map(Enum::name)
                .collect(Collectors.toList()));
        statusComboBox.setItems(FXCollections.observableArrayList(statusOptions));
        statusComboBox.setValue("All");

        loadVehicleRental();
        configureTable();

        pagination.setMaxPageIndicatorCount(3);
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> updateTable(newIndex.intValue()));

        pause = new PauseTransition(Duration.millis(100));
    }

    private void loadVehicleRental() {
        List<VehicleRental> vehicleRentalList = vehicleRentalService.getAll();
        vehicleRentals.setAll(vehicleRentalList);
        filteredVehicleRentals = new ArrayList<>(vehicleRentals);
        pagination.setPageCount((int) Math.ceil((double) filteredVehicleRentals.size() / ROWS_PER_PAGE));
        updateTable(0);
    }

    private void configureTable() {
        colstartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colendDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        coltotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colstatusCircuit.setCellValueFactory(new PropertyValueFactory<>("statusCircuit"));

        colvehicle.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getVehicle().getBrand())
        );

        coluser.setCellValueFactory(cellData -> {
            String fullName = cellData.getValue().getUser().getFirstName() + " " + cellData.getValue().getUser().getLastName();
            return new SimpleObjectProperty<>(fullName);
        });

        setColActions();
        colActions.setSortable(false);
    }

    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredVehicleRentals.size());
        List<VehicleRental> pageItems = filteredVehicleRentals.subList(fromIndex, toIndex);
        vehicleRentalTable.setItems(FXCollections.observableArrayList(pageItems));
    }

    private void refreshVehicleRentalList() {
        filteredVehicleRentals = new ArrayList<>(vehicleRentals);
        pagination.setPageCount((int) Math.ceil((double) filteredVehicleRentals.size() / ROWS_PER_PAGE));
        updateTable(pagination.getCurrentPageIndex());
    }



    private void confirmDelete(VehicleRental vehicleRental) {
        Optional<ButtonType> result = Shared.deletePopUP("Are you sure to delete this booking?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            vehicleRentalService.delete(vehicleRental);
            vehicleRentals.remove(vehicleRental);
            refreshVehicleRentalList();
        }
    }
    @FXML
    private void showAddVehicleRentalPopup() {
        showPopup("/fxml/CircuitManagementFXML/AddVehicleRental.fxml", null);
    }

    @FXML
    private void showEditPopup(VehicleRental vehicleRental) {
        showPopup("/fxml/CircuitManagementFXML/UpdateVehicleRental.fxml", vehicleRental);
        refreshVehicleRentalList();
    }

    private void showPopup(String fxmlPath, VehicleRental vehicleRental) {
        try {
            Stage primaryStage = (Stage) stackPane.getScene().getWindow();
            Scene primaryScene = primaryStage.getScene();

            Rectangle overlay = new Rectangle(primaryScene.getWidth(), primaryScene.getHeight());
            overlay.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.4));

            primaryScene.widthProperty().addListener((obs, oldVal, newVal) -> overlay.setWidth(newVal.doubleValue()));
            primaryScene.heightProperty().addListener((obs, oldVal, newVal) -> overlay.setHeight(newVal.doubleValue()));

            Pane rootPane = (Pane) primaryScene.getRoot();
            rootPane.getChildren().add(overlay);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (vehicleRental != null) {
                UpdateVehicleRentalController controller = loader.getController();
                controller.setVehicleRental(vehicleRental);
            }

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
    private void handleStatusFilter() {
        String selectedStatus = statusComboBox.getValue();

        if (selectedStatus != null && !selectedStatus.equalsIgnoreCase("All")) {
            filteredVehicleRentals = vehicleRentals.stream()
                    .filter(vr -> vr.getStatusCircuit().name().equalsIgnoreCase(selectedStatus))
                    .collect(Collectors.toList());
        } else {
            filteredVehicleRentals = new ArrayList<>(vehicleRentals);
        }

        pagination.setPageCount((int) Math.ceil((double) filteredVehicleRentals.size() / ROWS_PER_PAGE));
        updateTable(0);
        pagination.setCurrentPageIndex(0);
    }

    private void setColActions() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/icons8-edit-64.png")));
            private final ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/icons8-delete-48.png")));
            private final HBox hbox = new HBox(10, editIcon, deleteIcon);

            {
                editIcon.setFitWidth(30);
                editIcon.setFitHeight(30);
                deleteIcon.setFitWidth(30);
                deleteIcon.setFitHeight(30);

                editIcon.setStyle("-fx-cursor: hand;");
                deleteIcon.setStyle("-fx-cursor: hand;");

                editIcon.setOnMouseClicked(event -> {
                    VehicleRental vehicleRental = getTableView().getItems().get(getIndex());
                    if (vehicleRental != null) showEditPopup(vehicleRental);
                });

                deleteIcon.setOnMouseClicked(event -> {
                    VehicleRental vehicleRental = getTableView().getItems().get(getIndex());
                    if (vehicleRental != null) confirmDelete(vehicleRental);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }
}
