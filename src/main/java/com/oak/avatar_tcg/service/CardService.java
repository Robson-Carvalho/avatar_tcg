package com.oak.avatar_tcg.service;

import com.oak.avatar_tcg.enums.RarityCard;
import com.oak.avatar_tcg.model.Card;
import com.oak.avatar_tcg.model.SystemCard;
import com.oak.avatar_tcg.model.User;
import com.oak.avatar_tcg.repository.CardRepository;
import com.oak.avatar_tcg.repository.SystemCardRepository;
import java.util.Random;

import java.util.ArrayList;
import java.util.List;

public class CardService {
    private final Random random = new Random();
    private final CardRepository cardRepository;
    private final SystemCardRepository systemCardRepository;
    private final UserService userService;

    public CardService() {
        this.systemCardRepository = new SystemCardRepository();
        this.cardRepository = new CardRepository();
        this.userService = new UserService();
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


    private RarityCard chooseRarity() {
        int rand = (int) (Math.random() * 100);
        int chanceCommon = 80;
        int chanceRare = 15;
        int chanceEpic = 4;

        if (rand < chanceCommon) return RarityCard.COMMON; // 80%
        if (rand < chanceCommon + chanceRare) return RarityCard.RARE; // 15%
        if (rand < chanceCommon + chanceRare + chanceEpic) return RarityCard.EPIC; // 4%
        return RarityCard.LEGENDARY; // 1%
    }

    public synchronized List<Card> openPackage(String userId) throws Exception {
        User user = userService.findById(userId);

        if(user==null){
            throw new Exception("User not found");
        };

        List<SystemCard> systemCards;
        List<Card> packageCards = new ArrayList<>();

        List<SystemCard> systemCardsCommon = new ArrayList<>();
        List<SystemCard> systemCardsRare = new ArrayList<>();
        List<SystemCard> systemCardsEpic = new ArrayList<>();
        List<SystemCard> systemCardsLegendary = new ArrayList<>();

        systemCards = systemCardRepository.findAllSystemCards();

        for (SystemCard systemCard : systemCards) {
            if(systemCard.getRarity().equals(RarityCard.COMMON)) {
                systemCardsCommon.add(systemCard);
            }
            else if(systemCard.getRarity().equals(RarityCard.RARE)) {
                systemCardsRare.add(systemCard);
            }
            else if(systemCard.getRarity().equals(RarityCard.EPIC)) {
                systemCardsEpic.add(systemCard);
            }
            else{
                systemCardsLegendary.add(systemCard);
            }
        }

        for(int i = 0; i < 5; i++){
            RarityCard rarity = chooseRarity();
            SystemCard chosenCard;

            if(rarity.equals(RarityCard.COMMON)){
                int index = random.nextInt(systemCardsCommon.size());
                chosenCard =  systemCardsCommon.get(index);
            }
            else if(rarity.equals(RarityCard.RARE)){
                int index = random.nextInt(systemCardsRare.size());
                chosenCard =  systemCardsRare.get(index);
            }
            else if(rarity.equals(RarityCard.EPIC)){
                int index = random.nextInt(systemCardsEpic.size());
                chosenCard =  systemCardsEpic.get(index);
            }
            else{
                int index = random.nextInt(systemCardsLegendary.size());
                chosenCard =  systemCardsLegendary.get(index);
            }

            Card card = new Card();
            card.setUserId(userId);
            card.setName(chosenCard.getName());
            card.setRarity(chosenCard.getRarity());
            card.setDescription(chosenCard.getDescription());
            card.setElement(chosenCard.getElement());
            card.setAttack(chosenCard.getAttack());
            card.setDefense(chosenCard.getDefense());
            card.setPhase(chosenCard.getPhase());
            card.setLife(chosenCard.getLife());

            cardRepository.save(card);
            packageCards.add(card);
        }

        return packageCards;
    }
}
