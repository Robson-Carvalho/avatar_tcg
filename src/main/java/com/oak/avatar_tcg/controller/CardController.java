package com.oak.avatar_tcg.controller;

import com.oak.http.HttpRequest;
import com.oak.http.HttpResponse;
import com.oak.avatar_tcg.model.Card;
import com.oak.avatar_tcg.service.AuthService;
import com.oak.avatar_tcg.service.CardService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CardController {
    private final CardService cardService;
    private final AuthService authService;

    public CardController() {
        this.cardService = new CardService();
        this.authService = new AuthService();
    }

    public void getCards(HttpRequest request, HttpResponse response) throws IOException {
        try {
            String token = request.getBearerToken();

            String user_id = authService.validateToken(token);

            List<Card> cards = cardService.findByUserId(user_id);

            response.json(Map.of(
                    "cards", cards
            ));

        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            response.setStatus(403);
            response.json(Map.of("error", e.getMessage()));
        }
    }

    public void openPackage(HttpRequest request, HttpResponse response) throws IOException {
        try {
            String token = request.getBearerToken();

            String user_id = authService.validateToken(token);

            List<Card> cards = cardService.openPackage(user_id);

            response.json(Map.of(
                    "cards", cards
            ));
            System.out.println("Package opened");
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            response.setStatus(403);
            response.json(Map.of("error", e.getMessage()));
        }
    }


}
