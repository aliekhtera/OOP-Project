module com.front {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.front to javafx.fxml;
    exports com.front;
}