package com.personal.networkchat.server.models;

import lombok.Data;

@Data
public class User {
    private final String name;
    private final String surname;
    private final String login;
    private final String password;
}
