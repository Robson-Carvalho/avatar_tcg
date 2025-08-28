package com.oak.avatar_tcg;

import com.oak.http.OakServer;
import com.oak.avatar_tcg.controller.*;
import com.oak.avatar_tcg.database.Migrations;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        OakServer server = new OakServer(8080);

        Migrations.runMigrations();

        AuthController authController = new AuthController();
        CardController cardController = new CardController();
        DeckController deckController = new DeckController();
        WelcomeController welcomeController = new WelcomeController();
        WebSocketController webSocketController = new WebSocketController();

        // Welcome
        server.get("/", welcomeController::welcome);

        // Auth
        server.post("/auth/login", authController::login);
        server.post("/auth/register", authController::register);

        // Card
        server.get("/card", cardController::getCards);
        server.get("/card/open", cardController::openPackage);

        // Deck
        server.put("/deck", deckController::updateDeck);
        server.get("/deck", deckController::getDeck);

        // WebSocket
        server.websocket("/game", webSocketController.websocket());

        server.start();
    }
}
