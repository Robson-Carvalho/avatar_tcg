package com.oak.avatar_tcg.controller;

import com.oak.avatar_tcg.model.Card;
import com.oak.avatar_tcg.service.AuthService;
import com.oak.avatar_tcg.service.CardService;
import com.oak.oak_protocol.OakRequest;
import com.oak.oak_protocol.OakResponse;

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

    public void getCards(com.oak.oak_protocol.OakRequest request, com.oak.oak_protocol.OakResponse response) throws IOException {
        try {
            String token = request.getData("token");

            String user_id = authService.validateToken(token);

            List<Card> cards = cardService.findByUserId(user_id);

            response.sendJson(Map.of(
                    "status", "success",
                    "cards", cards
            ));
        } catch (Exception e) {
            response.sendJson(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    public void cardsAvailable(OakRequest request, OakResponse response) throws IOException {
        try {
            int num = cardService.getCardsAvailable();

            response.sendJson(Map.of(
                    "status", "success",
                    "cards_available", num
            ));
        } catch (Exception e) {
           response.sendJson(Map.of(
                   "status", "error",
                   "message", e.getMessage()
           ));
        }
    }

    public void openPackage(OakRequest request, OakResponse response) throws IOException {
        try {
            String token = request.getData("token");

            String user_id = authService.validateToken(token);

            List<Card> cards = cardService.openPackage(user_id);

            response.sendJson(Map.of(
                    "status", "success",
                    "cards", cards
            ));
        } catch (Exception e) {
            response.sendJson(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}
