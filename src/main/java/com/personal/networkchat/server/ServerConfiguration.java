package com.personal.networkchat.server;

import com.personal.networkchat.server.authentication.AuthService;
import com.personal.networkchat.server.authentication.BaseAuthentication;
import com.personal.networkchat.server.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;

import java.net.Socket;
import java.util.logging.*;

public class ServerConfiguration {
    private static final Logger logger;
    private static final Handler serverLoggerHandler;

    static {
        logger = Logger.getLogger(ServerConfiguration.class.getName());
        logger.setLevel(Level.ALL);
        try {
            serverLoggerHandler = new FileHandler("src/main/resources/logs/serverConfigLogs.txt");
            serverLoggerHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(serverLoggerHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final ServerSocket serverSocket;
    private final AuthService authService;

    public ServerConfiguration(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        authService = new BaseAuthentication();
    }

    public void start() {
        logger.info("Server started");

        try {
            while (true) {
                wainAndProcessNewClientConnection();
            }
        } catch (IOException e) {
            logger.severe(String.format("%s %s %s", e.getClass(), e.getCause(), e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    private void wainAndProcessNewClientConnection() throws IOException {
        logger.warning("Waiting for client...");
        Socket socket = serverSocket.accept();
        logger.info("Client is connected");

        processClientConnection(socket);
    }

    private void processClientConnection(Socket socket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(this, socket);
        clientHandler.handle();
    }
}
