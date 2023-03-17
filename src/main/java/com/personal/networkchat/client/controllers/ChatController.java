package com.personal.networkchat.client.controllers;

import com.personal.networkchat.client.models.Network;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;

public class ChatController implements Initializable, ChatActions {
    @FXML
    private TextArea chatHistory;

    @FXML
    private Menu fileMB;

    @FXML
    private Menu helpMB;

    @FXML
    private Text userFullName;

    @FXML
    private TextField inputField;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Button sendButton;

    @FXML
    private ListView<String> userList;

    private String selectedRecipient;

    @FXML
    private Text userName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Font mainFont = new Font("Arial", 14);
        chatHistory.setFont(mainFont);
        inputField.setFont(mainFont);
        sendButton.setOnAction(e -> sendMessage());

        userList.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = userList.getSelectionModel();
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                userList.requestFocus();
                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (selectionModel.getSelectedIndices().contains(index)) {
                        selectionModel.clearSelection(index);
                        selectedRecipient = null;
                    } else {
                        selectionModel.select(index);
                        selectedRecipient = cell.getItem();
                    }
                    event.consume();
                }
            });
            return cell;
        });
    }

    private Network network;
    private File userHistoryFilePath;

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void sendMessage() {
        String message = inputField.getText().trim();
        inputField.clear();
        if (message.isBlank()) {
            return;
        }
        if (selectedRecipient != null) network.sendPrivateMessage(selectedRecipient, message);
        else network.sendMessage(message);

        addMessage(String.format("Me: %s", message));
    }

    public void setUserFullName(String fullName) {
        userFullName.setText(fullName);
    }

    public void addMessage(String message) {
        String timeStamp = DateFormat.getInstance().format(new Date());
        String messageFormat = String.format("%s\n%s\n\n", timeStamp, message);
        chatHistory.appendText(messageFormat);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(userHistoryFilePath, true))) {
            bufferedWriter.write(messageFormat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        chatHistory.appendText(timeStamp);
//        chatHistory.appendText(System.lineSeparator());
//        chatHistory.appendText(message);
//        chatHistory.appendText(System.lineSeparator());
//        chatHistory.appendText(System.lineSeparator());
    }

    public void addServerMessage(String serverMessage) {
        chatHistory.appendText(serverMessage);
        chatHistory.appendText(System.lineSeparator());
        chatHistory.appendText(System.lineSeparator());
    }

    public void refreshChatMembersList(String[] users) {
        Arrays.sort(users);
//        for (String client : users) {
//            if (client.equals(network.getFullname())) client = "Me";
//        }
        userList.getItems().clear();
        Collections.addAll(userList.getItems(), users);
    }

    @FXML
    void closeWindow(ActionEvent event) {
        Alert exitAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to leave the chat?",
                ButtonType.YES, ButtonType.CANCEL);
        exitAlert.setTitle("Warning about exit");
        exitAlert.setHeaderText("Confirm your action");
        exitAlert.showAndWait();
        if (exitAlert.getResult() == ButtonType.YES) {
            System.exit(0);
        } else exitAlert.close();
    }

    @FXML
    void showInfoAbout(ActionEvent event) {
        Alert showInfoAlert = new Alert(Alert.AlertType.INFORMATION);
        showInfoAlert.setTitle("Info about application");
        showInfoAlert.setHeaderText("Network chat v1.1");
        showInfoAlert.setContentText("In the latest update, " +
                "the possibility of registration and authorization " +
                "using a database has been added,\npassword hashing " +
                "has also been added\nfor greater security.");
        showInfoAlert.show();
    }

    public void setHistory(File userHistoryFileName) {
        userHistoryFilePath = userHistoryFileName;

        if (userHistoryFilePath.exists() && userHistoryFileName.isFile()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(userHistoryFilePath));
                String currentLine;
                while ((currentLine = bufferedReader.readLine()) != null) {
                    chatHistory.appendText(currentLine);
                    chatHistory.appendText(System.lineSeparator());
                }
                chatHistory.appendText(System.lineSeparator());
                bufferedReader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
