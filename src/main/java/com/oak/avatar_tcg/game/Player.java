package com.oak.avatar_tcg.game;

import com.oak.avatar_tcg.model.Card;
import com.oak.avatar_tcg.service.CardService;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String id;
    private int points;
    private String activationCard;
    private final List<String> cemetery;
    private final List<Card> cards;

    public Player(String id)  {
        CardService cardService = new CardService();
        this.id = id;
        this.points = 0;
        this.activationCard = "";
        this.cemetery = new ArrayList<>();
        this.cards = cardService.findDeckByUserId(this.id);
        System.out.println(cards.size() + " cards found");
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getPoints() { return points; }
    public void addPoint() { this.points++; }

    public String getActivationCard() { return activationCard; }
    public void setActivationCard(String cardID) {
        Card card = this.cards.stream()
                .filter(c -> c.getId().equals(cardID))
                .findFirst()
                .orElse(null);

        if (card == null || card.getLife() <= 0) return;
        this.activationCard = cardID;
    }

    public List<String> getCemetery() { return cemetery; }
    public List<Card> getCards() { return cards; }

    public void reduceLifeCard(String cardID, int attack) {
        Card card = this.cards.stream()
                .filter(c -> c.getId().equals(cardID))
                .findFirst()
                .orElse(null);

        if (card == null) return;

        int newLife = card.getLife() - attack;
        card.setLife(newLife);

        if (newLife <= 0) {
            this.cemetery.add(card.getId());
            this.cards.remove(card);

            if (card.getId().equals(this.activationCard)) {
                this.activationCard = null;
            }
        }
    }
}
