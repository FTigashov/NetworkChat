package com.personal.networkchat.server.handler;

import com.personal.networkchat.server.ServerConfiguration;
import com.personal.networkchat.server.authentication.AuthService;

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

    private ServerConfiguration serverConfiguration;
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private String fullname;


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
//                logger.severe(String.format("%s %s %s", e.getClass(), e.getCause(), e.getMessage()));
                e.printStackTrace();
            }

        }).start();
    }

    private void authentication() throws IOException {
        while (true) {
            String message = in.readUTF();
            if (message.startsWith(AUTH_CMD_PREFIX)) {
                boolean isSuccessAuth = processAuthentication(message);
                if (isSuccessAuth) {
//                    logger.info(AUTH_SUCCESS_CMD_PREFIX + " | authentication success");
                    System.out.println(AUTH_SUCCESS_CMD_PREFIX + " | authentication success");
                    break;
                }
            } else {
                out.writeUTF(AUTH_ERROR_CMD_PREFIX + " | authentication error");
                System.out.println(AUTH_ERROR_CMD_PREFIX + " | authentication error");
//                logger.severe("Failed authentication attempt");
            }
        }
    }

    private boolean processAuthentication(String message) throws IOException {
        String[] parts = message.split("\\s+", 3);
        if (parts.length != 3) out.writeUTF(AUTH_ERROR_CMD_PREFIX + " | authentication error");
        String login = parts[1];
        String password = parts[2];

        AuthService authService = serverConfiguration.getAuthService();
        authService.startAuthentication();
        fullname = authService.getUserNameByLoginAndPassword(login, password);
        if (fullname != null) {
            if (serverConfiguration.isLoginBusy(fullname)) {
                out.writeUTF(AUTH_ERROR_CMD_PREFIX + " | user is already busy");
                return false;
            }
            out.writeUTF(AUTH_SUCCESS_CMD_PREFIX + " | " + fullname);
            serverConfiguration.subscribe(this);
            System.out.println("User " + fullname + " is connected");
            return true;
        } else {
            out.writeUTF(AUTH_ERROR_CMD_PREFIX + " | login or password incorrect");
            System.out.println(AUTH_ERROR_CMD_PREFIX + " | login or password incorrect");
//            logger.info("Incorrect login or password was entered by user " + login);
        }
        authService.endAuthentication();
        return false;
    }

    private void readMessage() throws IOException {
        while (true) {
            String message = in.readUTF();
            System.out.println("message | " + fullname + ": " + message);
            if (message.startsWith(STOP_SERVER_CMD_PREFIX)) {
                System.exit(1);
            } else if (message.startsWith(STOP_CLIENT_CMD_PREFIX)) {
                return;
            } else if (message.startsWith(PRIVATE_MSG_CMD_PREFIX)) {
                //TODO
            } else {
                serverConfiguration.broadcastMessage(message, this);
            }
        }
    }

    public void sendMessage(String sender, String message) throws IOException {
        out.writeUTF(String.format("%s %s %s", CLIENT_MSG_CMD_PREFIX, sender, message));
    }

    public String getFullname() {
        return fullname;
    }
}
