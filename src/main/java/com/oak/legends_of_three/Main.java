package com.oak.legends_of_three;

import com.oak.http.HttpServer;
import com.oak.legends_of_three.controller.*;
import com.oak.legends_of_three.database.Migrations;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        HttpServer server = new HttpServer(8080);

        Migrations.runMigrations();

        AuthController authController = new AuthController();
        CardController cardController = new CardController();
        DeckController deckController = new DeckController();
        WelcomeController welcomeController = new WelcomeController();
        WebSocketController webSocketController = new WebSocketController();

        // Welcome
        server.get("/", welcomeController::welcome);
        server.get("/{name}", welcomeController::welcomeDynamic);

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
        server.websocket("/ws", webSocketController.websocket());

        server.start();
    }
}
