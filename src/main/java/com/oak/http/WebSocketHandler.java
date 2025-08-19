package com.oak.http;

import java.io.IOException;

public interface WebSocketHandler {

    // Handshake padrão
    default void handleHandshake(HttpRequest request, HttpResponse response) throws IOException {
        WebSocket.doHandshake(request, response);
    }

    // Chamado quando a conexão abre
    default void onOpen(WebSocket ws) throws IOException {}

    // Chamado quando recebe uma mensagem (JSON ou texto)
    default void onMessage(WebSocket ws, String message) throws IOException {}

    // Chamado quando a conexão fecha
    default void onClose(WebSocket ws) {}

    // Loop de escuta (opcional, já implementado pelo servidor)
    default void listen(WebSocket ws) throws IOException {
        try {
            onOpen(ws);
            String msg;
            while ((msg = ws.receive()) != null) {
                onMessage(ws, msg);
            }
        } finally {
            onClose(ws);
            ws.close();
        }
    }
}
