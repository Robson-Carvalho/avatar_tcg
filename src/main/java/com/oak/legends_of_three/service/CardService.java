package com.oak.legends_of_three.service;

import com.oak.legends_of_three.model.Card;
import com.oak.legends_of_three.repository.CardRepository;

import java.util.ArrayList;
import java.util.List;

public class CardService {
    CardRepository cardRepository = new CardRepository();

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public List<Card> findByUserId(String userId) {
        List<Card> cards = new ArrayList<>();

        for (Card card : cardRepository.findAll()) {
            if(card.getUserId().equals(userId)){
                cards.add(card);
            }
        }

        return cards;
    }
}
