package com.example.networkchat.server.options.models;


import lombok.Data;

@Data
public class User {
    private final String username;
    private final String login;
    private final String password;
}
