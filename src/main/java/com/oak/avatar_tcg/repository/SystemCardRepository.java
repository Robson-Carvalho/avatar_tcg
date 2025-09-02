package com.oak.avatar_tcg.repository;

import com.oak.avatar_tcg.database.Database;
import com.oak.avatar_tcg.enums.ElementCard;
import com.oak.avatar_tcg.enums.PhaseCard;
import com.oak.avatar_tcg.enums.RarityCard;
import com.oak.avatar_tcg.model.SystemCard;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SystemCardRepository {

    public List<SystemCard> findAllSystemCards()  {
        String sql = "SELECT * FROM system_cards";

        List<SystemCard> cards = new ArrayList<>();

        try (Connection conn = Database.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cards.add(mapResultSetToSystemCard(rs));
            }

            return cards;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cards;
    }

    private SystemCard mapResultSetToSystemCard(ResultSet rs) throws SQLException {
        SystemCard systemCard = new SystemCard();

        systemCard.setId(rs.getString("id"));
        systemCard.setName(rs.getString("name"));
        systemCard.setPhase(PhaseCard.valueOf(rs.getString("phase")));
        systemCard.setRarity(RarityCard.valueOf(rs.getString("rarity")));
        systemCard.setElement(ElementCard.valueOf(rs.getString("element")));
        systemCard.setAttack(rs.getInt("attack"));
        systemCard.setDefense(rs.getInt("defense"));
        systemCard.setLife(rs.getInt("life"));
        systemCard.setDescription(rs.getString("description"));
        systemCard.setQuantity(rs.getInt("quantity"));

        return systemCard;
    }

    public void save(SystemCard card) {
        String sql = "UPDATE system_cards SET " +
                "name = ?, " +
                "element = ?, " +
                "phase = ?, " +
                "attack = ?, " +
                "life = ?, " +
                "defense = ?, " +
                "rarity = ?, " +
                "description = ?, " +
                "quantity = ? " +
                "WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, card.getName());
            stmt.setString(2, card.getElement().name());
            stmt.setString(3, card.getPhase().name());
            stmt.setInt(4, card.getAttack());
            stmt.setInt(5, card.getLife());
            stmt.setInt(6, card.getDefense());
            stmt.setString(7, card.getRarity().name());
            stmt.setString(8, card.getDescription());
            stmt.setInt(9, card.getQuantity());
            stmt.setString(10, card.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                System.out.println("⚠ Nenhuma carta encontrada com id=" + card.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}