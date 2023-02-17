package com.personal.networkchat.server.authentication;

import com.personal.networkchat.server.models.User;

public interface AuthService {
    User getUserNameByLoginAndPassword(String login, String password);
    User insertNewUser(User user);
    void startAuthentication();
    void endAuthentication();
    String update(User user);
    String delete(User user);
}
