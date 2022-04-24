package com.example.networkchat.server.options.authentication;

import java.sql.SQLException;

public interface AuthenticationService {
    String getUsernameByLoginAndPassword(String login, String password);

    void startAuthentication() throws ClassNotFoundException, SQLException;
    void endAuthentication();
}
