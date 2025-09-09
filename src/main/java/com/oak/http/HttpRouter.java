package com.oak.http;

import com.oak.http.interfaces.HttpHandler;
import com.oak.http.interfaces.WebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class HttpRouter {
    private final Map<String, Map<String, HttpHandler>> routes = new HashMap<>();
    private final Map<String, Map<String, WebSocketHandler>> routesWebSocket = new HashMap<>();


    public void addRoute(String method, String path, HttpHandler handler) {
        routes.computeIfAbsent(method.toUpperCase(), k -> new HashMap<>()).put(path, handler);
    }

    public void addRoute(String method, String path, WebSocketHandler handler){
        routesWebSocket.computeIfAbsent(method.toUpperCase(), k -> new HashMap<>()).put(path, handler);
    }

    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        String method = request.getMethod();
        String path = request.getPath();

        if (routes.containsKey(method) && routes.get(method).containsKey(path)) {
            routes.get(method).get(path).handle(request, response);
        }else{
            response.setStatus(404);
            response.send("HTTP endpoint not found");
        }
    }

    public boolean hasWebSocketRoute(String path) {
        return routesWebSocket.values().stream().anyMatch(map -> map.containsKey(path));
    }

    public WebSocketHandler getWebSocketHandler(HttpRequest request) {
        String method = request.getMethod();
        String path = request.getPath();

        if (routesWebSocket.containsKey(method) && routesWebSocket.get(method).containsKey(path)) {
            return routesWebSocket.get(method).get(path);
        }

        return null;
    }

    public void handleWebSocket(HttpRequest request, HttpResponse response) throws IOException {
        String method = request.getMethod();
        String path = request.getPath();

        if (routesWebSocket.containsKey(method) && routesWebSocket.get(method).containsKey(path)) {
            routesWebSocket.get(method).get(path).handleHandshake(request, response);
        } else {
            response.setStatus(404);
            response.send("WebSocket endpoint not found");
        }
    }
}
