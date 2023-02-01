package com.personal.networkchat.server.handler;

import com.personal.networkchat.server.ServerConfiguration;
import com.personal.networkchat.server.authentication.AuthService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientHandler extends LoggingConfig {
    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTH_SUCCESS_CMD_PREFIX = "/auth_success"; // + send username
    private static final String AUTH_ERROR_CMD_PREFIX = "/auth_error"; // + error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/client_msg"; // + client message
    private static final String SERVER_MSG_CMD_PREFIX = "/server_msg"; // + server message
    private static final String PRIVATE_MSG_CMD_PREFIX = "/private_msg"; // + private message
    private static final String STOP_SERVER_CMD_PREFIX = "/stop_server_msg"; // + stop server
    private static final String STOP_CLIENT_CMD_PREFIX = "/stop_client_msg"; // + stop client
    private static final String LIST_OF_CHAT_MEMBERS = "/list_of_chat_members"; // + list of chat members

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
                admin.fatal(String.format("%s %s %s", e.getClass(), e.getCause(), e.getMessage()));
                admin_console.fatal(String.format("%s %s %s", e.getClass(), e.getCause(), e.getMessage()));
                serverConfiguration.unSubscribe(this);
                try {
                    serverConfiguration.broadcastDisconnectUser(this);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
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
                    admin.info(AUTH_SUCCESS_CMD_PREFIX + " | authentication success");
                    admin_console.info(AUTH_SUCCESS_CMD_PREFIX + " | authentication success");
                    break;
                }
            } else {
                out.writeUTF(AUTH_ERROR_CMD_PREFIX + " | authentication error");
                admin.fatal("Failed authentication attempt");
                admin_console.fatal("Failed authentication attempt");
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
                out.writeUTF(AUTH_ERROR_CMD_PREFIX + " user is already busy");
                return false;
            }
            out.writeUTF(AUTH_SUCCESS_CMD_PREFIX + " " + fullname);
            serverConfiguration.subscribe(this);
            admin.info("User " + fullname + " is connected");
            admin_console.info("User " + fullname + " is connected");
            serverConfiguration.sendChatMembersList(this);
            serverConfiguration.broadcastMessage(String.format("%s has connected to the chat", fullname), this, true);
            authService.endAuthentication();
            return true;
        } else {
            out.writeUTF(AUTH_ERROR_CMD_PREFIX + " | login or password incorrect");
            admin.error("Incorrect login or password was entered by user " + login);
            admin_console.error("Incorrect login or password was entered by user " + login);
        }
        return false;
    }

    private void readMessage() throws IOException {
        while (true) {
            String message = in.readUTF();
            admin.info("message from " + fullname + ": " + message);
            admin_console.info("message from " + fullname + ": " + message);
            if (message.startsWith(STOP_SERVER_CMD_PREFIX)) {
                System.exit(1);
            } else if (message.startsWith(STOP_CLIENT_CMD_PREFIX)) {
                serverConfiguration.unSubscribe(this);
                return;
            } else if (message.startsWith(PRIVATE_MSG_CMD_PREFIX)) {
                String[] msg_parts = message.split("\\s+", 4);
                String user_fullname = msg_parts[1] + " " + msg_parts[2];
                String user_msg = msg_parts[3];
                serverConfiguration.privateMessage(this, user_fullname, user_msg);
            } else {
                serverConfiguration.broadcastMessage(message, this);
            }
        }
    }

    public void sendMessage(String sender, String message) throws IOException {
        if (sender != null) out.writeUTF(String.format("%s %s %s", CLIENT_MSG_CMD_PREFIX, sender, message));
        else out.writeUTF(String.format("%s %s", SERVER_MSG_CMD_PREFIX, message));
    }

    public void sendPrivateMessage(String recipient, String message) throws IOException {
        out.writeUTF(String.format("%s %s %s", PRIVATE_MSG_CMD_PREFIX, recipient, message));
    }

    public String getFullname() {
        return fullname;
    }

    public void sendListOfChatMembers(List<ClientHandler> clientHandlers) throws IOException {
        List<String> listOfChatMembers = new ArrayList<>();
        for (ClientHandler clientHandler : clientHandlers) {
            listOfChatMembers.add(clientHandler.getFullname());
        }
        out.writeUTF(String.format("%s %s", LIST_OF_CHAT_MEMBERS, listOfChatMembers));
    }

    public void sendServerMessage(String serverMessage) throws IOException {
        out.writeUTF(String.format("%s %s", SERVER_MSG_CMD_PREFIX, serverMessage));
    }
}
