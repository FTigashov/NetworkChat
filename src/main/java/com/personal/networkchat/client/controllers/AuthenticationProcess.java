package com.personal.networkchat.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public interface AuthenticationProcess {
    @FXML
    void createAuthProcess(MouseEvent event);

    @FXML
    void showError(String errorType);

    void clearAllField();
}
