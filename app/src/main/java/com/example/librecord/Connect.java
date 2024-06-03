package com.example.librecord;

import java.sql.Connection;
import java.sql.DriverManager;
import android.util.*;

public class Connect {
    protected static String dbname = "librecord";
    protected static String ip = "10.0.2.2";
    protected static String port = "3306";
    protected static String username = "root";
    protected static String password = "AdminUser1";
//    protected static String password = "";

    public static Connection CONN(){
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connString = "jdbc:mysql://"+ip+":"+port+"/"+dbname+"?useSSL=false";
            conn = DriverManager.getConnection(connString, username, password);
        } catch (Exception e){
            e.printStackTrace();
            Log.d("Error", e+"");
        }

        return conn;
    }
}

