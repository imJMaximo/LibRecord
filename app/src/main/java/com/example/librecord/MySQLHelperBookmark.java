package com.example.librecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLHelperBookmark {

    public static void addBookmark(int id, int bookid, String username) throws SQLException {
        Connection connection = Connect.CONN();
        String query = "INSERT INTO bookmark (id, bookid,username) VALUES (?, ?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.setInt(2, bookid);
        preparedStatement.setString(3, username);
        preparedStatement.executeUpdate();
        connection.close();
    }

    public static void removeBookmark(int id, int bookid) throws SQLException {
        Connection connection = Connect.CONN();
        String query = "DELETE FROM bookmark WHERE id = ? AND bookid = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.setInt(2, bookid);
        preparedStatement.executeUpdate();
        connection.close();
    }

    public static ResultSet getBookmarks(int id) throws SQLException {
        Connection connection = Connect.CONN();
        String query = "SELECT details FROM bookmark WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        return preparedStatement.executeQuery();
    }
}

