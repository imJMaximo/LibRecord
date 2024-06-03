package com.example.librecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLHelperBookmark {

    public static void addBookmark(String userId, String bookmarkDetails,String username) throws SQLException {
        Connection connection = Connect.CONN();
        String query = "INSERT INTO bookmark (user_id, bookmarkcol,username) VALUES (?, ?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, userId);
        preparedStatement.setString(2, bookmarkDetails);
        preparedStatement.setString(3, username);
        preparedStatement.executeUpdate();
        connection.close();
    }

    public static void removeBookmark(String userId, String bookmarkDetails) throws SQLException {
        Connection connection = Connect.CONN();
        String query = "DELETE FROM bookmark WHERE user_id = ? AND details = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, userId);
        preparedStatement.setString(2, bookmarkDetails);
        preparedStatement.executeUpdate();
        connection.close();
    }

    public static ResultSet getBookmarks(String userId) throws SQLException {
        Connection connection = Connect.CONN();
        String query = "SELECT details FROM bookmark WHERE user_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, userId);
        return preparedStatement.executeQuery();
    }
}

