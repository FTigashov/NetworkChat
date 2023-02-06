package com.personal.networkchat.client.controllers;

import com.personal.networkchat.client.ClientApp;
import com.personal.networkchat.client.models.Network;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistrationController {
    @FXML
    private Button cancelButton;

    @FXML
    private TextField loginField;

    @FXML
    private TextField nameField;

    @FXML
    private PasswordField pwdField;

    @FXML
    private Button registerButton;

    @FXML
    private TextField surnameField;

    private Network network;
    private ClientApp clientApp;

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
    }

    @FXML
    void backToAuthFrame(ActionEvent event) {
        clearAllField();
        clientApp.backToAuthDialog();
    }

    private void clearAllField() {
        nameField.clear();
        surnameField.clear();
        loginField.clear();
        pwdField.clear();
    }

    @FXML
    void startRegistration(ActionEvent event) {

    }
}
