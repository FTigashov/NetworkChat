package com.example.networkchat.server.options;

import com.example.networkchat.server.options.authentication.AuthenticationService;
import com.example.networkchat.server.options.authentication.DBAuthentication;
import com.example.networkchat.server.options.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    private final ServerSocket serverSocket;
    private final AuthenticationService authenticationService;
    private final List<ClientHandler> clients;

    public MyServer(int port) throws IOException, SQLException, ClassNotFoundException {
        serverSocket = new ServerSocket(port);
        authenticationService = new DBAuthentication();
        clients = new ArrayList<>();
    }

    public void start() {
        System.out.println("Сервер запущен\n---------------");

        try {
            while (true) {
                waitAndProccessNewClientConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitAndProccessNewClientConnection() throws IOException {
        System.out.println("Ожидание клиента...");
        Socket socket = serverSocket.accept();
        System.out.println("Клиент подключился!");

        proccessClientConnection(socket);
    }

    private void proccessClientConnection(Socket socket) throws IOException {
        ClientHandler handler = new ClientHandler(this, socket);
        handler.handle();
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public synchronized boolean isUsernameBusy(String username) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) return true;
        }
        return false;
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender, boolean isServerMessage) throws IOException {
        for (ClientHandler client : clients) {
            if (client == sender) {
                continue;
            }
            client.sendMessage(isServerMessage ? null : sender.getUsername(), message);
        }
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender) throws IOException {
        broadcastMessage(message, sender, false);
    }

    public synchronized void broadcastListOfClients(ClientHandler clientHandler) throws IOException {
        for (ClientHandler client : clients) {
            client.sendServerMessage(String.format("%s вошел в чат", clientHandler.getUsername()));
            client.sendListOfClients(clients);
        }
    }

    public synchronized void sendPrivateMessage(ClientHandler sender, String recipient, String privateMessage) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(recipient)) {
                client.sendMessage(sender.getUsername(), privateMessage);
            }
        }
    }

    /*public synchronized List<ClientHandler> getListOfClients() {
        return clients;
    }*/

    public synchronized void broadcastClientDisconnected(ClientHandler clientHandler) throws IOException {
        for (ClientHandler client : clients) {
            if (client == clientHandler) {
                continue;
            }
            client.sendServerMessage(String.format("%s вышел из чата", clientHandler.getUsername()));
            client.sendListOfClients(clients);
        }
    }
}
