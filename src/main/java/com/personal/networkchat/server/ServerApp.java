package com.personal.networkchat.server;

import java.io.IOException;

public class ServerApp {
    private static final int DEFAULT_PORT = 8186;
    public static void main(String[] args) {
        try {
            new ServerConfiguration(DEFAULT_PORT).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
