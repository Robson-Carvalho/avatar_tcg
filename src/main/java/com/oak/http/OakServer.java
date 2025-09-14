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
    private final ExecutorService wsWorkerPool;
    private final ExecutorService wsReadPool;

    public OakServer(int port) {
        this.port = port;
        this.router = new HttpRouter();

        int cores = Runtime.getRuntime().availableProcessors();

        // Pool para workers que processam as mensagens WebSocket
        this.wsWorkerPool = Executors.newFixedThreadPool(cores * 2);

        // Pool dedicado para leitura de WebSockets (substitui o new Thread)
        this.wsReadPool = Executors.newFixedThreadPool(cores);

        // Pool principal para conexões HTTP
        int maxPoolSize = cores * 2;
        long keepAlive = 60L;
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(5000);

        this.threadPool = new ThreadPoolExecutor(
                cores,
                maxPoolSize,
                keepAlive,
                TimeUnit.SECONDS,
                queue,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        // Adiciona hook para desligamento graceful
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void shutdown() {
        // Desliga todos os pools de threads de maneira ordenada
        threadPool.shutdown();
        wsReadPool.shutdown();
        wsWorkerPool.shutdown();

        try {
            // Aguarda até 30 segundos para terminação graceful
            if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                threadPool.shutdownNow(); // Força desligamento se necessário
            }
            if (!wsReadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                wsReadPool.shutdownNow();
            }
            if (!wsWorkerPool.awaitTermination(30, TimeUnit.SECONDS)) {
                wsWorkerPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            // Se for interrompido, força desligamento de todos os pools
            threadPool.shutdownNow();
            wsReadPool.shutdownNow();
            wsWorkerPool.shutdownNow();
            Thread.currentThread().interrupt(); // Restaura flag de interrupção
        }
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port, 5000)) {
            String ip = IPv4.getLocalIPv4();
            System.out.println("Oak Server running on http://" + ip + ":" + port);

            // Loop principal do servidor
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
                    // Trata requisições OPTIONS (preflight CORS)
                    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                        new Config().handlePreflightRequest(response);
                        clientSocket.close();
                        return;
                    }

                    // Roteia requisições HTTP normais
                    router.handle(request, response);

                    try {
                        clientSocket.shutdownOutput();
                        clientSocket.close();
                    } catch (IOException e) {
                        System.out.println("Erro ao fechar socket: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.out.println("Erro no handleConnection: " + e.getMessage());
                try { clientSocket.close(); } catch (IOException ignored) {}
            }
        });
    }

    private boolean isWebSocketUpgrade(HttpRequest request) {
        String upgrade = request.getHeader("upgrade");
        return "websocket".equalsIgnoreCase(upgrade) && router.hasWebSocketRoute(request.getPath());
    }

    private void handleWebSocket(HttpRequest request, HttpResponse response, Socket socket) throws IOException {
        // Executa handshake WebSocket
        router.handleWebSocket(request, response);

        if (response.getStatus() == 101) {
            // Handshake bem-sucedido
            WebSocket ws = new WebSocket(socket);
            WebSocketHandler handler = router.getWebSocketHandler(request);

            try {
                handler.onOpen(ws);
            } catch (IOException openEx) {
                try { ws.close(); } catch (IOException ignored) {}
                throw openEx;
            }

            // Inicia escuta de mensagens usando o pool dedicado
            listenWebSocket(ws, handler);
        } else {
            // Handshake falhou
            if (response.getStatus() == 0) response.setStatus(400);
            response.send("WebSocket handshake failed");

            try { socket.close(); } catch (IOException e) {
                System.out.println("Erro ao fechar socket: " + e.getMessage());
            }
        }
    }

    // Loop de escuta do WebSocket usando pool dedicado
    private void listenWebSocket(WebSocket ws, WebSocketHandler handler) {
        wsReadPool.submit(() -> {
            try {
                while (ws.isOpen()) {
                    String message = ws.receive();
                    if (message == null) break;

                    Map<String, Object> receive = JsonParser.parseJsonToMap(message);

                    // Envia para o pool de workers processar a mensagem
                    wsWorkerPool.submit(() -> {
                        try {
                            handler.onMessage(ws, receive);
                        } catch (Exception e) {
                            System.out.println("Erro no handler WebSocket: " + e.getMessage());
                            try { ws.close(); } catch (IOException ignored) {}
                        }
                    });
                }
            } catch (IOException e) {
                System.out.println("Erro na recepção WebSocket: " + e.getMessage());
            } finally {
                try { handler.onClose(ws); } catch (Exception ignored) {}
                try { ws.close(); } catch (IOException ignored) {}
            }
        });
    }

    // Métodos para adicionar rotas HTTP e WebSocket
    public void get(String path, HttpHandler handler) { router.addRoute("GET", path, handler); }
    public void post(String path, HttpHandler handler) { router.addRoute("POST", path, handler); }
    public void put(String path, HttpHandler handler) { router.addRoute("PUT", path, handler); }
    public void delete(String path, HttpHandler handler) { router.addRoute("DELETE", path, handler); }
    public void websocket(String path, WebSocketHandler handler) { router.addRoute("GET", path, handler); }
}