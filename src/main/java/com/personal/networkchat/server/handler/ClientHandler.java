package com.personal.networkchat.server.handler;

import com.personal.networkchat.server.ServerConfiguration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    private ServerConfiguration serverConfiguration;
    private Socket clientSocket;

    public ClientHandler(ServerConfiguration serverConfiguration, Socket clientSocket) {
        this.serverConfiguration = serverConfiguration;
        this.clientSocket = clientSocket;
    }

    public void handle() throws IOException {
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
        DataInputStream in = new DataInputStream(clientSocket.getInputStream());

        new Thread(() -> {
            authentication();
            readMessage();
        }).start();
    }

    private void authentication() {

    }

    private void readMessage() {

    }
}
