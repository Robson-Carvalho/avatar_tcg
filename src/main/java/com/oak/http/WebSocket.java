package com.oak.http;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.util.Base64;

public class WebSocket {
    private final Socket socket;
    private final InputStream input;
    private final OutputStream output;
    private final String requestPath;

    public WebSocket(Socket socket, HttpRequest request) throws IOException {
        this.socket = socket;
        this.input = socket.getInputStream();
        this.output = socket.getOutputStream();
        this.requestPath = request.getPath();
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
            if (opcode == 8) { // close frame
                close();
                return null;
            } else if (opcode == 9) { // ping frame
                sendPong();
                return receive(); // Continua lendo após pong
            } else if (opcode != 1 && opcode != 2) { // não é texto ou binary frame
                // Ignora frames não suportados e continua lendo
                skipFrame();
                return receive();
            }

            b = input.read();
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
        } catch (SocketException e) {
            // Cliente desconectou
            return null;
        }
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

    private void sendPong() throws IOException {
        ByteArrayOutputStream frame = new ByteArrayOutputStream();
        frame.write(0x8A); // FIN + pong frame
        frame.write(0x00); // payload length 0
        output.write(frame.toByteArray());
        output.flush();
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isOpen() {
        return !socket.isClosed() && socket.isConnected();
    }

    public void close() throws IOException {
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