package com.oak.avatar_tcg;

import com.oak.avatar_tcg.controller.*;
import com.oak.avatar_tcg.database.Migrations;
import com.oak.oak_protocol.OakServer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {

        AuthController authController = new AuthController();
        CardController cardController = new CardController();
        DeckController deckController = new DeckController();
        MatchController matchController = new MatchController();
        WelcomeController welcomeController = new WelcomeController();
        WebSocketController webSocketController = new WebSocketController();

        OakServer oakServer = new OakServer(8080);

        Migrations.runMigrations();

        // Welcome
        oakServer.get("/", welcomeController::welcome);

        oakServer.get("/ping", ( request, response) -> {
            response.sendJson(Map.of(
                    "status", "success",
                    "message", "pong!"
            ));
        });

        // Auth
        oakServer.post("/auth/login", authController::login);
        oakServer.post("/auth/register", authController::register);

        // Card
        oakServer.get("/card", cardController::getCards);
        oakServer.get("/card/available", cardController::cardsAvailable);
        oakServer.get("/card/open", cardController::openPackage);

        // Deck
        oakServer.put("/deck", deckController::updateDeck);
        oakServer.get("/deck", deckController::getDeck);

        // Match
        oakServer.get("/match", matchController::getMatchs);

//        // WebSocket
//        oakServer.websocket("/game", webSocketController.websocket());

        oakServer.start();
    }
}
