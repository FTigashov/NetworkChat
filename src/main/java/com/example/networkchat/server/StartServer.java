package com.example.networkchat.server;

import com.example.networkchat.server.options.MyServer;

import java.io.IOException;
import java.sql.SQLException;

public class StartServer {
    private static final int DEFAULT_PORT = 8186;
    public static void main(String[] args) {
        try {
            new MyServer(DEFAULT_PORT).start();
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
