package com.personal.networkchat.server;

import java.io.IOException;

public class ServerApp {

    public static void main(String[] args) {
        ServerConfiguration.getInstance().start();
    }
}
