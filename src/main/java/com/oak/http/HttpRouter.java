package com.oak.http;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class HttpRouter {
    private final Map<String, Map<String, HttpHandler>> staticRoutes = new HashMap<>();
    private final Map<String, Map<Pattern, DynamicRoute>> dynamicRoutes = new HashMap<>();

    private static class DynamicRoute {
        final HttpHandler handler;
        final String[] paramNames;

        DynamicRoute(HttpHandler handler, String[] paramNames) {
            this.handler = handler;
            this.paramNames = paramNames;
        }
    }

    public void addRoute(String method, String path, HttpHandler handler) {
        if (path.contains("{") && path.contains("}")) {
            // Processa rota dinâmica
            String[] paramNames = extractParamNames(path);
            String regex = path.replaceAll("\\{(.*?)\\}", "([^/]+)");
            Pattern pattern = Pattern.compile("^" + regex + "$");

            dynamicRoutes.computeIfAbsent(method.toUpperCase(), k -> new HashMap<>())
                    .put(pattern, new DynamicRoute(handler, paramNames));
        } else {
            // Rota estática
            staticRoutes.computeIfAbsent(method.toUpperCase(), k -> new HashMap<>())
                    .put(path, handler);
        }
    }

    private String[] extractParamNames(String path) {
        java.util.List<String> paramNames = new java.util.ArrayList<>();
        Matcher m = Pattern.compile("\\{(.*?)\\}").matcher(path);
        while (m.find()) {
            paramNames.add(m.group(1));
        }
        return paramNames.toArray(new String[0]);
    }

    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        String method = request.getMethod();
        String path = request.getPath();

        // Primeiro tenta rotas estáticas
        if (staticRoutes.containsKey(method) && staticRoutes.get(method).containsKey(path)) {
            staticRoutes.get(method).get(path).handle(request, response);
            return;
        }

        // Depois tenta rotas dinâmicas
        if (dynamicRoutes.containsKey(method)) {
            for (Map.Entry<Pattern, DynamicRoute> entry : dynamicRoutes.get(method).entrySet()) {
                Matcher matcher = entry.getKey().matcher(path);
                if (matcher.matches()) {
                    DynamicRoute route = entry.getValue();
                    // Adiciona parâmetros à requisição
                    for (int i = 0; i < route.paramNames.length; i++) {
                        request.addParam(route.paramNames[i], matcher.group(i + 1));
                    }
                    route.handler.handle(request, response);
                    return;
                }
            }
        }

        response.setStatus(404);
        response.send("Route not found");
    }
}
