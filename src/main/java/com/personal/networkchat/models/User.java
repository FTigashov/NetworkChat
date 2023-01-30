package com.personal.networkchat.models;

import lombok.Data;

@Data
public class User {
    private final String name;
    private final String surname;
    private final String login;
    private final String password;
}
