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
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.stage.StageStyle;
import org.Esprit.TripNShip.Entities.CircuitBooking;
import org.Esprit.TripNShip.Entities.RentalAgency;
import org.Esprit.TripNShip.Services.RentalAgencyService;
import org.Esprit.TripNShip.Utils.Shared;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ListViewRentalAgencyController {

    private static final int ROWS_PER_PAGE = 6;

    // UI Elements
    @FXML private ComboBox<String> entriesComboBox;
    @FXML private Region spacer;
    @FXML private TextField searchField;
    @FXML private Button addAgencyButton;
    @FXML private TableView<RentalAgency> agencyTable;
    @FXML private TableColumn<RentalAgency, String> nameColumn;
    @FXML private TableColumn<RentalAgency, String> addressColumn;
    @FXML private TableColumn<RentalAgency, String> contactColumn;
    @FXML private TableColumn<RentalAgency, Float> ratingColumn;
    @FXML private TableColumn<RentalAgency, Void> actionColumn;
    @FXML private Pagination pagination;
    @FXML
    private StackPane stackPane;


    // Services
    private final RentalAgencyService rentalAgencyService = new RentalAgencyService();

    // Data
    private ObservableList<RentalAgency> agencyList = FXCollections.observableArrayList();
    private List<RentalAgency> filteredAgency;

    @FXML
    private void initialize() {
        setupTableColumns();
        setupSearchField();
        setupButtons();
        loadAgenciesFromDatabase();
        setupActionButtons();
        setupPagination();

        agencyTable.setRowFactory(tv -> {
            TableRow<RentalAgency> row = new TableRow<>();
            row.setPrefHeight(40);
            return row;
        });
    }

    // Table setup
    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nameAgency"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("addressAgency"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactAgency"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
    }

    private void loadAgenciesFromDatabase() {
        List<RentalAgency> agencies = rentalAgencyService.getAll();
        agencyList.setAll(agencies);
        filteredAgency = agencyList;
        updateTable(0);
        pagination.setPageCount((int) Math.ceil((double) filteredAgency.size() / ROWS_PER_PAGE));
    }

    private void setupPagination() {
        pagination.setMaxPageIndicatorCount(3);
        pagination.setPageFactory(this::createPage);
    }

    private VBox createPage(int pageIndex) {
        updateTable(pageIndex);
        return new VBox(agencyTable);
    }

    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredAgency.size());
        agencyTable.setItems(FXCollections.observableArrayList(filteredAgency.subList(fromIndex, toIndex)));
    }

    // Search setup
    private void setupSearchField() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch());
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase();

        if (!keyword.isEmpty()) {
            filteredAgency = agencyList.stream()
                    .filter(u ->
                            u.getNameAgency().toLowerCase().contains(keyword)
                                    || u.getContactAgency().toLowerCase().contains(keyword)
                                    || u.getAddressAgency().toLowerCase().contains(keyword)
                                    || String.valueOf(u.getRating()).toLowerCase().contains(keyword)
                    )
                    .collect(Collectors.toList());
        } else {
            filteredAgency = agencyList;
        }

        pagination.setPageCount((int) Math.ceil((double) filteredAgency.size() / ROWS_PER_PAGE));
        updateTable(0);
        pagination.setCurrentPageIndex(0);
    }

    private void refreshUserList() {
        loadAgenciesFromDatabase();
        pagination.setCurrentPageIndex(0);
    }

    // Button setup
    private void setupButtons() {
        addAgencyButton.setOnAction(event -> openAddAgencyForm());

    }

    // Action buttons in table
    private void setupActionButtons() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
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
                    RentalAgency agency = getTableView().getItems().get(getIndex());
                    handleEditAgency(agency);
                });

                deleteIcon.setOnMouseClicked(event -> {
                    RentalAgency agency = getTableView().getItems().get(getIndex());
                    handleDeleteAgency(agency);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    // CRUD Actions
    private void handleEditAgency(RentalAgency agency) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/UpdateRentalAgency.fxml"));
            Parent root = loader.load();

            UpdateRentalAgencyController controller = loader.getController();
            controller.setAgencyData(agency);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Agence de Location");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadAgenciesFromDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void handleDeleteAgency(RentalAgency agency) {
        Optional<ButtonType> result = Shared.deletePopUP("Are you sure to delete this booking?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            rentalAgencyService.delete(agency);
            agencyList.remove(agency);
            loadAgenciesFromDatabase();
        }
    }


    @FXML
    private void openAddAgencyForm() {
        try {
            // Obtenir la scène principale
            Stage primaryStage = (Stage) stackPane.getScene().getWindow();
            Scene primaryScene = primaryStage.getScene();

            // Créer un overlay sombre semi-transparent
            Rectangle overlay = new Rectangle(primaryScene.getWidth(), primaryScene.getHeight());
            overlay.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.4));
            overlay.setMouseTransparent(true); // Laisse passer les événements si besoin

            // Adapter l’overlay au redimensionnement
            primaryScene.widthProperty().addListener((obs, oldVal, newVal) -> overlay.setWidth(newVal.doubleValue()));
            primaryScene.heightProperty().addListener((obs, oldVal, newVal) -> overlay.setHeight(newVal.doubleValue()));

            // Ajouter l’overlay à la racine
            Pane rootPane = (Pane) primaryScene.getRoot();
            rootPane.getChildren().add(overlay);

            // Charger le fichier FXML du formulaire
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CircuitManagementFXML/AddRentalAgency.fxml"));
            Parent formRoot = loader.load();

            // Créer une nouvelle fenêtre modale
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(primaryStage);
            modalStage.initStyle(StageStyle.UNDECORATED);
            modalStage.setScene(new Scene(formRoot));

            // Supprimer l’overlay et recharger les agences à la fermeture
            modalStage.setOnHidden(event -> {
                rootPane.getChildren().remove(overlay);
                loadAgenciesFromDatabase();
            });

            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            // Nettoyage de l’overlay en cas d’échec
            Stage primaryStage = (Stage) stackPane.getScene().getWindow();
            Pane rootPane = (Pane) primaryStage.getScene().getRoot();
            rootPane.getChildren().removeIf(node -> node instanceof Rectangle);
        }
    }


}
