package com.oak.legends_of_three.service;

import com.oak.legends_of_three.enums.RarityCard;
import com.oak.legends_of_three.model.Card;
import com.oak.legends_of_three.repository.CardRepository;

import java.util.ArrayList;
import java.util.List;

public class CardService {
    private final CardRepository cardRepository;

    public CardService() {
        this.cardRepository = new CardRepository();
    }

    public List<Card> findByUserId(String userId) {
        List<Card> cards = new ArrayList<>();
        for (Card card : cardRepository.findAll()) {
            if (userId.equals(card.getUserId())) {
                cards.add(card);
            }
        }
        return cards;
    }

    private List<String> findAllLegendaryCardsDealt() {
        List<String> legendaryCards = new ArrayList<>();
        for (Card card : cardRepository.findAll()) {
            if (card.getRarity().equals(RarityCard.LEGENDARY) && card.getUserId() == null) {
                legendaryCards.add(card.getName());
            }
        }
        return legendaryCards;
    }

    private String chooseRarity() {
        int rand = (int) (Math.random() * 100);
        int chanceCommon = 70;
        int chanceRare = 20;
        int chanceEpic = 9;

        if (rand < chanceCommon) return "COMMON";
        if (rand < chanceCommon + chanceRare) return "RARE";
        if (rand < chanceCommon + chanceRare + chanceEpic) return "EPIC";
        return "LEGENDARY";
    }

    // Thread-safe: abre um pacote de cartas para o usuário
    public synchronized List<Card> openPackage(String userId) {
        List<Card> allCards = cardRepository.findAll();
        List<Card> packageCards = new ArrayList<>();
        List<String> legendaryDistributed = findAllLegendaryCardsDealt();
        int packageSize = 5;


        while (packageCards.size() < packageSize) {
            String rarity = chooseRarity();
            List<Card> availableCards = filterAvailableCards(allCards, packageCards, legendaryDistributed, rarity);

            // Se não houver cartas disponíveis, tenta outra raridade
            if (availableCards.isEmpty()) {
                // Tenta fallback: pega qualquer carta disponível que não esteja no pacote
                availableCards = new ArrayList<>();
                for (Card card : allCards) {
                    if (!packageCards.contains(card) && (card.getRarity().equals("LEGENDARY") ? !legendaryDistributed.contains(card.getName()) : true)) {
                        availableCards.add(card);
                    }
                }

            }

            Card chosenCard = pickRandomCard(availableCards);

            if (chosenCard.getRarity().equals("LEGENDARY")) {
                legendaryDistributed.add(chosenCard.getName());
            }

            chosenCard.setUserId(userId);
            cardRepository.save(chosenCard);
            packageCards.add(chosenCard);
        }

        return packageCards;
    }

    // Filtra cartas disponíveis considerando raridade, pacotes e lendárias distribuídas
    private List<Card> filterAvailableCards(List<Card> allCards, List<Card> packageCards, List<String> legendaryDistributed, String rarity) {
        List<Card> available = new ArrayList<>();
        for (Card card : allCards) {
            if (!card.getRarity().equals(rarity)) continue;
            if (rarity.equals("LEGENDARY") && legendaryDistributed.contains(card.getName())) continue;
            if (packageCards.contains(card)) continue;
            available.add(card);
        }
        return available;
    }

    // Seleciona aleatoriamente uma carta
    private Card pickRandomCard(List<Card> availableCards) {
        int index = (int) (Math.random() * availableCards.size());
        Card original = availableCards.get(index);

        if (original.getRarity().equals("LEGENDARY")) {
            // Para lendárias, mantém a mesma instância
            return original;
        } else {
            // Para cartas comuns, raras ou épicas, cria uma cópia com novo ID
            Card copy = new Card();
            copy.setName(original.getName());
            copy.setRarity(original.getRarity());
            copy.setAttack(original.getAttack());
            copy.setDefense(original.getDefense());
            copy.setDescription(original.getDescription());
            copy.setElement(original.getElement());
            copy.setPhase(original.getPhase());
            copy.setLife(original.getLife());

            return copy;
        }
    }
}
