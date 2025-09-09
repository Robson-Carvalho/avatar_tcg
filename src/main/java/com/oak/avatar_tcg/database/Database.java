package com.oak.avatar_tcg.database;

import com.oak.avatar_tcg.util.IPv4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load PostgreSQL JDBC driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String ip = IPv4.getLocalIPv4();

        String url = "jdbc:postgresql://"+ip+":5432/avatar_tcg";
        String user = "postgres";
        String password = "postgres";

        return DriverManager.getConnection(url, user, password);
    }
}