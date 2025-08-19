package com.oak.legends_of_three.model;

import java.util.Objects;
import java.util.UUID;

public class Deck {
    private String id;
    private String name;
    private String userId;
    private String card1Id;
    private String card2Id;
    private String card3Id;
    private String card4Id;
    private String card5Id;

    public Deck() {
        this.id = UUID.randomUUID().toString();
    }

    public Deck(String name, String userId, String card1Id, String card2Id, String card3Id, String card4Id, String card5Id) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean hasDuplicateCards() {
        return card1Id.equals(card2Id) ||
                card1Id.equals(card3Id) ||
                card1Id.equals(card4Id) ||
                card1Id.equals(card5Id) ||
                card2Id.equals(card3Id) ||
                card2Id.equals(card4Id) ||
                card2Id.equals(card5Id) ||
                card3Id.equals(card4Id) ||
                card3Id.equals(card5Id) ||
                card4Id.equals(card5Id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deck deck = (Deck) o;

        return Objects.equals(id, deck.id) &&
                Objects.equals(userId, deck.userId) &&
                Objects.equals(card1Id, deck.card1Id) &&
                Objects.equals(card2Id, deck.card2Id) &&
                Objects.equals(card3Id, deck.card3Id) &&
                Objects.equals(card4Id, deck.card4Id) &&
                Objects.equals(card5Id, deck.card5Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, card1Id, card2Id, card3Id, card4Id, card5Id);
    }

    @Override
    public String toString() {
        return "Deck{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", card1Id='" + card1Id + '\'' +
                ", card2Id='" + card2Id + '\'' +
                ", card3Id='" + card3Id + '\'' +
                ", card4Id='" + card4Id + '\'' +
                ", card5Id='" + card5Id + '\'' +
                '}';
    }
}