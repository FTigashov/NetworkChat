module com.personal.networkchat {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.personal.networkchat to javafx.fxml;
    exports com.personal.networkchat;
}