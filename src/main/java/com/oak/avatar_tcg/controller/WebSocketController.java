package com.oak.avatar_tcg.controller;

import com.oak.http.WebSocket;
import com.oak.http.WebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketController {
    private final ConcurrentHashMap<WebSocket, String> clients = new ConcurrentHashMap<>();

    public WebSocketHandler websocket() {
        return new WebSocketHandler() {

            @Override
            public void onOpen(WebSocket ws) {
                String clientInfo = "connected: " + ws.getSocket().getRemoteSocketAddress();
                clients.put(ws, clientInfo);
                System.out.println("Client connected: " + clientInfo);
            }

            @Override
            public void onMessage(WebSocket ws, String message) {
                String clientAddress = ws.getSocket().getRemoteSocketAddress().toString();
                System.out.println("Received from " + clientAddress + ": " + message);

                broadcastMessage(ws, message);
            }

            @Override
            public void onClose(WebSocket ws) {
                String clientInfo = "closed: " + ws.getSocket().getRemoteSocketAddress();
                clients.remove(ws);
                System.out.println("Client disconnected: " + clientInfo);
            }
        };
    }

    private void broadcastMessage(WebSocket sender, String message) {
        clients.keySet().forEach(ws -> {
            try {
                if (ws.isOpen() && !ws.equals(sender)) {
                    ws.send(message);
                }
            } catch (IOException e) {
                System.out.println("Error broadcasting message: " + e.getMessage());
            }
        });
    }
}