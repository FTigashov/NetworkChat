package com.personal.networkchat.client.models;

import com.personal.networkchat.client.controllers.ChatController;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network implements NetworkModelFunctions {
    private final String host;
    private final int port;
    private final String DEFAULT_HOST = "localhost";
    private final int DEFAULT_PORT = 8186;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String REG_CMD_PREFIX = "/reg"; // + login + password
    private static final String AUTH_SUCCESS_CMD_PREFIX = "/auth_success"; // + send username
    private static final String REG_SUCCESS_CMD_PREFIX = "/reg_success"; // + send username
    private static final String CLIENT_MSG_CMD_PREFIX = "/client_msg"; // + client message
    private static final String SERVER_MSG_CMD_PREFIX = "/server_msg"; // + server message
    private static final String PRIVATE_MSG_CMD_PREFIX = "/private_msg"; // + private message
    private static final String STOP_SERVER_CMD_PREFIX = "/stop_server_msg"; // + stop server
    private static final String STOP_CLIENT_CMD_PREFIX = "/stop_client_msg"; // + stop client
    private static final String LIST_OF_CHAT_MEMBERS = "/list_of_chat_members"; // + list of chat members




    private String fullname;

    public Network() {
        this.host = DEFAULT_HOST;
        this.port = DEFAULT_PORT;
    }

    public Network(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() {
        try {
            socket = new Socket(host, port);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
//            logger.severe("Failed connection...");
            throw new RuntimeException(e);
        }

    }

    public void waitMessage(ChatController chatController) {
        Thread thread = new Thread(() ->{
            try {
                while (true) {
                    String message = in.readUTF();
                    if (message.startsWith(CLIENT_MSG_CMD_PREFIX) || message.startsWith(PRIVATE_MSG_CMD_PREFIX)) {
                        String[] parts = message.split("\\s+", 4);
                        String sender = parts[1] + " " + parts[2];
                        String messageFromSender = parts[3];

                        Platform.runLater(() -> chatController.addMessage(String.format("%s: %s", sender, messageFromSender)));
                    } else if (message.startsWith(SERVER_MSG_CMD_PREFIX)) {
                        String[] parts = message.split("\\s+", 2);
                        String serverMessage = parts[1];

                        Platform.runLater(() -> chatController.addServerMessage(serverMessage));
                    } else if (message.startsWith(LIST_OF_CHAT_MEMBERS)) {
                        message = message.substring(message.indexOf('[') + 1, message.indexOf(']'));
                        String[] users = message.split(", ");
                        Platform.runLater(() -> chatController.refreshChatMembersList(users));
                    } else {
//                        Platform.runLater(() -> chatController.showError());
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        });

        thread.setDaemon(true);
        thread.start();
    }


    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
//            logger.severe("Failed to send message...");
            throw new RuntimeException(e);
        }
    }

    public String sendAuthMessage(String login, String password) {
        try {
            out.writeUTF(String.format("%s %s %s", AUTH_CMD_PREFIX, login, password));
            String response = in.readUTF();
            if (response.startsWith(AUTH_SUCCESS_CMD_PREFIX)) {
                String[] userName = response.split(" ", 4);
                String surname = userName[1];
                String name = userName[2];
                this.fullname = String.format("%s %s", surname, name);
                return null;
            } else return response.split("\\s+", 2)[1];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String sendRegMessage(String name, String surname, String login, String password) {
        try {
            String formMessage = String.format("%s %s %s %s %s", REG_CMD_PREFIX, name, surname, login, password);
            out.writeUTF(formMessage);
            String response = in.readUTF();
            if (response.startsWith(REG_SUCCESS_CMD_PREFIX)) return null;
            else  return response.split("\\s+", 2)[1];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFullname() {
        return fullname;
    }

    public void sendPrivateMessage(String selectedRecipient, String message) {
        sendMessage(String.format("%s %s %s", PRIVATE_MSG_CMD_PREFIX, selectedRecipient, message));
    }
}
