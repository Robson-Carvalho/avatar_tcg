package com.oak.http;

import com.oak.avatar_tcg.util.IPv4;
import com.oak.avatar_tcg.util.JsonParser;
import com.oak.http.config.Config;
import com.oak.http.interfaces.HttpHandler;
import com.oak.http.interfaces.WebSocketHandler;
import com.oak.http.websocket.WebSocket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;

public class OakServer {
    private final int port;
    private final HttpRouter router;
    private final ExecutorService threadPool;

    public OakServer(int port) {
        this.port = port;
        this.router = new HttpRouter();

        // Configura ThreadPool com base no sistema
        int cores = Runtime.getRuntime().availableProcessors();
        int corePoolSize = cores * 2;     // threads sempre ativas
        int maxPoolSize = cores * 4;      // máximo de threads
        long keepAlive = 60L;              // segundos

        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(500); // fila de conexões

        this.threadPool = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAlive,
                TimeUnit.SECONDS,
                queue,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void shutdown() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            String ip = IPv4.getLocalIPv4();
            System.out.println("Oak Server running on http://" + ip + ":" + port);

            for(;;){
                Socket clientSocket = serverSocket.accept();
                handleConnection(clientSocket);
            }
        }
    }

    private void handleConnection(Socket clientSocket) {
        threadPool.submit(() -> {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                HttpRequest request = new Config().parseRequest(in);
                HttpResponse response = new HttpResponse(clientSocket.getOutputStream());
                new Config().addCorsHeaders(response, request);

                if (isWebSocketUpgrade(request)) {
                    handleWebSocket(request, response, clientSocket);
                } else {
                    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                        new Config().handlePreflightRequest(response);
                        clientSocket.close();
                        return;
                    }

                    router.handle(request, response);

                    try {
                        clientSocket.shutdownOutput();
                        clientSocket.close();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                try { clientSocket.close(); } catch (IOException ignored) {}
            }
        });
    }

    private boolean isWebSocketUpgrade(HttpRequest request) {
        String upgrade = request.getHeader("upgrade");
        return "websocket".equalsIgnoreCase(upgrade) && router.hasWebSocketRoute(request.getPath());
    }

    private void handleWebSocket(HttpRequest request, HttpResponse response, Socket socket) throws IOException {
        router.handleWebSocket(request, response);

        if (response.getStatus() == 101) {
            WebSocket ws = new WebSocket(socket);
            WebSocketHandler handler = router.getWebSocketHandler(request);

            try {
                handler.onOpen(ws);
            } catch (IOException openEx) {
                try { ws.close(); } catch (IOException ignored) {}
                throw openEx;
            }

            threadPool.submit(() -> listenWebSocket(ws, handler));
        } else {
            if (response.getStatus() == 0) response.setStatus(400);
            response.send("WebSocket handshake failed");

            try { socket.close(); } catch (IOException e) { System.out.println(e.getMessage()); }
        }
    }

    // Loop de escuta do WebSocket
    private void listenWebSocket(WebSocket ws, WebSocketHandler handler) {
        try {
            while (ws.isOpen()) {
                String message = ws.receive();
                if (message == null) break;

                Map<String, Object> receive = JsonParser.parseJsonToMap(message);
                handler.onMessage(ws, receive);
            }
        } catch (IOException e) {
            System.out.println("error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try { handler.onClose(ws); } catch (Exception ignored) {}
            try { ws.close(); } catch (IOException ignored) {}
        }
    }

    // Métodos para adicionar rotas HTTP e WebSocket
    public void get(String path, HttpHandler handler) { router.addRoute("GET", path, handler); }
    public void post(String path, HttpHandler handler) { router.addRoute("POST", path, handler); }
    public void put(String path, HttpHandler handler) { router.addRoute("PUT", path, handler); }
    public void delete(String path, HttpHandler handler) { router.addRoute("DELETE", path, handler); }
    public void websocket(String path, WebSocketHandler handler) { router.addRoute("GET", path, handler); }
}
