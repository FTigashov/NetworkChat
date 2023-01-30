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

    private static final Logger logger;
    private static final Handler serverLoggerHandler;

    static {
        logger = Logger.getLogger(ServerConfiguration.class.getName());
        logger.setLevel(Level.ALL);
        try {
            serverLoggerHandler = new FileHandler("src/main/resources/logs/clientConnectionLogs.txt");
            serverLoggerHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(serverLoggerHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
            logger.severe("Failed connection...");
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
            logger.severe("Failed to send message...");
            throw new RuntimeException(e);
        }
    }
}
