package com.personal.networkchat.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
    @FXML
    private TextArea chatHistory;

    @FXML
    private Menu fileMB;

    @FXML
    private Menu helpMB;

    @FXML
    private TextField inputField;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Button sendButton;

    @FXML
    private ListView<?> userList;

    @FXML
    private Text userName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Font mainFont = new Font("Arial", 16);
        chatHistory.setFont(mainFont);
        inputField.setFont(mainFont);
        sendButton.setOnAction(e -> sendMessage());
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (message.isBlank()) {
            inputField.clear();
            return;
        }
        inputField.clear();
        addMessage(message);
    }

    private void addMessage(String message) {
        chatHistory.appendText(message);
        chatHistory.appendText(System.lineSeparator());
    }
}
