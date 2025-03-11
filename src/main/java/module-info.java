module org.esprit.tripnship {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.esprit.tripnship to javafx.fxml;
    exports org.esprit.tripnship;
}