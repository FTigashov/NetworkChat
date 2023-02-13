package com.personal.networkchat.server.authentication;

public interface AuthService {
    String getUserNameByLoginAndPassword(String login, String password);
    void startAuthentication();
    void endAuthentication();

    String registerForNewUser(String name, String surname, String login, String password);
}
