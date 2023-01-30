module com.personal.networkchat {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;


    opens com.personal.networkchat to javafx.fxml;
    exports com.personal.networkchat;
    exports com.personal.networkchat.controllers;
}