package com.personal.networkchat.client.controllers;

import com.personal.networkchat.client.ClientApp;
import com.personal.networkchat.client.models.Network;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.*;

public class AuthController implements AuthenticationProcess {
    @FXML
    private Button authBtn;

    @FXML
    private TextField loginField;

    @FXML
    private TextField pwdField;

    @FXML
    private Hyperlink registerBtn;

    private Network network;
    private ClientApp clientApp;

    private File chatHistoryDirectory = new File("src/main/resources/chat_history");

    private final String EMPTY_FIELDS_ERROR = "emptyFields";
    private final String ACCOUNT_ERROR = "authError";
    private final String USER_IS_BUSY = "user_is_busy";


    @Override
    public void createAuthProcess(ActionEvent event) {
        String login = loginField.getText().trim();
        String password = pwdField.getText().trim();
        if (login.length() == 0 || password.length() == 0) {
            showError(EMPTY_FIELDS_ERROR);
            return;
        }

        String authErrorMessage = network.sendAuthMessage(login, password);

        if (authErrorMessage == null) {
            String userChatHistoryFileName = String.format("history_%s.txt", login);
            String fullPath = chatHistoryDirectory.getPath() + "\\" + userChatHistoryFileName;
            File checkFile = new File(fullPath);
            if (!checkFile.exists()) {
//                System.out.println("Файл не существует");
                try {
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fullPath, true));
//                    System.out.println("Файл создан!");
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            clientApp.openChatDialog(network.getFullname(), checkFile);
        } else {
            if (authErrorMessage.equals("user is already busy")) {
                showError(USER_IS_BUSY);
            } else {
                showError(ACCOUNT_ERROR);
            }
        }
    }

    @FXML
    public void showError(String errorType) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Authentication error");
        switch (errorType) {
            case "emptyFields":
                alert.setHeaderText("Login or password is empty");
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

    @FXML
    void openRegisterView(MouseEvent event) {

    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
    }


    @FXML
    void showRegView(ActionEvent event) {
        clearAllField();
        clientApp.openRegDialog();
    }

    public void clearAllField() {
        loginField.clear();
        pwdField.clear();
    }
}
