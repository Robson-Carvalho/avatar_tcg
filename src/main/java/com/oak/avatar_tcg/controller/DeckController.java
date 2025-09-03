package com.oak.avatar_tcg.controller;


import com.oak.avatar_tcg.model.Deck;
import com.oak.avatar_tcg.service.AuthService;
import com.oak.avatar_tcg.service.DeckService;
import com.oak.oak_protocol.OakRequest;
import com.oak.oak_protocol.OakResponse;

import java.io.IOException;
import java.util.*;

public class DeckController {
    private final AuthService authService;
    private final DeckService deckService;

    public DeckController() {
        this.authService = new AuthService();
        this.deckService = new DeckService();
    }

    public void getDeck(OakRequest request, OakResponse response) throws IOException {
        try {
            String token = request.getData("token");


            String user_id = authService.validateToken(token);

            Deck deck = deckService.findByUserId(user_id);

            if(deck == null){
                response.sendJson(Map.of(
                        "status", "error",
                        "message", "Deck não encontrado!"
                ));
                return;
            }

            response.sendJson(Map.of(
                    "status", "success",
                    "deck", deck
            ));

        } catch (Exception e) {
            response.sendJson(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    public void updateDeck(OakRequest request, OakResponse response) throws IOException {
        try {
            String token = request.getData("token");

            authService.validateToken(token);

            String id = request.getData("deckID");
            String userID = request.getData("userID");

            if(id == null || userID == null){
                response.sendJson(Map.of(
                        "status", "error",
                        "message","Deck ID ou User ID estão inválidos"
                ));
                return;
            }

            Deck deck = deckService.findByUserId(userID);

            String card1Id = request.getData("card1Id");
            String card2Id = request.getData("card2Id");
            String card3Id = request.getData("card3Id");
            String card4Id = request.getData("card4Id");
            String card5Id = request.getData("card5Id");

            List<String> cards = new ArrayList<>();

            if(card1Id != null) cards.add(card1Id);
            if(card2Id != null) cards.add(card2Id);
            if(card3Id != null) cards.add(card3Id);
            if(card4Id != null) cards.add(card4Id);
            if(card5Id != null) cards.add(card5Id);

            List<String> nonNullCards = cards.stream().filter(Objects::nonNull).toList();
            Set<String> uniqueCards = new HashSet<>(nonNullCards);

            if (uniqueCards.size() != nonNullCards.size()) {
                response.sendJson(Map.of(
                        "status", "error",
                        "message","Não pode repetir cartas no deck"
                ));
                return;
            }

            if (!Objects.equals(card1Id, "")) deck.setCard1Id(card1Id);
            if (!Objects.equals(card2Id, "")) deck.setCard2Id(card2Id);
            if (!Objects.equals(card3Id, "")) deck.setCard3Id(card3Id);
            if (!Objects.equals(card4Id, "")) deck.setCard4Id(card4Id);
            if (!Objects.equals(card5Id, "")) deck.setCard5Id(card5Id);

            Deck updatedDeck = deckService.updateDeck(deck);

            response.sendJson(Map.of(
                    "status", "success",
                    "deck", updatedDeck
            ));

        } catch (Exception e) {
            response.sendJson(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}
