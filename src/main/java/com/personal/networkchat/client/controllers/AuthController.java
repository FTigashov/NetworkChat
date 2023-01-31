package com.personal.networkchat.client.controllers;

import com.personal.networkchat.client.ClientApp;
import com.personal.networkchat.client.models.Network;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class AuthController {
    @FXML
    private Button authBtn;

    @FXML
    private TextField loginField;

    @FXML
    private TextField pwdField;

    @FXML
    private Text registerBtn;

    private Network network;
    private ClientApp clientApp;

    @FXML
    void checkAuth(MouseEvent event) {

    }

    @FXML
    void openRegisterView(MouseEvent event) {

    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
    }
}
