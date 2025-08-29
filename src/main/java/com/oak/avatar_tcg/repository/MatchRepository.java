package com.oak.avatar_tcg.repository;

import com.oak.avatar_tcg.database.Database;
import com.oak.avatar_tcg.model.Match;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MatchRepository {
    public List<Match> findAll()  {
        String sql = "SELECT * FROM matchs";

        List<Match> matchs = new ArrayList<>();

        try (Connection conn = Database.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                matchs.add(mapResultSetToMatch(rs));
            }

            return matchs;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return matchs;
    }

    private Match mapResultSetToMatch(ResultSet rs) throws SQLException {
        Match match = new Match();

        match.setId(rs.getString("id"));
        match.setPlayerOneID(rs.getString("playerOneID"));
        match.setPlayerTwoID(rs.getString("playerOneID"));
        match.setPlayerWin(rs.getString("playerWin"));

        return match;
    }

    public void save(Match match) {
        String sql = "INSERT INTO cards (id, playerOneID, playerOneID, playerWin) " + "VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, match.getId());
            stmt.setString(2, match.getPlayerOneID());
            stmt.setString(3, match.getPlayerOneID());
            stmt.setString(4, match.getPlayerWin());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}