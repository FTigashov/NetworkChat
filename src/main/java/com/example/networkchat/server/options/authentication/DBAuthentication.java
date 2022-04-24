package com.example.networkchat.server.options.authentication;
import java.sql.*;


public class DBAuthentication implements AuthenticationService {

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        String username = null;
        String passwordDB = null;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM authentication WHERE login = ?");
            preparedStatement.setString(1, login);
            resultSet = preparedStatement.executeQuery();

            username = resultSet.getString("username");
            passwordDB = resultSet.getString("password");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return passwordDB != null && passwordDB.equals(password) ? username : null;
    }

    @Override
    public void startAuthentication() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/com/example/networkchat/databases/mainDB.db");
        statement = connection.createStatement();
    }

    @Override
    public void endAuthentication() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
