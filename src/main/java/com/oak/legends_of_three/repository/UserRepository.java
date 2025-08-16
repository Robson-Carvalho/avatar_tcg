package com.oak.legends_of_three.repository;

import com.oak.legends_of_three.database.Database;
import com.oak.legends_of_three.model.User;
import java.sql.*;
import java.util.UUID;

public class UserRepository {
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        }
    }

    public User findByNickname(String nickname) throws SQLException {
        String sql = "SELECT * FROM users WHERE nickname = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nickname);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        }
    }

    public User save(User user) throws SQLException {
        String sql = "INSERT INTO users (id, name, nickname, email, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            user.setId(UUID.randomUUID().toString());
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getNickname());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPassword());
            stmt.executeUpdate();
            return user;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setNickname(rs.getString("nickname"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        return user;
    }
}