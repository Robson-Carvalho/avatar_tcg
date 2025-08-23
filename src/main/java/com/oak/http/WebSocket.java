package com.oak.http;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebSocket {
    private final Socket socket;
    private final InputStream input;
    private final OutputStream output;
    private final String requestPath;
    private long lastPongTime;
    private final ScheduledExecutorService pingScheduler;
    private static final long PING_INTERVAL = 30000; // 30 segundos
    private static final long PONG_TIMEOUT = 45000; // 45 segundos

    public WebSocket(Socket socket, HttpRequest request) throws IOException {
        this.socket = socket;
        this.input = socket.getInputStream();
        this.output = socket.getOutputStream();
        this.requestPath = request.getPath();
        this.lastPongTime = System.currentTimeMillis();
        this.pingScheduler = Executors.newSingleThreadScheduledExecutor();

        //startPingService();
    }

    private void startPingService() {
        pingScheduler.scheduleAtFixedRate(() -> {
            try {
                if (isOpen()) {
                    // Verifica timeout
                    if (System.currentTimeMillis() - lastPongTime > PONG_TIMEOUT) {
                        System.out.println("WebSocket timeout, closing connection");
                        close();
                        return;
                    }

                    // Envia ping
                    sendPing();
                } else {
                    pingScheduler.shutdown();
                }
            } catch (IOException e) {
                System.err.println("Error in ping service: " + e.getMessage());
                try {
                    close();
                } catch (IOException ignored) {}
                pingScheduler.shutdown();
            }
        }, PING_INTERVAL, PING_INTERVAL, TimeUnit.MILLISECONDS);
    }

    public String getParam() {
        return requestPath;
    }

    public void send(String message) throws IOException {
        if (socket.isClosed()) {
            throw new IOException("Socket is closed");
        }

        byte[] data = message.getBytes("UTF-8");
        ByteArrayOutputStream frame = new ByteArrayOutputStream();

        // Frame FIN + text
        frame.write(0x81);

        // Tamanho do payload
        if (data.length <= 125) {
            frame.write(data.length);
        } else if (data.length <= 65535) {
            frame.write(126);
            frame.write((data.length >> 8) & 0xFF);
            frame.write(data.length & 0xFF);
        } else {
            frame.write(127);
            for (int i = 7; i >= 0; i--) {
                frame.write((data.length >> (8 * i)) & 0xFF);
            }
        }

        frame.write(data);
        output.write(frame.toByteArray());
        output.flush();
    }

    public String receive() throws IOException {
        try {
            // Lê o primeiro byte (bloqueante)
            int b = input.read();

            if (b == -1) {
                return null; // Conexão fechada
            }

            int opcode = b & 0x0F;

            // Handle diferentes tipos de frames
            switch (opcode) {
                case 0x8: // Close frame
                    close();
                    return null;

                case 0x9: // Ping frame
                    handlePing();
                    return receive(); // Continua lendo após responder

                case 0xA: // Pong frame
                    handlePong();
                    return receive(); // Continua lendo após processar pong

                case 0x1: // Text frame
                case 0x2: // Binary frame
                    return readDataFrame(b, opcode);

                default:
                    // Ignora frames não suportados
                    skipFrame();
                    return receive();
            }
        } catch (SocketException e) {
            // Cliente desconectou
            return null;
        }
    }

    private String readDataFrame(int firstByte, int opcode) throws IOException {
        int b = input.read();
        if (b == -1) return null;

        int length = b & 0x7F;
        boolean masked = (b & 0x80) != 0;

        if (length == 126) {
            length = (input.read() << 8) | input.read();
        } else if (length == 127) {
            // Lê 8 bytes para o length (64-bit)
            long longLength = 0;
            for (int i = 0; i < 8; i++) {
                longLength = (longLength << 8) | (input.read() & 0xFF);
            }
            if (longLength > Integer.MAX_VALUE) {
                throw new IOException("Frame too large");
            }
            length = (int) longLength;
        }

        byte[] mask = new byte[4];
        if (masked) {
            input.read(mask, 0, 4);
        }

        byte[] payload = new byte[length];
        int totalRead = 0;
        while (totalRead < length) {
            int read = input.read(payload, totalRead, length - totalRead);
            if (read == -1) {
                throw new IOException("Incomplete frame");
            }
            totalRead += read;
        }

        // Aplica a máscara se presente
        if (masked) {
            for (int i = 0; i < payload.length; i++) {
                payload[i] ^= mask[i % 4];
            }
        }

        return new String(payload, "UTF-8");
    }

    private void handlePing() throws IOException {
        // Responde automaticamente com pong
        sendPong();
    }

    private void handlePong() {
        // Atualiza o último tempo de pong recebido
        lastPongTime = System.currentTimeMillis();
    }

    private void sendPing() throws IOException {
        ByteArrayOutputStream frame = new ByteArrayOutputStream();
        frame.write(0x89); // FIN + ping frame (0x89 = 10001001)
        frame.write(0x00); // payload length 0
        output.write(frame.toByteArray());
        output.flush();
    }

    private void sendPong() throws IOException {
        ByteArrayOutputStream frame = new ByteArrayOutputStream();
        frame.write(0x8A); // FIN + pong frame (0x8A = 10001010)
        frame.write(0x00); // payload length 0
        output.write(frame.toByteArray());
        output.flush();
    }

    private void skipFrame() throws IOException {
        // Lê o segundo byte para obter o length
        int b = input.read();
        if (b == -1) return;

        int length = b & 0x7F;
        boolean masked = (b & 0x80) != 0;

        if (length == 126) {
            input.read(); input.read(); // Skip 2 bytes
        } else if (length == 127) {
            for (int i = 0; i < 8; i++) input.read(); // Skip 8 bytes
        }

        if (masked) {
            for (int i = 0; i < 4; i++) input.read(); // Skip mask
        }

        // Skip o payload
        for (int i = 0; i < length; i++) {
            input.read();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isOpen() {
        return !socket.isClosed() && socket.isConnected();
    }

    public void close() throws IOException {
        pingScheduler.shutdown();

        if (!socket.isClosed()) {
            socket.close();
        }
    }

    public static void doHandshake(HttpRequest request, HttpResponse response) throws IOException {
        String key = request.getHeader("Sec-WebSocket-Key");

        if (key == null) {
            throw new IOException("Missing Sec-WebSocket-Key");
        }

        String acceptKey = generateAcceptKey(key);

        response.setStatus(101);
        response.setHeader("Upgrade", "websocket");
        response.setHeader("Connection", "Upgrade");
        response.setHeader("Sec-WebSocket-Accept", acceptKey);
        response.sendHandshake();
    }

    private static String generateAcceptKey(String key) throws IOException {
        try {
            String acceptSeed = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hash = sha1.digest(acceptSeed.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IOException("Failed to generate Sec-WebSocket-Accept", e);
        }
    }
}