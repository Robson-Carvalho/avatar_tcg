package com.oak.http;

import java.io.IOException;

public interface WebSocketHandler {
    default void handleHandshake(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatus(101);
        response.setHeader("Upgrade", "websocket");
        response.setHeader("Connection", "Upgrade");
        response.send("");
    }

    default void onOpen(WebSocket ws) {}

    default void onMessage(WebSocket ws, String message) throws IOException {}

    default void onClose(WebSocket ws) {}

    default void listen(WebSocket ws) throws IOException {
        try {
            String message;
            while ((message = ws.receive()) != null) {
                onMessage(ws, message);
            }
        } finally {
            onClose(ws);
            ws.close();
        }
    }
}