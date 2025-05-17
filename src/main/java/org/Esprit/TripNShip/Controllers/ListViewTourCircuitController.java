package org.Esprit.TripNShip.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.Esprit.TripNShip.Entities.TourCircuit;
import org.Esprit.TripNShip.Services.TourCircuitService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ListViewTourCircuitController {

    private static final int ROWS_PER_PAGE = 7;

    @FXML private ComboBox<String> entriesComboBox;
    @FXML private Region spacer;
    @FXML private TextField searchField;
    @FXML private Button addTourButton;
    @FXML private TableView<TourCircuit> tourTable;
    @FXML private TableColumn<TourCircuit, String> nameCircuitColumn;
    @FXML private TableColumn<TourCircuit, String> descriptionCircuitColumn;
    @FXML private TableColumn<TourCircuit, Float> priceCircuitColumn;
    @FXML private TableColumn<TourCircuit, String> durationColumn;
    @FXML private TableColumn<TourCircuit, String> destinationColumn;
    @FXML private TableColumn<TourCircuit, String> guideIncludedColumn;
    @FXML private TableColumn<TourCircuit, Void> actionColumn;
    @FXML private Pagination pagination;

    private final TourCircuitService tourCircuitService = new TourCircuitService();

    private ObservableList<TourCircuit> tourList = FXCollections.observableArrayList();
    private List<TourCircuit> filteredTour;

    @FXML
    private void initialize() {
        setupTableColumns();
        setupSearchField();
        setupButtons();
        loadTourFromDatabase();
        setupActionButtons();
        setupPagination();

        tourTable.setRowFactory(tv -> {
            TableRow<TourCircuit> row = new TableRow<>();
            row.setPrefHeight(40);
            return row;
        });
    }

    private void setupTableColumns() {
        nameCircuitColumn.setCellValueFactory(new PropertyValueFactory<>("nameCircuit"));
        descriptionCircuitColumn.setCellValueFactory(new PropertyValueFactory<>("descriptionCircuit"));
        priceCircuitColumn.setCellValueFactory(new PropertyValueFactory<>("priceCircuit"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        guideIncludedColumn.setCellValueFactory(new PropertyValueFactory<>("guideIncluded"));
    }

    private void loadTourFromDatabase() {
        List<TourCircuit> circuits = tourCircuitService.getAll();
        tourList.setAll(circuits);
        filteredTour = tourList;
        updateTable(0);
        pagination.setPageCount((int) Math.ceil((double) filteredTour.size() / ROWS_PER_PAGE));
    }

    private void setupPagination() {
        pagination.setMaxPageIndicatorCount(3);
        pagination.setPageFactory(this::createPage);
    }

    private VBox createPage(int pageIndex) {
        updateTable(pageIndex);
        return new VBox(tourTable);
    }

    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredTour.size());
        tourTable.setItems(FXCollections.observableArrayList(filteredTour.subList(fromIndex, toIndex)));
    }

    private void setupSearchField() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch());
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase();

        if (!keyword.isEmpty()) {
            filteredTour = tourList.stream()
                    .filter(circuit ->
                            circuit.getNameCircuit().toLowerCase().contains(keyword) ||
                                    circuit.getDestination().toLowerCase().contains(keyword) ||
                                    String.valueOf(circuit.getPriceCircuit()).toLowerCase().contains(keyword) ||
                                    String.valueOf(circuit.getGuideIncluded()).toLowerCase().contains(keyword)
                    )
                    .collect(Collectors.toList());
        } else {
            filteredTour = tourList;
        }

        pagination.setPageCount((int) Math.ceil((double) filteredTour.size() / ROWS_PER_PAGE));
        updateTable(0);
        pagination.setCurrentPageIndex(0);
    }

    private void setupButtons() {
        addTourButton.setOnAction(event -> openAddTourForm());
    }

    private void setupActionButtons() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final ImageView editIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icons8-edit-64.png"))));
            private final ImageView deleteIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icons8-delete-48.png"))));
            private final HBox hbox = new HBox(10, editIcon, deleteIcon);

            {
                editIcon.setFitWidth(30);
                editIcon.setFitHeight(30);
                deleteIcon.setFitWidth(30);
                deleteIcon.setFitHeight(30);

                editIcon.setStyle("-fx-cursor: hand;");
                deleteIcon.setStyle("-fx-cursor: hand;");

                editIcon.setOnMouseClicked(event -> {
                    TourCircuit circuit = getTableView().getItems().get(getIndex());
                    handleEditCircuit(circuit);
                });

                deleteIcon.setOnMouseClicked(event -> {
                    TourCircuit circuit = getTableView().getItems().get(getIndex());
                    handleDeleteCircuit(circuit);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    private void handleEditCircuit(TourCircuit circuit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/UpdateTourCircuit.fxml"));
            Parent root = loader.load();

            UpdateTourCircuitController controller = loader.getController();
            controller.setCircuitData(circuit);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier un Circuit");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadTourFromDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteCircuit(TourCircuit circuit) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Suppression Circuit");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce circuit ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                tourCircuitService.delete(circuit);
                loadTourFromDatabase();
            }
        });
    }

    private void openAddTourForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/AddTourCircuit.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Circuit");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadTourFromDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
