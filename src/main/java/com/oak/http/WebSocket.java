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

    public String receive() {
        try {
            if (socket.isClosed() || !socket.isConnected()) {
                return null;
            }

            // Lê o primeiro byte para verificar se há dados
            if (input.available() <= 0) {
                return null;
            }

            int b = input.read();
            if (b == -1) return null;

            int opcode = b & 0x0F;
            if (opcode == 8) { // close frame
                close();
                return null;
            } else if (opcode == 9) { // ping frame
                sendPong();
                return null;
            } else if (opcode != 1 && opcode != 2) { // não é texto ou binary frame
                return null;
            }

            b = input.read();
            int length = b & 0x7F;

            if (length == 126) {
                length = (input.read() << 8) | input.read();
            } else if (length == 127) {
                length = 0;
                for (int i = 0; i < 8; i++) {
                    length = (length << 8) | input.read();
                }
            }

            byte[] mask = new byte[4];
            input.read(mask, 0, 4);

            byte[] payload = new byte[length];
            input.read(payload, 0, length);

            // Aplica a máscara
            for (int i = 0; i < payload.length; i++) {
                payload[i] ^= mask[i % 4];
            }

            return new String(payload, "UTF-8");
        } catch (SocketException e) {
            System.out.println("Cliente desconectou abruptamente");
            try { close(); } catch (IOException ex) {}
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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
        response.send("");
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