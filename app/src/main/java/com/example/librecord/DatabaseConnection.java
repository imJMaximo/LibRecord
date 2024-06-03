package com.example.librecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/dump20240418?useSSL=false&serverTimezone=UTC";
        String user = "your-database-username";
        String password = "your-database-password";
        return DriverManager.getConnection(url, user, password);
    }
}

