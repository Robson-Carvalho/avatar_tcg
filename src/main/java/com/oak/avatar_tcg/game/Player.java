package com.oak.avatar_tcg.game;

import com.oak.avatar_tcg.enums.ElementCard;
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
    private Boolean playedCard;

    public Player(String id)  {
        CardService cardService = new CardService();
        this.id = id;
        this.points = 0;
        this.activationCard = "";
        this.cemetery = new ArrayList<>();
        this.playedCard = false;
        this.cards = cardService.findDeckByUserId(this.id);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Boolean getPlayedCard() { return playedCard; }
    public void setPlayedCard(Boolean playedCard) {
        this.playedCard = playedCard;

        if(this.activationCard.equals("")){
            for(Card card : this.cards){
                if(card.getLife() > 0) {
                    this.activationCard = card.getId();
                    return;
                }
            }
        }
    }

    public int getPoints() { return points; }

    public void addPoint() { this.points++; }

    public Card getActivationCard() {
        for (Card card : this.cards) {
            if(card.getId().equals(this.activationCard))
                return card;
        }

        return null;
    }

    public void setActivationCard(String cardID) {
        if(cemetery.contains(cardID)){
            return;
        }

        for(Card c : cards){
            if(c.getId().equals(cardID)){
                this.activationCard = c.getId();
            }
        }
    }

    public List<Card> getCards() { return cards; }

    public Boolean reduceLifeCard(int attack, String attackElementStr) {
        Card card = this.getActivationCard();

        ElementCard attackElement = ElementCard.valueOf(attackElementStr.toUpperCase());
        ElementCard defenseElement = card.getElement();

        boolean hasAdvantage = false;

        switch (attackElement) {
            case WATER:
                hasAdvantage = defenseElement == ElementCard.FIRE || defenseElement == ElementCard.LIGHTNING;
                break;
            case FIRE:
                hasAdvantage = defenseElement == ElementCard.AIR || defenseElement == ElementCard.METAL;
                break;
            case EARTH:
                hasAdvantage = defenseElement == ElementCard.LIGHTNING || defenseElement == ElementCard.FIRE;
                break;
            case AIR:
                hasAdvantage = defenseElement == ElementCard.EARTH;
                break;
            case BLOOD:
                hasAdvantage = defenseElement != ElementCard.AVATAR && defenseElement != ElementCard.BLOOD;
                break;
            case METAL:
                hasAdvantage = defenseElement == ElementCard.EARTH || defenseElement == ElementCard.LIGHTNING;
                break;
            case LIGHTNING:
                hasAdvantage = defenseElement == ElementCard.WATER || defenseElement == ElementCard.AIR;
                break;
            case AVATAR:
                hasAdvantage = true;
                break;
        }

        int effectiveDefense = hasAdvantage ? 0 : card.getDefense();
        int damage = Math.max(attack - effectiveDefense, 0);
        int newLife = card.getLife() - damage;

        if (newLife <= 0) {
            card.setLife(0);
            this.cemetery.add(card.getId());
            this.activationCard = "";
        } else {
            card.setLife(newLife);
        }

        return newLife <= 0;
    }
}
