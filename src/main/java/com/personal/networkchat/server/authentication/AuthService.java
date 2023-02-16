package com.personal.networkchat.server.authentication;

import com.personal.networkchat.server.models.User;

public interface AuthService {
    String getUserNameByLoginAndPassword(String login, String password);
    void startAuthentication();
    void endAuthentication();

    String insertNewUser(User newUser);
    String update(User user);
    String delete(User user);
}
