package com.oak.legends_of_three.model;

import com.oak.legends_of_three.enums.ElementCard;
import com.oak.legends_of_three.enums.PhaseCard;
import com.oak.legends_of_three.enums.RarityCard;

import java.util.UUID;

public class Card {
    private final String id;
    private String userId;
    private String name;
    private ElementCard element;
    private PhaseCard phase;
    private int attack;
    private int defense;
    private RarityCard rarity;
    private String description;

    public Card(String userId) {
        this.id = UUID.randomUUID().toString();
    }

    public Card(String userId, String name, ElementCard element, PhaseCard phase, int attack, int defense, RarityCard rarity, String description) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.name = name;
        this.element = element;
        this.phase = phase;
        this.attack = attack;
        this.defense = defense;
        this.rarity = rarity;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String setUserId(String userId) {
        return this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ElementCard getElement() {
        return element;
    }

    public void setElement(ElementCard element) {
        this.element = element;
    }

    public PhaseCard getPhase() {
        return phase;
    }

    public void setPhase(PhaseCard phase) {
        this.phase = phase;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public RarityCard getRarity() {
        return rarity;
    }

    public void setRarity(RarityCard rarity) {
        this.rarity = rarity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", element=" + element +
                ", phase=" + phase +
                ", attack=" + attack +
                ", defense=" + defense +
                ", rarity=" + rarity +
                ", description='" + description + '\'' +
                '}';
    }
}
