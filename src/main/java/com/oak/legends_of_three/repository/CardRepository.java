package com.oak.legends_of_three.repository;

import com.oak.legends_of_three.database.Database;
import com.oak.legends_of_three.model.Card;
import com.oak.legends_of_three.enums.ElementCard;
import com.oak.legends_of_three.enums.PhaseCard;
import com.oak.legends_of_three.enums.RarityCard;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardRepository {
    public List<Card> findByUserId(String userId) {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT * FROM cards WHERE user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Card card = new Card(
                        rs.getString("user_id"),
                        rs.getString("name"),
                        ElementCard.valueOf(rs.getString("element")),
                        PhaseCard.valueOf(rs.getString("phase")),
                        rs.getInt("attack"),
                        rs.getInt("defense"),
                        RarityCard.valueOf(rs.getString("rarity")),
                        rs.getString("description")
                );
                card.setUserId(rs.getString("user_id"));
                cards.add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }

    public Card findById(String cardId) {
        String sql = "SELECT * FROM cards WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cardId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Card card = new Card(
                        rs.getString("user_id"),
                        rs.getString("name"),
                        ElementCard.valueOf(rs.getString("element")), // Corrigido aqui
                        PhaseCard.valueOf(rs.getString("phase")),    // Corrigido aqui
                        rs.getInt("attack"),
                        rs.getInt("defense"),
                        RarityCard.valueOf(rs.getString("rarity")),  // Corrigido aqui
                        rs.getString("description")
                );
                card.setUserId(rs.getString("user_id"));
                return card;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Card card) {
        String sql = "INSERT INTO cards (id, user_id, name, element, phase, attack, defense, rarity, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, card.getId());
            stmt.setString(2, card.getUserId());
            stmt.setString(3, card.getName());
            stmt.setString(4, card.getElement().name());
            stmt.setString(5, card.getPhase().name());
            stmt.setInt(6, card.getAttack());
            stmt.setInt(7, card.getDefense());
            stmt.setString(8, card.getRarity().name());
            stmt.setString(9, card.getDescription());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}