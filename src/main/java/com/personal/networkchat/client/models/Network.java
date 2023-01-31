package com.personal.networkchat.client.models;

import com.personal.networkchat.client.controllers.ChatController;
import com.personal.networkchat.server.ServerConfiguration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.*;

public class Network {
    private final String host;
    private final int port;
    private final String DEFAULT_HOST = "localhost";
    private final int DEFAULT_PORT = 8186;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTH_SUCCESS_CMD_PREFIX = "/auth_success"; // + send username
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
                    chatController.addMessage(message);
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

    public String getFullname() {
        return fullname;
    }
}
