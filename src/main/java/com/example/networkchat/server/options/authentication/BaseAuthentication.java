package com.example.networkchat.server.options.authentication;

import com.example.networkchat.server.options.models.User;
import org.sqlite.jdbc4.JDBC4Connection;

import java.util.List;

public class BaseAuthentication implements AuthenticationService {
    private static final List<User> clients = List.of(
            new User("Андрей", "user1", "1111"),
            new User("Тимофей", "user2", "2222"),
            new User("Игорь", "user3", "3333")
    );

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User client : clients) {
            if (client.getLogin().equals(login) && client.getPassword().equals(password)) return client.getUsername();
        }
        return null;
    }

    @Override
    public void startAuthentication() {
        System.out.println("Начата аутентификации");
    }

    @Override
    public void endAuthentication() {
        System.out.println("Начата аутентификации");
    }
}
