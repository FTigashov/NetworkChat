module com.personal.networkchat {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires java.logging;
    requires log4j;
    requires java.sql;


    exports com.personal.networkchat.client.controllers;
    exports com.personal.networkchat.client;
    opens com.personal.networkchat.client to javafx.fxml;
    opens com.personal.networkchat.client.controllers to javafx.fxml;
}