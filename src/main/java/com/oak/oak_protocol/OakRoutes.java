package com.oak.oak_protocol;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OakRoutes {
    private final Map<String, Map<String, OakHandler>> routes = new HashMap<>();

    public void addRoute(String method, String path, OakHandler handler) {
        routes.computeIfAbsent(method.toUpperCase(), k -> new HashMap<>()).put(path, handler);
    }

    public void handle(OakRequest request, OakResponse response) throws IOException {
        String command = request.getCommand();
        String path = request.getPath();

        if (routes.containsKey(command) && routes.get(command).containsKey(path)) {
            routes.get(command).get(path).handle(request, response);
        }
    }

}
