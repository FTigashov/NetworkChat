package com.personal.networkchat.server.handler;

import com.personal.networkchat.server.ServerConfiguration;
import com.personal.networkchat.server.authentication.AuthService;
import com.personal.networkchat.server.models.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler extends LoggingConfig {
    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String REG_CMD_PREFIX = "/reg"; // + login + password
    private static final String AUTH_SUCCESS_CMD_PREFIX = "/auth_success"; // + send username
    private static final String REG_SUCCESS_CMD_PREFIX = "/reg_success"; // + send username
    private static final String AUTH_ERROR_CMD_PREFIX = "/auth_error"; // + error message
    private static final String REG_ERROR_CMD_PREFIX = "/reg_error"; // + error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/client_msg"; // + client message
    private static final String SERVER_MSG_CMD_PREFIX = "/server_msg"; // + server message
    private static final String PRIVATE_MSG_CMD_PREFIX = "/private_msg"; // + private message
    private static final String STOP_SERVER_CMD_PREFIX = "/stop_server_msg"; // + stop server
    private static final String STOP_CLIENT_CMD_PREFIX = "/stop_client_msg"; // + stop client
    private static final String LIST_OF_CHAT_MEMBERS = "/list_of_chat_members"; // + list of chat members

    private ServerConfiguration serverConfiguration;
    private AuthService authService;
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private String fullname;


    public ClientHandler(ServerConfiguration serverConfiguration, Socket clientSocket) {
        this.serverConfiguration = serverConfiguration;
        this.clientSocket = clientSocket;
        authService = serverConfiguration.getAuthService();
    }

    public void handle() throws IOException {
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());

        new Thread(() -> {
            try {
                waitForStartMessage();
                readMessage();
            } catch (IOException e) {
                admin.fatal(String.format("%s %s %s", e.getClass(), e.getCause(), e.getMessage()));
                admin_console.fatal(String.format("%s %s %s", e.getClass(), e.getCause(), e.getMessage()));
                serverConfiguration.unSubscribe(this);
                admin.info(String.format("%s left the chat", getFullname()));
                admin_console.info(String.format("%s left the chat", getFullname()));
                try {
                    serverConfiguration.broadcastDisconnectUser(this);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                e.printStackTrace();
            }

        }).start();
    }

    private void waitForStartMessage() throws IOException {
        while (true) {
            String message = in.readUTF();
            if (message.startsWith(AUTH_CMD_PREFIX)) {
                boolean isSuccessAuth = processAuthentication(message);
                if (isSuccessAuth) {
                    admin.info(AUTH_SUCCESS_CMD_PREFIX + " | authentication success");
                    admin_console.info(AUTH_SUCCESS_CMD_PREFIX + " | authentication success");
                    break;
                } else {
                    out.writeUTF(AUTH_ERROR_CMD_PREFIX + " | authentication error");
                    admin.fatal("Failed authentication attempt");
                    admin_console.fatal("Failed authentication attempt");
                }
            } else if (message.startsWith(REG_CMD_PREFIX)) {
                boolean isSuccessReg = processRegistration(message);
                if (isSuccessReg) {
                    admin.info("New user is registered ");
                    admin_console.info("New user is registered ");
                    break;
                } else {
                    out.writeUTF(REG_ERROR_CMD_PREFIX + " this user is already exists");
                    admin.error(String.format("User with this login is already exists"));
                    admin_console.error(String.format("User with this login is already exists"));
                }
            }
        }
    }


    // Copy–Paste
    private boolean processRegistration(String message) throws IOException {
        String[] parts = message.split("\\s+", 5);
        if (parts.length != 5) out.writeUTF(AUTH_ERROR_CMD_PREFIX + " | registration error");
        String name = parts[1];
        String surname = parts[2];
        String login = parts[3];
        String password = parts[4];

        authService.openConnection();
        
        User receivedNewUser = authService.insertNewUser(new User(name, surname, login, password));
        if (receivedNewUser != null) {
            fullname = String.format("%s %s", receivedNewUser.getName(), receivedNewUser.getSurname());
            out.writeUTF(REG_SUCCESS_CMD_PREFIX + " " + fullname);
            authService.closeConnection();
            return true;
        }
        return false;
    }

    // Copy–Paste
    private boolean processAuthentication(String message) throws IOException {
        String[] parts = message.split("\\s+", 3);
        if (parts.length != 3) out.writeUTF(AUTH_ERROR_CMD_PREFIX + " | authentication error");
        String login = parts[1];
        String password = parts[2];

        authService.openConnection();
        User receivedUser = authService.getUserNameByLoginAndPassword(login, password);
        if (receivedUser != null) {
            fullname = String.format("%s %s", receivedUser.getName(), receivedUser.getSurname());
            if (serverConfiguration.isLoginBusy(fullname)) {
                admin.error("Attempt to log in to an already authorized account");
                admin_console.error("Attempt to log in to an already authorized account");
                out.writeUTF(AUTH_ERROR_CMD_PREFIX + " user is already busy");
                return false;
            }
            out.writeUTF(AUTH_SUCCESS_CMD_PREFIX + " " + fullname);
            serverConfiguration.subscribe(this);
            admin.info("User " + fullname + " is connected");
            admin_console.info("User " + fullname + " is connected");
            serverConfiguration.sendChatMembersList(this);
            serverConfiguration.broadcastMessage(String.format("%s has connected to the chat", fullname), this, true);
            authService.closeConnection();
            return true;
        } else {
            out.writeUTF(REG_ERROR_CMD_PREFIX + " | login or password incorrect");
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
                String userFullname = msg_parts[1] + " " + msg_parts[2];
                String userMessage = msg_parts[3];
                serverConfiguration.privateMessage(this, userFullname, userMessage);
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
