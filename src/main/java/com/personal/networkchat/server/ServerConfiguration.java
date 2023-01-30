package com.personal.networkchat.server;

import com.personal.networkchat.server.authentication.AuthService;
import com.personal.networkchat.server.authentication.BaseAuthentication;
import com.personal.networkchat.server.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerConfiguration {

    private final ServerSocket serverSocket;
    private final AuthService authService;

    private final List<ClientHandler> clientHandlers;

    public ServerConfiguration(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        authService = new BaseAuthentication();
        clientHandlers = new ArrayList<>();
    }

    public void subscribe(ClientHandler clientHandler) {
        clientHandlers.add(clientHandler);
    }

    public void unSubscribe(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }

    public void start() {
//        logger.info("Server started");
        System.out.println("Server started");

        try {
            while (true) {
                wainAndProcessNewClientConnection();
            }
        } catch (IOException e) {
//            logger.severe(String.format("%s %s %s", e.getClass(), e.getCause(), e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    private void wainAndProcessNewClientConnection() throws IOException {
//        logger.warning("Waiting for client...");
        System.out.println("Waiting for client...");
        Socket socket = serverSocket.accept();
        System.out.println("Client is connected");
//        logger.info("Client is connected");

        processClientConnection(socket);
    }

    private void processClientConnection(Socket socket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(this, socket);
        clientHandler.handle();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public boolean isLoginBusy(String fullname) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getFullname().equals(fullname)) {
                return true;
            }
        }
        return false;
    }
}
