package com.oak.http;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class HttpRouter {
    private final Map<String, Map<String, HttpHandler>> routes = new HashMap<>();

    public void addRoute(String method, String path, HttpHandler handler) {
        routes.computeIfAbsent(method.toUpperCase(), k -> new HashMap<>()).put(path, handler);
    }

    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, HttpHandler> methodRoutes = routes.get(request.method());
        if (methodRoutes != null) {
            HttpHandler handler = methodRoutes.get(request.path());
            if (handler != null) {
                handler.handle(request, response);
                return;
            }
        }

        response.setStatus(404);
        response.send("Route not found");
    }
}