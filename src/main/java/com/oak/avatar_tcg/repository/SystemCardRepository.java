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

        systemCard.setName(rs.getString("name"));
        systemCard.setPhase(PhaseCard.valueOf(rs.getString("phase")));
        systemCard.setRarity(RarityCard.valueOf(rs.getString("rarity")));
        systemCard.setElement(ElementCard.valueOf(rs.getString("element")));
        systemCard.setAttack(rs.getInt("attack"));
        systemCard.setDefense(rs.getInt("defense"));
        systemCard.setLife(rs.getInt("life"));
        systemCard.setDescription(rs.getString("description"));

        return systemCard;
    }



}