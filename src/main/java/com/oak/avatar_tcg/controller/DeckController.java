package com.oak.avatar_tcg.controller;

import com.oak.http.HttpRequest;
import com.oak.http.HttpResponse;
import com.oak.avatar_tcg.model.Deck;
import com.oak.avatar_tcg.service.AuthService;
import com.oak.avatar_tcg.service.DeckService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class DeckController {
    private final AuthService authService;
    private final DeckService deckService;

    public DeckController() {
        this.authService = new AuthService();
        this.deckService = new DeckService();
    }

    public void getDeck(HttpRequest request, HttpResponse response) throws IOException {
        try {
            String token = request.getBearerToken();

            String user_id = authService.validateToken(token);

            Deck deck = deckService.findByUserId(user_id);

            if(deck == null){
                response.setStatus(404);
                response.json(Map.of(
                        "deck", new ArrayList<Deck>() {
                        }
                ));
                return;
            }

            response.json(Map.of(
                    "deck", deck
            ));
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            response.setStatus(403);
            response.json(Map.of("error", e.getMessage()));
        }
    }

    public void updateDeck(HttpRequest request, HttpResponse response) throws IOException {
        try {
            String token = request.getBearerToken();

            String user_id = authService.validateToken(token);

            Map<String, Object> body = request.getJsonBodyAsMap();

            String id = (String) body.get("id");

            if(id == null){
                response.setStatus(400);
                response.json(Map.of("error", "Deck Id é requerido!"));
                return;
            }

            String card1Id = (String) body.get("card1Id");
            String card2Id = (String) body.get("card2Id");
            String card3Id = (String) body.get("card3Id");
            String card4Id = (String) body.get("card4Id");
            String card5Id = (String) body.get("card5Id");

            Deck deck = new Deck(id);

            deck.setUserId(user_id);
            if (!Objects.equals(card1Id, "")) deck.setCard1Id(card1Id);
            if (!Objects.equals(card2Id, "")) deck.setCard2Id(card2Id);
            if (!Objects.equals(card3Id, "")) deck.setCard3Id(card3Id);
            if (!Objects.equals(card4Id, "")) deck.setCard4Id(card4Id);
            if (!Objects.equals(card5Id, "")) deck.setCard5Id(card5Id);


            Deck updatedDeck = deckService.updateDeck(deck);

            response.json(Map.of(
                    "deck", updatedDeck
            ));

        } catch (Exception e) {
            response.setStatus(400);
            response.json(Map.of("error", e.getMessage()));
        }
    }
}
