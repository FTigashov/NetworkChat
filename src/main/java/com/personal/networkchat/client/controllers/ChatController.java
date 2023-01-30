package com.personal.networkchat.client.controllers;

import com.personal.networkchat.client.models.Network;
import javafx.collections.FXCollections;
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
    private ListView<String> userList;

    @FXML
    private Text userName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userList.setItems(FXCollections.observableArrayList("Ivan", "Andrew", "Nikolay"));
        Font mainFont = new Font("Arial", 14);
        chatHistory.setFont(mainFont);
        inputField.setFont(mainFont);
        sendButton.setOnAction(e -> sendMessage());
    }

    private Network network;

    public void setNetwork(Network network) {
        this.network = network;
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        inputField.clear();
        if (message.isBlank()) {
            return;
        }
        network.sendMessage(message);
//        addMessage(message);
    }

    public void addMessage(String message) {
        chatHistory.appendText(message);
        chatHistory.appendText(System.lineSeparator());
    }
}
