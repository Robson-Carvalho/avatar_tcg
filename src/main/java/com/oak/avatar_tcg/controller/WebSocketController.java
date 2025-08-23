package com.oak.avatar_tcg.controller;

import com.oak.http.WebSocket;
import com.oak.http.WebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketController {
    private final ConcurrentHashMap<WebSocket, String> clients = new ConcurrentHashMap<>();

    public WebSocketHandler websocket() {
        return new WebSocketHandler() {

            @Override
            public void onOpen(WebSocket ws) {
                String clientInfo = "connected: " + ws.getSocket().getRemoteSocketAddress();
                clients.put(ws, clientInfo);

                if(ws.isOpen()){
                    System.out.println("Client connected: " + clientInfo);
                }

            }

            @Override
            public void onMessage(WebSocket ws,  Map<String, String> message) {
                /* toda lógica estará aqui - cada client terá um socket com essa
                 classe instanciada, logo quando um client específico manda uma mensagem
                  o socket vai pega sua instância dessa classe e tratar o envio do client */


                System.out.println("Received message: " + message.get("message"));

                broadcastMessage(ws, message);
            }

            @Override
            public void onClose(WebSocket ws) {
                String clientInfo = "closed: " + ws.getSocket().getRemoteSocketAddress();
                clients.remove(ws);

                if(!ws.isOpen()){
                    System.out.println("Client disconnected: " + clientInfo);
                }
            }
        };
    }

    private void broadcastMessage(WebSocket sender, Map<String, String> message) {
        clients.keySet().forEach(ws -> {
            try {
                if (ws.isOpen() && !ws.equals(sender)) {
                    ws.send(message.toString());
                }
            } catch (IOException e) {
                System.out.println("Error broadcasting message: " + e.getMessage());
            }
        });
    }

    private void broadcastMessage(Map<String, String> message) {
        clients.keySet().forEach(ws -> {
            try {
                if (ws.isOpen()) {
                    ws.send(message.toString());
                }
            } catch (IOException e) {
                System.out.println("Error broadcasting message: " + e.getMessage());
            }
        });
    }
}