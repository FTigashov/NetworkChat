package com.personal.networkchat.server.authentication;

import com.personal.networkchat.server.ServerConfiguration;
import com.personal.networkchat.server.handler.LoggingConfig;
import com.personal.networkchat.server.models.User;

import java.io.IOException;
import java.util.List;
import java.util.logging.*;

public class BaseAuthentication extends LoggingConfig implements AuthService {


    private static final List<User> users = List.of(
            new User("Ivan", "Ivanov", "ivanov", "1111"),
            new User("Andrew", "Vasyliev", "vasya", "2222"),
            new User("John", "Smith", "johny", "3333")
    );

    @Override
    public String getUserNameByLoginAndPassword(String login, String password) {
        for (User user : users) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                return String.format("%s %s", user.getName(), user.getSurname());
            }
        }
        return null;
    }

    @Override
    public void startAuthentication() {
        admin.info("Authentication is started");
        admin_console.info("Authentication is started");
//        System.out.println("auth is started");
    }

    @Override
    public void endAuthentication() {
        admin.info("End of authentication");
        admin_console.info("End of authentication");
//        System.out.println("auth is ended");
    }
}
