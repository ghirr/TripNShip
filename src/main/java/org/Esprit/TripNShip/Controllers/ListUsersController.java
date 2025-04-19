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
import org.Esprit.TripNShip.Entities.Gender;
import org.Esprit.TripNShip.Entities.Role;
import org.Esprit.TripNShip.Entities.User;
import org.Esprit.TripNShip.Services.UserService;
import org.Esprit.TripNShip.Utils.Shared;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ListUsersController {

    public Button searchButton;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, ImageView> colProfile;
    @FXML
    private TableColumn<User, String> colFirstName;
    @FXML
    private TableColumn<User, String> colLastName;
    @FXML
    private TableColumn<User, String> colEmail;
    @FXML
    private TableColumn<User, String> colBirthDayDate;
    @FXML
    private TableColumn<User, String> colHireDate;
    @FXML
    private TableColumn<User, String> colRole;
    @FXML
    private TableColumn<User, String> colPhoneNumber;
    @FXML
    private TableColumn<User, String> colGender;
    @FXML
    private TableColumn<User, String> colAddress;
    @FXML
    private TableColumn<User, Double> colSalary;
    @FXML
    private TableColumn<User, Void> colActions;
    @FXML
    private TextField searchField;
    @FXML
    private Pagination pagination; // Ajout de la pagination
    @FXML
    private StackPane stackPane;


    private ObservableList<User> users = FXCollections.observableArrayList();
    private List<User> filteredUsers = new ArrayList<>();

    private static final int ROWS_PER_PAGE = 10;
    private UserService userService;
    private PauseTransition pause;

    @FXML
    public void initialize() {
        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox.setMargin(searchField, new Insets(0, 0, 20, 0));
        userService = new UserService();

        loadUsers();
        configureTable();
        pagination.setPageCount((int) Math.ceil((double) users.size() / ROWS_PER_PAGE));
        pagination.setCurrentPageIndex(0);
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> updateTable(newIndex.intValue()));
        pause = new PauseTransition(Duration.millis(100));
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            pause.setOnFinished(e -> handleSearch());
            pause.playFromStart();
        });

//        usersTable.setSelectionModel(null);
    }

    private void loadUsers() {
        List<User> userList = userService.getAll();
        users.setAll(userList);
        usersTable.setItems(FXCollections.observableArrayList(userList));
    }

    private void confirmDelete(User user) {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Confirmation");
//        alert.setHeaderText("Are you sure to delete this user ?");
//        alert.setContentText(user.getFirstName() + " " + user.getLastName());

        Optional<ButtonType> result = Shared.deletePopUP("Are you sure to delete this user ?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
                userService.delete(user);
                users.remove(user);
                refreshUserList();
        }
    }

    private void configureTable() {
        colProfile.setCellValueFactory(cellData -> {
            String imagePath = cellData.getValue().getProfilePhoto();
            Image image = new Image(imagePath);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            return new SimpleObjectProperty<>(imageView);
        });
        colProfile.setSortable(false);
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colBirthDayDate.setCellValueFactory(new PropertyValueFactory<>("birthdayDate"));
//        colHireDate.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
        colRole.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(formatRole(cellData.getValue().getRole())));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colGender.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(formatGender(cellData.getValue().getGender())));
//        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
//        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        setColActions();
        colActions.setSortable(false);
    }

    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredUsers.size());

        // Create a sublist for the current page
        List<User> pageItems = filteredUsers.isEmpty() ?
                Collections.emptyList() :
                filteredUsers.subList(fromIndex, toIndex);

        usersTable.setItems(FXCollections.observableArrayList(pageItems));
    }


    @FXML
    private void showAddEmployeePopup() {
        try {
            // Get the main stage
            Stage primaryStage = (Stage) stackPane.getScene().getWindow();
            Scene primaryScene = primaryStage.getScene();

            // Create a semi-transparent overlay
            Rectangle overlay = new Rectangle();
            overlay.setWidth(primaryScene.getWidth());
            overlay.setHeight(primaryScene.getHeight());
            overlay.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.4)); // Dark transparent gray

            // Ensure overlay resizes with window
            primaryScene.widthProperty().addListener((obs, oldVal, newVal) -> overlay.setWidth(newVal.doubleValue()));
            primaryScene.heightProperty().addListener((obs, oldVal, newVal) -> overlay.setHeight(newVal.doubleValue()));

            // Add overlay to the root container of the main stage
            Pane rootPane = (Pane) primaryScene.getRoot();
            rootPane.getChildren().add(overlay);

            // Load popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addUser.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));

            // Remove overlay when popup is closed
            stage.setOnHidden(e -> rootPane.getChildren().remove(overlay));

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshUserList() {
        filteredUsers = users;
//        usersTable.setItems(FXCollections.observableArrayList(users));
//        pagination.setPageCount((int) Math.ceil((double) users.size() / ROWS_PER_PAGE));
        updateTable(pagination.getCurrentPageIndex());
        setColActions();
    }
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase();

        if (!keyword.isEmpty()) {
            List<User> filteredList = users.stream()
                    .filter(u ->
                            u.getFirstName().toLowerCase().contains(keyword)
                                    || u.getLastName().toLowerCase().contains(keyword)
                                    || u.getEmail().contains(keyword)
                                    || u.getRole().toString().toLowerCase().contains(keyword)
                                    || (u.getGender() != null && u.getGender().toString().toLowerCase().contains(keyword)))
                    .collect(Collectors.toList());

            usersTable.setItems(FXCollections.observableArrayList(filteredList));
        } else {
            refreshUserList();
//            pagination.setCurrentPageIndex(0);
        }
//        usersTable.refresh();
    }
    private String formatRole(Role role) {
        return switch (role) {
            case TOUR_COORDINATOR -> "Tour Coordinator";
            case TRAVEL_ORGANIZER -> "Travel Organizer";
            case ACCOMMODATION_SPECIALIST -> "Accommodation Specialist";
            case SHIPPING_COORDINATOR -> "Shipping Coordinator";
            case CLIENT -> "Client";
            case ADMIN -> "Admin";
        };
    }

    private String formatGender(Gender gender) {
        if (gender == null) return "";
        return switch (gender) {
            case MALE -> "Male";
            case FEMALE -> "Female";
            default -> "null";
        };
    }
    private void setColActions(){

        colActions.setCellFactory(param -> new TableCell<User, Void>() {
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


//                editIcon.setOnMouseClicked(event -> showEditPopup(getTableView().getItems().get(getIndex())));
                deleteIcon.setOnMouseClicked(event -> confirmDelete(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

}
