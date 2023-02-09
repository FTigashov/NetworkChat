package com.personal.networkchat.client.models;

import com.personal.networkchat.client.controllers.ChatController;

public interface NetworkModelFunctions {
    void connect();

    void waitMessage(ChatController chatController);


    void sendMessage(String message);

    String sendAuthMessage(String login, String password);

    String sendRegMessage(String name, String surname, String login, String password);


    void sendPrivateMessage(String selectedRecipient, String message);
}
