package com.oak.avatar_tcg.repository;

import com.oak.avatar_tcg.database.Database;
import com.oak.avatar_tcg.model.Card;
import com.oak.avatar_tcg.enums.ElementCard;
import com.oak.avatar_tcg.enums.PhaseCard;
import com.oak.avatar_tcg.enums.RarityCard;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardRepository {

    public List<Card> findAll()  {
        String sql = "SELECT * FROM cards";

        List<Card> cards = new ArrayList<>();

        try (Connection conn = Database.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cards.add(mapResultSetToCard(rs));
            }

            return cards;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cards;
    }

    private Card mapResultSetToCard(ResultSet rs) throws SQLException {
        Card card = new Card();
        card.setId(rs.getString("id"));
        card.setUserId(rs.getString("user_id"));

        card.setName(rs.getString("name"));
        card.setPhase(PhaseCard.valueOf(rs.getString("phase")));
        card.setRarity(RarityCard.valueOf(rs.getString("rarity")));
        card.setElement(ElementCard.valueOf(rs.getString("element")));
        card.setAttack(rs.getInt("attack"));
        card.setDefense(rs.getInt("defense"));
        card.setLife(rs.getInt("life"));
        card.setDescription(rs.getString("description"));

        return card;
    }

    public void saveAll(List<Card> cards) {
        String sql = "INSERT INTO cards (id, user_id, name, element, phase, attack, life, defense, rarity, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (Card card : cards) {
                stmt.setString(1, card.getId());
                stmt.setString(2, card.getUserId());
                stmt.setString(3, card.getName());
                stmt.setString(4, card.getElement().name());
                stmt.setString(5, card.getPhase().name());
                stmt.setInt(6, card.getAttack());
                stmt.setInt(7, card.getLife());
                stmt.setInt(8, card.getDefense());
                stmt.setString(9, card.getRarity().name());
                stmt.setString(10, card.getDescription());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}