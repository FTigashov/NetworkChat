package com.personal.networkchat.server.authentication;

import java.sql.*;

public class DBAuthService implements AuthService {
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;

    @Override
    public String getUserNameByLoginAndPassword(String login, String password) {
//        preparedStatement = connection.prepareStatement("SELECT * FROM ")
        return null;
    }

    @Override
    public void startAuthentication() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/databases/mainDB.db");
            statement = connection.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void endAuthentication() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
