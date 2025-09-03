package com.oak.oak_protocol;

import com.oak.avatar_tcg.util.JsonParser;
import com.oak.oak_protocol.util.JSON;
import com.oak.oak_protocol.util.OakData;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.*;

public class OakServer {
    private final int port;
    private final OakRoutes routes;

    private final ExecutorService executor;

    public OakServer(int port) {
        this.port = port;
        this.routes = new OakRoutes();
        this.executor = new ThreadPoolExecutor(
                10,
                10,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000)
        );
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Oak Server running on http://localhost:" + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> handleConnection(clientSocket));
            }
        }
    }

    private void closeSocket(Socket clientSocket) {
        try {
            clientSocket.shutdownOutput();
        } catch (IOException ignored) { }
        try {
            clientSocket.close();
        } catch (IOException ignored) { }
    }

    private void handleConectionHalfduplex(OakRequest request, OakResponse response, Socket socket) throws IOException {
        routes.handle(request, response);
        this.closeSocket(socket);
    }

    private void handleConectionFullduplex(OakRequest request, OakResponse response, Socket socket) throws IOException {
        OakRealTimeHandler handler = routes.handleRealTime(request.getPath());

        if(handler!=null){
            OakRealTime oak = new OakRealTime(socket, request.input, response.output);

            handler.onOpen(oak);
            this.executor.submit(() -> realTime(oak, handler));
        }
    }

    public void realTime(OakRealTime oakRealTime, OakRealTimeHandler handler) {
        try{
            while (oakRealTime.isOpen()){
                String message = oakRealTime.receive();

                if (message == null) {
                    break;
                }

                // adiciona depois, se não tiver OAK_PROTOCOL fecha a conexão
                //handler.onClose(oakRealTime);

                Map<String, Object> receive = JsonParser.parseJsonToMap(message);
                handler.onMessage(oakRealTime, receive);
            }
        }catch (IOException e) {
            System.out.println("error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            try {
                handler.onClose(oakRealTime);
            } catch (Exception ignored) {}
            try {
                oakRealTime.close();
            } catch (IOException ignored) {}
        }
    }

    private void handleConnection(Socket clientSocket) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));

            // Ler header do protocolo
            String protocolLine = input.readLine();
            if (protocolLine == null || !"OAK_PROTOCOL".equals(protocolLine.trim())) {
                System.out.println("Protocolo inválido!");
                output.write("OAK_PROTOCOL\r\n");
                output.write("Invalid protocol\r\n");
                output.flush();
                this.closeSocket(clientSocket);
                return;
            }

            // Ler comando HTTP (GET, POST, PUT, DELETE)
            String command = input.readLine();
            if (command == null) {
                System.out.println("Método não encontrado!");
                output.write("OAK_PROTOCOL\r\n");
                output.write("Invalid method\r\n");
                output.flush();
                this.closeSocket(clientSocket);
                return;
            }

            // Ler o caminho
            String path = input.readLine();
            if (path == null) {
                System.out.println("Path não encontrado!");
                output.write("OAK_PROTOCOL\r\n");
                output.write("Invalid path\r\n");
                output.flush();
                this.closeSocket(clientSocket);
                return;
            }

            StringBuilder jsonBody = new StringBuilder();
            String line;
            boolean emptyLineFound = false;

            while ((line = input.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    emptyLineFound = true;
                    break;
                }
                if (jsonBody.length() > 0) {
                    jsonBody.append("\n");
                }
                jsonBody.append(line);
            }

            if (!emptyLineFound || jsonBody.length() == 0) {
                System.out.println("Dados ausentes ou formato inválido!");
                output.write("OAK_PROTOCOL\r\n");
                output.write("Invalid data format\r\n");
                output.flush();
                this.closeSocket(clientSocket);
                return;
            }

            OakData data = JSON.fromJson(jsonBody.toString(), OakData.class);

            if (data == null) {
                System.out.println("Dados JSON incorretos!");
                output.write("OAK_PROTOCOL\r\n");
                output.write("Invalid JSON data\r\n");
                output.flush();
                this.closeSocket(clientSocket);
                return;
            }

            OakResponse response = new OakResponse(output);
            OakRequest request = new OakRequest(command, path, data, input);

            if ("halfduplex".equals(data.connection)) {
                handleConectionHalfduplex(request, response, clientSocket);
            } else if ("fullduplex".equals(data.connection)) {
                handleConectionFullduplex(request, response, clientSocket);
            } else {
                output.write("OAK_PROTOCOL\r\n");
                output.write("Invalid connection type\r\n");
                output.flush();
                this.closeSocket(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Erro: " + e.getMessage());
            this.closeSocket(clientSocket);
        }
    }


    public void realtime(String path, OakRealTimeHandler oakRealTimeHandler ) {
        routes.addRoute("REALTIME", path, oakRealTimeHandler);
    }

    public void get(String path, OakHandler handler) {
        routes.addRoute("GET", path, handler);
    }

    public void post(String path, OakHandler handler) {
        routes.addRoute("POST", path, handler);
    }

    public void put(String path, OakHandler handler) {
        routes.addRoute("PUT", path, handler);
    }

    public void delete(String path, OakHandler handler) {
        routes.addRoute("DELETE", path, handler);
    }
}
