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
import javafx.util.StringConverter;
import org.Esprit.TripNShip.Entities.*;
import org.Esprit.TripNShip.Services.VehicleService;
import org.Esprit.TripNShip.Utils.Shared;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ListViewVehicleController {

    @FXML
    private TableView<Vehicle> vehicleTable;

    @FXML
    private TableColumn<Vehicle, String> colbrand;
    @FXML
    private TableColumn<Vehicle, String> colmodel;
    @FXML
    private TableColumn<Vehicle, String> collicensePlate;
    @FXML
    private TableColumn<Vehicle, Float> coldailyPrice;
    @FXML
    private TableColumn<Vehicle, Boolean> colavailability;
    @FXML
    private TableColumn<Vehicle, Type> coltype;
    @FXML
    private TableColumn<Vehicle, String> colrentalAgency;
    @FXML
    private TableColumn<Vehicle, ImageView> colimage;
    @FXML
    private TableColumn<Vehicle, Void> colActions;
    @FXML
    private ComboBox<Type> statusComboBox;

    @FXML
    private TextField searchField;
    @FXML
    private Pagination pagination;
    @FXML
    private StackPane stackPane;

    private static ListViewVehicleController instance;
    private ObservableList<Vehicle> vehicles = FXCollections.observableArrayList();
    private List<Vehicle> filteredVehicles = new ArrayList<>();
    private static final int ROWS_PER_PAGE = 6;
    private VehicleService vehicleService;
    private PauseTransition pause;

    public ListViewVehicleController() {
        instance = this;
    }

    public static ListViewVehicleController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        vehicleService = new VehicleService();

        if (searchField != null) {
            VBox.setMargin(searchField, new Insets(0, 0, 20, 0));
        }

        vehicleTable.setRowFactory(tv -> {
            TableRow<Vehicle> row = new TableRow<>();
            row.setPrefHeight(40);
            return row;
        });

        List<Type> typeOptions = new ArrayList<>();
        typeOptions.add(null);
        typeOptions.addAll(Arrays.asList(Type.values()));
        statusComboBox.setItems(FXCollections.observableArrayList(typeOptions));
        statusComboBox.setValue(null);

        statusComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Type type) {
                return type == null ? "All" : type.name();
            }

            @Override
            public Type fromString(String string) {
                return "All".equals(string) ? null : Type.valueOf(string);
            }
        });

        statusComboBox.valueProperty().addListener((obs, oldVal, newVal) -> handleSearch());

        reloadVehicleList();
        configureTable();

        pagination.setMaxPageIndicatorCount(3);
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> updateTable(newIndex.intValue()));

        pause = new PauseTransition(Duration.millis(100));
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> handleSearch());
            pause.playFromStart();
        });
    }

    private void loadVehicles() {
        List<Vehicle> vehicleList = vehicleService.getAll();
        vehicles.setAll(vehicleList);
    }

    private void confirmDelete(Vehicle vehicle) {
        Optional<ButtonType> result = Shared.deletePopUP("Are you sure to delete this booking?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            vehicleService.delete(vehicle);
            vehicles.remove(vehicle);
            refreshVehicleList();
        }
    }

    private void configureTable() {
        colbrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colmodel.setCellValueFactory(new PropertyValueFactory<>("model"));
        collicensePlate.setCellValueFactory(new PropertyValueFactory<>("licensePlate"));
        coldailyPrice.setCellValueFactory(new PropertyValueFactory<>("dailyPrice"));
        colavailability.setCellValueFactory(new PropertyValueFactory<>("availability"));
        coltype.setCellValueFactory(new PropertyValueFactory<>("type"));

        colrentalAgency.setCellValueFactory(cellData -> {
            String rentalAgencyName = cellData.getValue().getRentalAgency().getNameAgency();
            return new SimpleObjectProperty<>(rentalAgencyName);
        });

        colimage.setCellValueFactory(cellData -> {
            ImageView imageView = new ImageView();

            try {
                String imagePath = cellData.getValue().getImageURL();

                if (imagePath != null && !imagePath.isEmpty()) {
                    // Nettoyer le chemin s'il commence par "file:\"
                    if (imagePath.startsWith("file:\\")) {
                        imagePath = imagePath.replace("file:\\", "");
                    } else if (imagePath.startsWith("file:/")) {
                        imagePath = imagePath.replace("file:/", "");
                    }

                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        String imageURI = imageFile.toURI().toString();
                        imageView.setImage(new Image(imageURI));
                    } else {
                        System.out.println("Image introuvable : " + imageFile.getAbsolutePath());
                        imageView.setImage(new Image(getClass().getResource("/images/icons8-car-rental-64.png").toExternalForm()));
                    }
                } else {
                    imageView.setImage(new Image(getClass().getResource("/images/icons8-car-rental-64.png").toExternalForm()));
                }

            } catch (Exception e) {
                System.out.println("Erreur lors du chargement de l'image : " + e.getMessage());
                imageView.setImage(new Image(getClass().getResource("/images/icons8-car-rental-64.png").toExternalForm()));
            }

            imageView.setFitWidth(60);
            imageView.setFitHeight(60);
            imageView.setPreserveRatio(true);

            return new SimpleObjectProperty<>(imageView);
        });


        setColActions();
        colActions.setSortable(false);
    }

    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredVehicles.size());

        List<Vehicle> pageItems = filteredVehicles.isEmpty() ?
                Collections.emptyList() : filteredVehicles.subList(fromIndex, toIndex);

        vehicleTable.setItems(FXCollections.observableArrayList(pageItems));
        setColActions();
    }

    @FXML
    private void showAddVehiclePopup() {
        try {
            Stage primaryStage = (Stage) stackPane.getScene().getWindow();
            Scene primaryScene = primaryStage.getScene();

            Rectangle overlay = new Rectangle(primaryScene.getWidth(), primaryScene.getHeight());
            overlay.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.4));

            primaryScene.widthProperty().addListener((obs, oldVal, newVal) -> overlay.setWidth(newVal.doubleValue()));
            primaryScene.heightProperty().addListener((obs, oldVal, newVal) -> overlay.setHeight(newVal.doubleValue()));

            Pane rootPane = (Pane) primaryScene.getRoot();
            rootPane.getChildren().add(overlay);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/AddVehicle.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));

            stage.setOnHidden(e -> rootPane.getChildren().remove(overlay));

            stage.showAndWait();
            refreshVehicleList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showEditPopup(Vehicle vehicle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/UpdateVehicle.fxml"));
            Parent root = loader.load();

            UpdateVehicleController controller = loader.getController();
            controller.setVehicleData(vehicle);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier véhicule");
            stage.showAndWait();

            loadVehicles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshVehicleList() {
        filteredVehicles = new ArrayList<>(vehicles);
        pagination.setPageCount((int) Math.ceil((double) filteredVehicles.size() / ROWS_PER_PAGE));
        updateTable(pagination.getCurrentPageIndex());
    }

    public void reloadVehicleList() {
        loadVehicles();
        handleSearch();
    }

    private void handleSearch() {
        String keyword = searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
        Type selectedType = statusComboBox.getValue();

        filteredVehicles = vehicles.stream()
                .filter(v ->
                        (v.getBrand() != null && v.getBrand().toLowerCase().contains(keyword)
                                || v.getModel() != null && v.getModel().toLowerCase().contains(keyword)
                                || v.getLicensePlate() != null && v.getLicensePlate().toLowerCase().contains(keyword))
                                && (selectedType == null || (v.getType() != null && v.getType() == selectedType))
                )
                .collect(Collectors.toList());

        pagination.setPageCount((int) Math.ceil((double) filteredVehicles.size() / ROWS_PER_PAGE));
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
                    Vehicle vehicle = getTableView().getItems().get(getIndex());
                    if (vehicle != null) showEditPopup(vehicle);
                });

                deleteIcon.setOnMouseClicked(event -> {
                    Vehicle vehicle = getTableView().getItems().get(getIndex());
                    if (vehicle != null) confirmDelete(vehicle);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hbox);
                }
            }
        });
    }
}
