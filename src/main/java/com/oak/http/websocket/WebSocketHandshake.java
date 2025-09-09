package com.oak.http.websocket;

import com.oak.http.HttpRequest;
import com.oak.http.HttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class WebSocketHandshake {
    public static void doHandshake(HttpRequest request, HttpResponse response) throws IOException {
        String key = request.getHeader("Sec-WebSocket-Key");
        if (key == null) {
            throw new IOException("Missing Sec-WebSocket-Key");
        }

        String acceptKey = generateAcceptKey(key);

        response.setStatus(101);
        response.setHeader("Upgrade", "websocket");
        response.setHeader("Connection", "Upgrade");
        response.setHeader("Sec-WebSocket-Accept", acceptKey);
        response.sendHandshake();
    }

    private static String generateAcceptKey(String key) throws IOException {
        try {
            String acceptSeed = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hash = sha1.digest(acceptSeed.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IOException("Failed to generate Sec-WebSocket-Accept", e);
        }
    }
}
