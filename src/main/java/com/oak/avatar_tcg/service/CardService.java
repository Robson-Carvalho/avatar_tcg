package com.oak.avatar_tcg.service;

import com.oak.avatar_tcg.enums.RarityCard;
import com.oak.avatar_tcg.model.Card;
import com.oak.avatar_tcg.model.Deck;
import com.oak.avatar_tcg.model.SystemCard;
import com.oak.avatar_tcg.model.User;
import com.oak.avatar_tcg.repository.CardRepository;
import com.oak.avatar_tcg.repository.SystemCardRepository;

import java.util.Map;
import java.util.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CardService {
    private final Random random = new Random();
    private final DeckService deckService;
    private final CardRepository cardRepository;
    private final SystemCardRepository systemCardRepository;
    private final UserService userService;

    public CardService() {
        this.deckService = new DeckService();
        this.systemCardRepository = new SystemCardRepository();
        this.cardRepository = new CardRepository();
        this.userService = new UserService();
    }

    public List<Card> findByUserId(String userId) {
        synchronized (cardRepository) {
           return cardRepository.findByUserId(userId);
        }
    }


    public List<Card> findDeckByUserId(String userId) {
        List<Card> inventory = findByUserId(userId);
        Deck deck = deckService.findByUserId(userId);

        List<Card> cards = new ArrayList<>();

        for (Card card : inventory) {
            if (deck.getCards().contains(card.getId())) {
                cards.add(card);
            }
        }

        return cards;
    }

    public List<Card> openPackage(String userId) throws Exception {
        User user = userService.findById(userId);
        if (user == null) {
            throw new Exception("User not found");
        }

        // Pré-processar as cartas do sistema por raridade uma única vez
        Map<RarityCard, List<SystemCard>> systemCardsByRarity = systemCardRepository.findAllSystemCards()
                .stream()
                .collect(Collectors.groupingBy(SystemCard::getRarity));

        // Gerar 5 cartas aleatórias
        List<Card> packageCards = IntStream.range(0, 5)
                .mapToObj(i -> generateRandomCard(userId, systemCardsByRarity))
                .collect(Collectors.toList());

        // Salvar todas as cartas de uma vez (já está otimizado)
        synchronized (cardRepository) {
            cardRepository.saveAll(packageCards);
        }

        return packageCards;
    }

    private RarityCard chooseRarity() {
        int rand = random.nextInt(100);

        if (rand < 80) return RarityCard.COMMON;        // 80%
        if (rand < 95) return RarityCard.RARE;          // 15% (80-95)
        if (rand < 99) return RarityCard.EPIC;          // 4% (95-99)
        return RarityCard.LEGENDARY;                    // 1% (99-100)
    }

    private Card generateRandomCard(String userId, Map<RarityCard, List<SystemCard>> systemCardsByRarity) {
        RarityCard rarity = chooseRarity();
        List<SystemCard> cardsOfRarity = systemCardsByRarity.get(rarity);

        if (cardsOfRarity == null || cardsOfRarity.isEmpty()) {
            throw new IllegalStateException("No system cards found for rarity: " + rarity);
        }

        SystemCard chosenCard = cardsOfRarity.get(random.nextInt(cardsOfRarity.size()));

        return createCardFromSystemCard(userId, chosenCard);
    }

    private Card createCardFromSystemCard(String userId, SystemCard systemCard) {
        Card card = new Card();
        card.setUserId(userId);
        card.setName(systemCard.getName());
        card.setRarity(systemCard.getRarity());
        card.setDescription(systemCard.getDescription());
        card.setElement(systemCard.getElement());
        card.setAttack(systemCard.getAttack());
        card.setDefense(systemCard.getDefense());
        card.setPhase(systemCard.getPhase());
        card.setLife(systemCard.getLife());

        return card;
    }
}
