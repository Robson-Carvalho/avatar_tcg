package com.oak.legends_of_three;

import com.oak.http.HttpServer;
import com.oak.legends_of_three.controller.AuthController;
import com.oak.legends_of_three.controller.CardController;
import com.oak.legends_of_three.controller.DeckController;
import com.oak.legends_of_three.controller.WebSocketController;
import com.oak.legends_of_three.database.Migrations;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        HttpServer server = new HttpServer(8080);

        Migrations.runMigrations();

        AuthController authController = new AuthController();
        CardController cardController = new CardController();
        DeckController deckController = new DeckController();
        WebSocketController webSocketController = new WebSocketController();

        // Welcome
        server.get("/", (req, res) -> {
            res.json(Map.of("message", "Welcome to the Oak Web Server!"));
        });

        // Auth
        server.post("/auth/login", authController::login);
        server.post("/auth/register", authController::register);

        // Card and Deck
        server.get("/card", cardController::getCards);
        server.get("/card/open", cardController::openPackage);

        server.put("/deck", deckController::updateDeck);
        server.get("/deck", deckController::getDeck);

        // WebSocket (agora com parâmetro nomeado {roomId}, e alias param1 mantido)
        server.websocket("/ws", webSocketController.websocket());

        server.start();
    }
}
