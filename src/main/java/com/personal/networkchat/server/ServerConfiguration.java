package com.personal.networkchat.server;

import com.personal.networkchat.server.authentication.AuthService;
import com.personal.networkchat.server.authentication.DataMapper;
import com.personal.networkchat.server.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;

import com.personal.networkchat.server.handler.LoggingConfig;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerConfiguration extends LoggingConfig {

    private final ServerSocket serverSocket;
    private final AuthService authService;

    private static final int DEFAULT_PORT = 8186;

    private final List<ClientHandler> clientHandlers;

    private static ServerConfiguration serverConfiguration;
    public static ServerConfiguration getInstance() {
        if (serverConfiguration == null) {
            try {
                serverConfiguration = new ServerConfiguration(DEFAULT_PORT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return serverConfiguration;
    }

    private ServerConfiguration(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        authService = new DataMapper();
        clientHandlers = new ArrayList<>();
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clientHandlers.add(clientHandler);
    }

    public synchronized void unSubscribe(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }

    public void start() {
        admin.info("Server started");
        admin_console.info("Server started");
        try {
            while (true) {
                waitAndProcessNewClientConnection();
            }
        } catch (IOException e) {
            admin.fatal(String.format("%s %s %s", e.getClass(), e.getCause(), e.getMessage()));
            admin_console.fatal(String.format("%s %s %s", e.getClass(), e.getCause(), e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    private void waitAndProcessNewClientConnection() throws IOException {
        admin.info("Waiting for client...");
        admin_console.info("Waiting for client...");
        Socket socket = serverSocket.accept();
        admin.info("Client is connected");
        admin_console.info("Client is connected");

        processClientConnection(socket);
    }

    private void processClientConnection(Socket socket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(this, socket);
        clientHandler.handle();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized boolean isLoginBusy(String fullname) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getFullname().equals(fullname)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void privateMessage(ClientHandler sender, String recipient, String message) throws IOException {
        for (ClientHandler client : clientHandlers) {
            if (client == sender) continue;
            else if (client.getFullname().equals(recipient)) client.sendPrivateMessage(sender.getFullname(), message);
        }
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender, boolean isServerMessage) throws IOException {
        for (ClientHandler client : clientHandlers) {
            if (client == sender) {
                continue;
            }
            client.sendMessage(isServerMessage ? null : sender.getFullname(), message);
        }
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender) throws IOException {
        broadcastMessage(message, sender, false);
    }

    public synchronized void sendChatMembersList(ClientHandler clientHandler) throws IOException {
        for (ClientHandler client : clientHandlers) {
            client.sendListOfChatMembers(clientHandlers);
        }
    }

    public void broadcastDisconnectUser(ClientHandler clientHandler) throws IOException {
        for (ClientHandler handler : clientHandlers) {
            if (handler != clientHandler) {
                handler.sendServerMessage(String.format("%s left the chat", clientHandler.getFullname()));
                handler.sendListOfChatMembers(clientHandlers);
            }
        }
    }
}
