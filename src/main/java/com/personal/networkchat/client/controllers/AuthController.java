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

    private final String EMPTY_FIELDS_ERROR = "emptyFields";
    private final String ACCOUNT_ERROR = "authError";

    @FXML
    void checkAuth(MouseEvent event) {
        String login = loginField.getText().trim();
        String password = pwdField.getText().trim();
        if (login.length() == 0 || password.length() == 0) {
            showError(EMPTY_FIELDS_ERROR);
            return;
        }

//        String authErrorMessage = network.sendAuthMessage(login, password);
//        if (authErrorMessage == null) {
//            clientApp.openChatDialog();
//        } else {
//
//        }
    }

    @FXML
    void showError(String errorType) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        switch (errorType) {
            case "emptyFields":
                alert.setTitle("Authentication error");
                alert.setHeaderText("Login or password is empty");
                alert.setContentText("Make sure that all fields must be filled in.");
                break;
            case "authError":
                alert.setTitle("Authentication error");
                alert.setHeaderText("Incorrect login or password");
                alert.setContentText("Make sure that your login and password are correct.");
                break;
        }
        alert.show();
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
