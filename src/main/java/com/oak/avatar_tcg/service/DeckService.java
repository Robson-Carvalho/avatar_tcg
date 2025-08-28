package com.oak.avatar_tcg.service;

import com.oak.avatar_tcg.model.Deck;
import com.oak.avatar_tcg.repository.DeckRepository;

import java.util.List;

public class DeckService {
    DeckRepository deckRepository;

    public DeckService() {
        this.deckRepository = new DeckRepository();
    }

    public Deck findByUserId(String id)  {
        List<Deck> decks = deckRepository.findAll();

        for (Deck deck : decks) {
            if (deck.getUserId().equals(id)) {
                return deck;
            }
        }

        return null;
    }

    public Deck findById(String id)  {
        List<Deck> decks = deckRepository.findAll();

        for (Deck deck : decks) {
            if (deck.getId().equals(id)) {
                return deck;
            }
        }

        return null;
    }


    public Deck updateDeck(Deck deck) throws Exception {
       deckRepository.update(deck);
       return this.findById(deck.getId());
    }
}
