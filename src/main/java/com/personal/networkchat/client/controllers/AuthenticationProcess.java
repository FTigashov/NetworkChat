package com.personal.networkchat.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public interface AuthenticationProcess {
    @FXML
    void createAuthProcess(ActionEvent event);

    @FXML
    void showError(String errorType);

    void clearAllField();
}
