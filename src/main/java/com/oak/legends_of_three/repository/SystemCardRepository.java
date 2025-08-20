package com.oak.legends_of_three.repository;

import com.oak.legends_of_three.database.Database;
import com.oak.legends_of_three.enums.ElementCard;
import com.oak.legends_of_three.enums.PhaseCard;
import com.oak.legends_of_three.enums.RarityCard;
import com.oak.legends_of_three.model.SystemCard;

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