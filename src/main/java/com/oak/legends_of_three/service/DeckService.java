package com.oak.legends_of_three.service;

import com.oak.legends_of_three.model.Deck;
import com.oak.legends_of_three.repository.DeckRepository;

import java.util.List;

public class DeckService {
    DeckRepository deckRepository;

    public DeckService() {
        this.deckRepository = new DeckRepository();
    }

    public Deck findByUserId(String id) throws Exception {
        List<Deck> decks = deckRepository.findAll();

        for (Deck deck : decks) {
            if (deck.getUserId().equals(id)) {
                return deck;
            }
        }

        return null;
    }

    public Deck updateDeck(Deck deck) throws Exception {
       Deck oldDeck = findByUserId(deck.getUserId());

       if (oldDeck == null) {
           throw new Exception("Deck not found");
       }

       oldDeck.setCard1Id(deck.getCard1Id());
       oldDeck.setCard2Id(deck.getCard2Id());
       oldDeck.setCard3Id(deck.getCard3Id());
       oldDeck.setCard4Id(deck.getCard4Id());
       oldDeck.setCard5Id(deck.getCard5Id());

       deckRepository.save(oldDeck);

       return oldDeck;
    }
}
