package com.oak.http;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
    private final int port;
    private final HttpRouter router;
    private final Map<String, WebSocketHandler> webSocketHandlers;

    public HttpServer(int port) {
        this.port = port;
        this.router = new HttpRouter();
        this.webSocketHandlers = new HashMap<>();
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Oak Server running on http://localhost:" + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleConnection(clientSocket);
            }
        }
    }

    private void handleConnection(Socket clientSocket) {
        new Thread(() -> {
            try {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(clientSocket.getOutputStream()));

                HttpRequest request = parseRequest(in);
                HttpResponse response = new HttpResponse(out);

                if (isWebSocketUpgrade(request)) {
                    handleWebSocket(request, response, clientSocket);
                    return;
                }

                router.handle(request, response);
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private boolean isWebSocketUpgrade(HttpRequest request) {
        return "websocket".equalsIgnoreCase(request.getHeader("upgrade")) &&
                webSocketHandlers.containsKey(request.path());
    }

    private void handleWebSocket(HttpRequest request, HttpResponse response, Socket socket) throws IOException {
        WebSocketHandler handler = webSocketHandlers.get(request.path());
        if (handler != null) {
            handler.handleHandshake(request, response);
            WebSocket ws = new WebSocket(socket);
            handler.onOpen(ws);
            handler.listen(ws);
        }
    }

    private HttpRequest parseRequest(BufferedReader in) throws IOException {
        String line = in.readLine();
        if (line == null) {
            throw new IOException("Empty request");
        }

        String[] requestLine = line.split(" ");
        String method = requestLine[0];
        String path = requestLine[1];

        Map<String, String> headers = new HashMap<>();
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            String[] header = line.split(": ", 2);
            if (header.length == 2) {
                headers.put(header[0].toLowerCase(), header[1]);
            }
        }

        StringBuilder body = new StringBuilder();
        while (in.ready()) {
            body.append((char) in.read());
        }

        return new HttpRequest(method, path, headers, body.toString());
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
        webSocketHandlers.put(path, handler);
    }
}