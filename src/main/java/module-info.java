module org.esprit.tripnship {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.esprit.tripnship to javafx.fxml;
    exports org.esprit.tripnship;
}