package com.oak.http;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class OakServer {
    private final int port;
    private final HttpRouter router;

    private static class WsRoute {
        final Pattern pattern;
        final WebSocketHandler handler;
        final String[] paramNames;

        WsRoute(Pattern pattern, WebSocketHandler handler, String[] paramNames) {
            this.pattern = pattern;
            this.handler = handler;
            this.paramNames = paramNames;
        }
    }

    private final List<WsRoute> webSocketRoutes;

    public OakServer(int port) {
        this.port = port;
        this.router = new HttpRouter();
        this.webSocketRoutes = new ArrayList<>();
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Oak Server running on http://localhost:" + port);

            while(true) {
                Socket clientSocket = serverSocket.accept();
                handleConnection(clientSocket);
            }
        }
    }

    private void handleConnection(Socket clientSocket) {
        new Thread(() -> {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                HttpRequest request = parseRequest(in);
                HttpResponse response = new HttpResponse(clientSocket.getOutputStream());

                if (isWebSocketUpgrade(request)) {
                    handleWebSocket(request, response, clientSocket);
                } else {
                    // Requisição HTTP normal
                    router.handle(request, response);
                    try { clientSocket.shutdownOutput(); } catch (IOException ignored) {}

                    try { clientSocket.close(); } catch (IOException ignored) {}
                }
            } catch (IOException e) {
                System.out.println("error: "+e.getMessage());

                try {
                    clientSocket.close();
                } catch (IOException ignored) {}
            }
        }, "oak-conn-handler").start();
    }

    private boolean isWebSocketUpgrade(HttpRequest request) {
        String upgrade = request.getHeader("upgrade");

        if (!"websocket".equalsIgnoreCase(upgrade)) {
            return false;
        }

        String path = request.getPath();

        for (WsRoute route : webSocketRoutes) {
            if (route.pattern.matcher(path).matches()) {
                return true;
            }
        }
        return false;
    }

    private void handleWebSocket(HttpRequest request, HttpResponse response, Socket socket) throws IOException {
        String path = request.getPath();

        for (WsRoute route : webSocketRoutes) {
            Matcher matcher = route.pattern.matcher(path);
            if (matcher.matches()) {
                // Preenche parâmetros capturados
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    String value = matcher.group(i);


                    request.addParam("param" + i, value);

                    // Param nomeado (se existir)
                    if (route.paramNames != null && (i - 1) < route.paramNames.length) {
                        String name = route.paramNames[i - 1];
                        if (name != null && !name.isEmpty()) {
                            request.addParam(name, value);
                        }
                    }
                }

                WebSocketHandler handler = route.handler;


                handler.handleHandshake(request, response);


                if (response.getStatus() == 101) {
                    WebSocket ws = new WebSocket(socket, request);


                    try {
                        handler.onOpen(ws);
                    } catch (IOException openEx) {
                        try { ws.close(); } catch (IOException ignored) {}
                        throw openEx;
                    }


                    new Thread(() -> listenWebSocket(ws, handler), "oak-ws-listener").start();
                } else {

                    if (response.getStatus() == 0) {
                        response.setStatus(400);
                    }

                    response.send("WebSocket handshake failed");
                    try { socket.close(); } catch (IOException ignored) {}
                }

                return;
            }
        }

        response.setStatus(404);
        response.send("WebSocket endpoint not found");
        try { socket.close(); } catch (IOException ignored) {}
    }

    private void listenWebSocket(WebSocket ws, WebSocketHandler handler) {
        try {
            while (ws.isOpen()) {
                String message = ws.receive();
                if (message == null) {
                    break; // conexão fechada pelo cliente
                }
                handler.onMessage(ws, message);
            }
        } catch (IOException e) {
            System.out.println("error: "+e.getMessage());
        } finally {
            try {
                handler.onClose(ws);
            } catch (Exception ignored) {}
            try {
                ws.close();
            } catch (IOException ignored) {}
        }
    }

    private HttpRequest parseRequest(BufferedReader in) throws IOException {
        String line = in.readLine();
        if (line == null) {
            throw new IOException("Empty request");
        }

        String[] requestLine = line.split(" ");
        if (requestLine.length < 2) {
            throw new IOException("Invalid request line: " + line);
        }
        String method = requestLine[0];
        String path = requestLine[1];

        // Headers
        Map<String, String> headers = new HashMap<>();
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            String[] header = line.split(": ", 2);
            if (header.length == 2) {
                headers.put(header[0].toLowerCase(), header[1]);
            }
        }

        // Body
        int contentLength = 0;
        if (headers.containsKey("content-length")) {
            try {
                contentLength = Integer.parseInt(headers.get("content-length"));
            } catch (NumberFormatException ignored) {}
        }

        char[] bodyChars = new char[contentLength];
        if (contentLength > 0) {
            int read = 0;
            while (read < contentLength) {
                int r = in.read(bodyChars, read, contentLength - read);
                if (r == -1) break;
                read += r;
            }
            if (read < contentLength) {
                throw new IOException("Incomplete request body");
            }
        }

        String body = new String(bodyChars);
        return new HttpRequest(method, path, body, headers);
    }

    public void get(String path, HttpHandler handler) {
        router.addRoute("GET", path, handler);
    }

    public void post(String path, HttpHandler handler) {
        router.addRoute("POST", path, handler);
    }

    public void put(String path, HttpHandler handler) {
        router.addRoute("PUT", path, handler);
    }

    public void delete(String path, HttpHandler handler) {
        router.addRoute("DELETE", path, handler);
    }

    public void websocket(String path, WebSocketHandler handler) {
        // Extrai nomes dos parâmetros e monta regex
        String[] paramNames = extractParamNames(path);

        String regex = path.replaceAll("\\{(.*?)\\}", "([^/]+)");
        Pattern pattern = Pattern.compile("^" + regex + "$");

        webSocketRoutes.add(new WsRoute(pattern, handler, paramNames));
    }

    private String[] extractParamNames(String path) {
        List<String> names = new ArrayList<>();
        Matcher m = Pattern.compile("\\{(.*?)\\}").matcher(path);
        while (m.find()) {
            names.add(m.group(1));
        }
        return names.toArray(new String[0]);
    }
}
