package com.personal.networkchat.server.authentication;

import java.sql.*;

public class DBAuthService implements AuthService {
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;

    private static ResultSet resultSet;
    @Override
    public String getUserNameByLoginAndPassword(String login, String password) {
        String passwordDB;
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE login = ?");
            preparedStatement.setString(1, login);
            resultSet = preparedStatement.executeQuery();
            passwordDB = resultSet.getString("password");
            return passwordDB != null && passwordDB.equals(password) ?
                    String.format("%s %s", resultSet.getString("name"), resultSet.getString("surname")) :
                    null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startAuthentication() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/databases/mainDB");
            statement = connection.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void endAuthentication() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
