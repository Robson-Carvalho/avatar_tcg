package com.oak.http.websocket;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class WebSocket {
    private final Socket socket;
    private final InputStream input;
    private final OutputStream output;

    public WebSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.input = socket.getInputStream();
        this.output = socket.getOutputStream();
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
            return switch (opcode) {
                case 0x8 -> {
                    close();
                    yield null;
                }
                case 0x1, 0x2 -> // Binary frame
                        readDataFrame();
                default -> {
                    // Ignora frames não suportados
                    skipFrame();
                    yield receive();
                }
            };
        } catch (SocketException e) {
            // Cliente desconectou
            return null;
        }
    }

    private String readDataFrame() throws IOException {
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

    public void closeCleanly() {
        try {
            if (!socket.isClosed()) {
                // Avisa ao cliente que não vamos mais enviar dados
                socket.shutdownOutput();

                // Opcional: fecha a leitura também
                socket.shutdownInput();

                // Por fim, fecha o socket
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void close() throws IOException {
        if (!socket.isClosed()) {
            socket.close();
        }
    }
}