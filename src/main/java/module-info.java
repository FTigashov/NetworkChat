module com.personal.networkchat {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires java.logging;


    opens com.personal.networkchat to javafx.fxml;
    exports com.personal.networkchat.client.controllers;
    exports com.personal.networkchat.client;
    opens com.personal.networkchat.client to javafx.fxml;
}