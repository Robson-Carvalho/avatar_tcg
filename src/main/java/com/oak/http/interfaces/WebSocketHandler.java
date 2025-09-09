package com.oak.http.interfaces;

import com.oak.http.HttpRequest;
import com.oak.http.HttpResponse;
import com.oak.http.websocket.WebSocket;
import com.oak.http.websocket.WebSocketHandshake;

import java.io.IOException;
import java.util.Map;

public interface WebSocketHandler {

    // Handshake padrão
    default void handleHandshake(HttpRequest request, HttpResponse response) throws IOException {
        WebSocketHandshake.doHandshake(request, response);
    }

    // Chamado quando a conexão abre
    default void onOpen(WebSocket ws) throws IOException {}

    // Chamado quando recebe uma mensagem (JSON ou texto)
    default void onMessage(WebSocket ws, Map<String, Object> message) throws Exception {}

    // Chamado quando a conexão fecha
    default void onClose(WebSocket ws) throws IOException {}
}
