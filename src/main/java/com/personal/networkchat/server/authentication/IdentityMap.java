package com.personal.networkchat.server.authentication;

import com.personal.networkchat.server.models.User;

import java.util.HashMap;
import java.util.Map;

public class IdentityMap {
    private static Map<String, User> userMap = new HashMap<>();

    public static void addUser(String login, User user) {
        userMap.put(login, user);
    }

    public static User getUser(String login, String password) {
        for (String key : userMap.keySet()) {
            if (key.equals(login) && password.equals(userMap.get(login).getPassword())) return userMap.get(login);
                else break;
        }
        return null;
    }
}
