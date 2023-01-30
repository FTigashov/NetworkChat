package com.personal.networkchat.server.handler;

import com.personal.networkchat.server.ServerConfiguration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.*;

public class ClientHandler {
    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTH_SUCCESS_CMD_PREFIX = "/auth_success"; // + send username
    private static final String AUTH_ERROR_CMD_PREFIX = "/auth_error"; // + error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/client_msg"; // + client message
    private static final String SERVER_MSG_CMD_PREFIX = "/server_msg"; // + server message
    private static final String PRIVATE_MSG_CMD_PREFIX = "/private_msg"; // + private message
    private static final String STOP_SERVER_CMD_PREFIX = "/stop_server_msg"; // + stop server
    private static final String STOP_CLIENT_CMD_PREFIX = "/stop_client_msg"; // + stop client

    private static final Logger logger;
    private static final Handler clientLoggerHandler;

    private ServerConfiguration serverConfiguration;
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;

    static {
        logger = Logger.getLogger(ServerConfiguration.class.getName());
        logger.setLevel(Level.ALL);
        try {
            clientLoggerHandler = new FileHandler("src/main/resources/logs/clientHandlerLogs.txt");
            clientLoggerHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(clientLoggerHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ClientHandler(ServerConfiguration serverConfiguration, Socket clientSocket) {
        this.serverConfiguration = serverConfiguration;
        this.clientSocket = clientSocket;
    }

    public void handle() throws IOException {
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());

        new Thread(() -> {
            try {
                authentication();
                readMessage();
            } catch (IOException e) {
                logger.severe(String.format("%s %s %s", e.getClass(), e.getCause(), e.getMessage()));
                e.printStackTrace();
            }

        }).start();
    }

    private void authentication() throws IOException {
        while (true) {
            String message = in.readUTF();
            if (message.startsWith(AUTH_CMD_PREFIX)) {
                out.writeUTF(AUTH_SUCCESS_CMD_PREFIX + " / authentication success");
            } else {
                out.writeUTF(AUTH_ERROR_CMD_PREFIX + " / authentication error");
                logger.severe("Failed authentication attempt");
            }
        }
    }

    private void readMessage() {

    }
}
