package com.oak.http.websocket;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class WebSocket {
    private final Socket socket;
    private final InputStream input;
    private final OutputStream output;

    public WebSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.input = socket.getInputStream();
        this.output = socket.getOutputStream();
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

    public void send(String message) throws IOException {
        byte[] data = message.getBytes("UTF-8");
        ByteArrayOutputStream frame = new ByteArrayOutputStream();

        // FIN bit + opcode texto (0x81 = 10000001)
        frame.write(0x81); // FIN=1, RSV=0, Opcode=1 (text)

        // Tamanho do payload (Seção 5.2)
        if (data.length <= 125) {
            frame.write(data.length); // 7 bits
        } else if (data.length <= 65535) {
            frame.write(126); // indica que vem 16 bits
            frame.write((data.length >> 8) & 0xFF); // high byte
            frame.write(data.length & 0xFF);        // low byte
        } else {
            frame.write(127); // indica que vem 64 bits
            for (int i = 7; i >= 0; i--) {
                frame.write((data.length >> (8 * i)) & 0xFF);
            }
        }

        frame.write(data);
        output.write(frame.toByteArray());
    }

    public String receive() throws IOException {
        int b = input.read();
        int opcode = b & 0x0F; // Pega os 4 bits menos significativos

        switch (opcode) {
            case 0x8: // Close frame (Seção 5.5.1)
                close();
                return null;
            case 0x1: // Text frame (Seção 5.6)
            case 0x2: // Binary frame
                return readDataFrame();
            default:  // Outros opcodes (ping/pong etc)
                skipFrame();
                return receive();
        }
    }

    private String readDataFrame() throws IOException {
        int b = input.read();
        int length = b & 0x7F; // Pega os 7 bits de tamanho
        boolean masked = (b & 0x80) != 0; // MASK bit

        // Length extendido (Seção 5.2)
        if (length == 126) {
            length = (input.read() << 8) | input.read();
        } else if (length == 127) {
            long longLength = 0;
            for (int i = 0; i < 8; i++) {
                longLength = (longLength << 8) | (input.read() & 0xFF);
            }
            length = (int) longLength;
        }

        // Masking key (Seção 5.3)
        byte[] mask = new byte[4];
        if (masked) {
            input.read(mask, 0, 4);
        }

        // Payload data
        byte[] payload = new byte[length];
        input.read(payload);

        // Aplica mask (XOR) - Seção 5.3
        if (masked) {
            for (int i = 0; i < payload.length; i++) {
                payload[i] ^= mask[i % 4];
            }
        }

        return new String(payload, "UTF-8");
    }

    private void skipFrame() throws IOException {
        int secondByte = input.read();
        if (secondByte == -1) return;

        int length = secondByte & 0x7F;
        boolean masked = (secondByte & 0x80) != 0;

        // Lê length extendido se necessário
        if (length == 126) {
            length = (input.read() << 8) | input.read();
        } else if (length == 127) {
            // Descarta os 8 bytes do length extendido
            for (int i = 0; i < 8; i++) input.read();
            // Não podemos determinar o length real sem ler os 8 bytes,
            // então assumimos que precisamos skipar baseado no length original
        }

        // Skip masking key
        if (masked) {
            for (int i = 0; i < 4; i++) input.read();
        }

        // Skip do payload de forma mais eficiente
        skipBytes(length);
    }

    private void skipBytes(int numBytes) throws IOException {
        // Método mais eficiente para pular bytes
        long skipped = input.skip(numBytes);
        while (skipped < numBytes) {
            skipped += input.skip(numBytes - skipped);
        }
    }
}