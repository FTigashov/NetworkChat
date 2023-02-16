package com.personal.networkchat.server.authentication;

import com.personal.networkchat.server.models.User;

import java.sql.*;

public class DataMapper implements AuthService {
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;

    private static ResultSet resultSet;
    @Override
    public User getUserNameByLoginAndPassword(String login, String password) {
        String passwordDB;
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE login = ?");
            preparedStatement.setString(1, login);
            resultSet = preparedStatement.executeQuery();
            passwordDB = resultSet.getString("password");
            return passwordDB != null && passwordDB.equals(password) ?
                    new User(resultSet.getString("name"),
                            resultSet.getString("surname"),
                            login,
                            password) :
                    null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User insertNewUser(User user) {
        User checkIsUserExists = getUserNameByLoginAndPassword(user.getLogin(), user.getPassword());
        if (checkIsUserExists == null) {
            try {
                preparedStatement = connection.prepareStatement("INSERT INTO users (name, surname, login, password) VALUES (?, ?, ?, ?)");
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getSurname());
                preparedStatement.setString(3, user.getLogin());
                preparedStatement.setString(4, user.getPassword());
                preparedStatement.executeUpdate();
                return user;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
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

    @Override
    public String update(User user) {
        return null;
    }

    @Override
    public String delete(User user) {
        return null;
    }
}
