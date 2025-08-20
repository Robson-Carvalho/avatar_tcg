package com.oak.legends_of_three.controller;

import com.oak.http.HttpRequest;
import com.oak.http.HttpResponse;
import com.oak.legends_of_three.model.Deck;
import com.oak.legends_of_three.service.AuthService;
import com.oak.legends_of_three.service.DeckService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

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

            authService.validateToken(token);

            Map<String, Object> body = request.getJsonBodyAsMap();

            String id = (String) body.get("id");
            String userId = (String) body.get("useId");
            String card1Id = (String) body.get("card1Id");
            String card2Id = (String) body.get("email");
            String card3Id = (String) body.get("email");
            String card4Id = (String) body.get("email");
            String card5Id = (String) body.get("email");

            Deck deck = new Deck(userId);
            deck.setId(id);
            deck.setUserId(userId);
            deck.setCard1Id(card1Id);
            deck.setCard2Id(card2Id);
            deck.setCard3Id(card3Id);
            deck.setCard4Id(card4Id);
            deck.setCard5Id(card5Id);

            Deck updatedDeck = deckService.updateDeck(deck);

            response.json(Map.of(
                    "deck", updatedDeck
            ));

        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            response.setStatus(403);
            response.json(Map.of("error", e.getMessage()));
        }
    }
}
