package com.oak.http;

import java.io.IOException;
import java.util.Map;

public interface WebSocketHandler {

    // Handshake padrão
    default void handleHandshake(HttpRequest request, HttpResponse response) throws IOException {
        WebSocket.doHandshake(request, response);
    }

    // Chamado quando a conexão abre
    default void onOpen(WebSocket ws) throws IOException {}

    // Chamado quando recebe uma mensagem (JSON ou texto)
    default void onMessage(WebSocket ws, Map<String, String> message) throws IOException {}

    // Chamado quando a conexão fecha
    default void onClose(WebSocket ws) throws IOException {}
}
