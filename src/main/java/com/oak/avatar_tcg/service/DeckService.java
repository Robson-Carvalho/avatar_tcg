package com.oak.avatar_tcg.service;

import com.oak.avatar_tcg.model.Deck;
import com.oak.avatar_tcg.repository.DeckRepository;

import java.util.List;

public class DeckService {
    private final DeckRepository deckRepository;

    public DeckService() {
        this.deckRepository = new DeckRepository();
    }

    public Deck findByUserId(String id)  {
        synchronized (deckRepository) {
            return deckRepository.findByUserId(id);
        }
    }

    public Deck findById(String id)  {
        synchronized (deckRepository) {
            return deckRepository.findById(id);
        }
    }

    public Deck updateDeck(Deck deck) throws Exception {
        synchronized (deckRepository) {
            deckRepository.update(deck);

            return this.findById(deck.getId());
        }
    }
}
