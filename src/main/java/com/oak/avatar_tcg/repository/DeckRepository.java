package com.oak.avatar_tcg.repository;

import com.oak.avatar_tcg.database.Database;
import com.oak.avatar_tcg.model.Deck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeckRepository {

    public List<Deck> findAll() {
        String sql = "SELECT * FROM decks";

        List<Deck> decks = new ArrayList<>();

        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                decks.add(mapResultSetToDeck(rs));
            }

            return decks;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return decks;
    }

    private Deck mapResultSetToDeck(ResultSet rs) throws SQLException {
        Deck deck = new Deck();
        deck.setId(rs.getString("id"));
        deck.setUserId(rs.getString("user_id"));
        deck.setCard1Id(rs.getString("card1_id"));
        deck.setCard2Id(rs.getString("card2_id"));
        deck.setCard3Id(rs.getString("card3_id"));
        deck.setCard4Id(rs.getString("card4_id"));
        deck.setCard5Id(rs.getString("card5_id"));

        return deck;
    }

    public Deck save(Deck deck) throws SQLException {
        String sql = "INSERT INTO decks (id, user_id, card1_id, card2_id, card3_id, card4_id, card5_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            deck.setId(UUID.randomUUID().toString());

            stmt.setString(1, deck.getId());
            stmt.setString(2, deck.getUserId());
            stmt.setString(3, deck.getCard1Id());
            stmt.setString(4, deck.getCard2Id());
            stmt.setString(5, deck.getCard3Id());
            stmt.setString(6, deck.getCard4Id());
            stmt.setString(7, deck.getCard5Id());

            stmt.executeUpdate();
            return deck;
        }
    }
}
