package com.oak.http;

import com.oak.avatar_tcg.util.JsonParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class OakServer {
    private final int port;
    private final HttpRouter router;
    private final List<WsRoute> webSocketRoutes;
    private final ExecutorService executor;

    public OakServer(int port) {
        this.port = port;
        this.router = new HttpRouter();
        this.webSocketRoutes = new ArrayList<>();
        this.executor = new ThreadPoolExecutor(
                10,
                10,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000)
        );
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port, 5000)) {
            System.out.println("Oak Server running on http://localhost:" + port);

            while(true) {
                Socket clientSocket = serverSocket.accept();
                clientSocket.getOutputStream().write("OI".getBytes());
                clientSocket.getOutputStream().flush();

                executor.submit(() -> handleConnection(clientSocket));
            }
        }
    }

    private void handleConnection(Socket clientSocket) {
        try {
            clientSocket.setSoTimeout(50000); // 5 segundos

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            OakRequest request = parseRequest(in);
            OakResponse response = new OakResponse(clientSocket.getOutputStream());


            addCorsHeaders(response, request);

            if (isWebSocketUpgrade(request)) {
                handleWebSocket(request, response, clientSocket);
            } else {
                if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                    handlePreflightRequest(response);
                    clientSocket.close();
                    return;
                }

                router.handle(request, response);
                try { clientSocket.shutdownOutput(); } catch (IOException ignored) {}
                try { clientSocket.close(); } catch (IOException ignored) {}
            }
        } catch (IOException e) {
            System.out.println("error: " + e.getMessage());
            try { clientSocket.close(); } catch (IOException ignored) {}
        }
    }

    private void handlePreflightRequest(OakResponse response) throws IOException {
        response.setStatus(200);
        response.send("");
    }

    private void addCorsHeaders(OakResponse response, OakRequest request) {
        String origin = request.getHeader("Origin");

        if (origin == null) {
            origin = "*";
        }

        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        response.setHeader("Access-Control-Max-Age", "3600");
    }

    private boolean isWebSocketUpgrade(OakRequest request) {
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

    private void handleWebSocket(OakRequest request, OakResponse response, Socket socket) throws IOException {
        String path = request.getPath();

        for (WsRoute route : webSocketRoutes) {
            Matcher matcher = route.pattern.matcher(path);
            if (matcher.matches()) {
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

                    this.executor.submit(() -> listenWebSocket(ws, handler));
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
                    break;
                }

                Map<String, Object> receive = JsonParser.parseJsonToMap(message);

                handler.onMessage(ws, receive);
            }
        } catch (IOException e) {
            System.out.println("error: "+e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                handler.onClose(ws);
            } catch (Exception ignored) {}
            try {
                ws.close();
            } catch (IOException ignored) {}
        }
    }

    private OakRequest parseRequest(BufferedReader in) throws IOException {
        String line = in.readLine();

        if (line == null) {
            throw new IOException("Warning: Empty request");
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

        return new OakRequest(method, path, body, headers);
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
        // Simplificado: usar pattern que só casa com o path exato
        Pattern pattern = Pattern.compile("^" + Pattern.quote(path) + "$");
        webSocketRoutes.add(new WsRoute(pattern, handler));
    }
}
