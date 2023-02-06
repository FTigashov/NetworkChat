package com.personal.networkchat.client.controllers;

import com.personal.networkchat.client.ClientApp;
import com.personal.networkchat.client.models.Network;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

    private final String EMPTY_FIELDS_ERROR = "emptyFields";
    private final String ACCOUNT_ERROR = "authError";
    private final String USER_IS_BUSY = "user_is_busy";

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
        String name, surname, login, password;
        name = nameField.getText().trim();
        surname = surnameField.getText().trim();
        login = loginField.getText().trim();
        password = pwdField.getText().trim();
        if (name.isBlank() ||
        surname.isBlank() ||
        login.isBlank() ||
        password.isBlank()) {
            showError(EMPTY_FIELDS_ERROR);
            return;
        }

        String regErrorMessage = null;
    }

    @FXML
    void showError(String errorType) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Registration error");
        switch (errorType) {
            case "emptyFields":
                alert.setHeaderText("Some of these fields are empty");
                alert.setContentText("Make sure that all fields must be filled in.");
                break;
            case "authError":
                alert.setHeaderText("Incorrect login or password");
                alert.setContentText("Make sure that your login and password are correct.");
                break;
            case "user_is_busy":
                alert.setHeaderText("This user has already logged in to the chat");
                alert.setContentText("To continue,\nyou need to log out of the chat,\nor log in to another account.");
                break;
        }
        alert.show();
    }
}
