package com.personal.networkchat.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

public class ChatController {
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
}
