package com.oak.legends_of_three.model;

import java.util.Objects;
import java.util.UUID;

public class Deck {
    private String id;
    private String userId;
    private String card1Id;
    private String card2Id;
    private String card3Id;
    private String card4Id;
    private String card5Id;

    public Deck() {
        this.id = UUID.randomUUID().toString();
    }

    public Deck(String userId, String card1Id, String card2Id, String card3Id, String card4Id, String card5Id) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.card1Id = card1Id;
        this.card2Id = card2Id;
        this.card3Id = card3Id;
        this.card4Id = card4Id;
        this.card5Id = card5Id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCard1Id() {
        return card1Id;
    }

    public void setCard1Id(String card1Id) {
        this.card1Id = card1Id;
    }

    public String getCard2Id() {
        return card2Id;
    }

    public void setCard2Id(String card2Id) {
        this.card2Id = card2Id;
    }

    public String getCard3Id() {
        return card3Id;
    }

    public void setCard3Id(String card3Id) {
        this.card3Id = card3Id;
    }

    public String getCard4Id() {
        return card4Id;
    }

    public void setCard4Id(String card4Id) {
        this.card4Id = card4Id;
    }

    public String getCard5Id() {
        return card5Id;
    }

    public void setCard5Id(String card5Id) {
        this.card5Id = card5Id;
    }
}