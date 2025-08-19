package com.oak.legends_of_three;

import com.oak.http.HttpServer;
import com.oak.legends_of_three.controller.AuthController;
import com.oak.legends_of_three.controller.WebSocketController;

import java.io.IOException;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(8080);

        AuthController authController = new AuthController();
        WebSocketController webSocketController = new WebSocketController();

        // Welcome
        server.get("/", (req, res) -> {
            res.json(Map.of("message", "Welcome to the Oak Web Server!"));
        });

        // Auth
        server.post("/auth/login", authController::login);
        server.post("/auth/register", authController::register);

        // WebSocket (agora com parâmetro nomeado {roomId}, e alias param1 mantido)
        server.websocket("/ws", webSocketController.websocket());

        server.start();
    }
}
