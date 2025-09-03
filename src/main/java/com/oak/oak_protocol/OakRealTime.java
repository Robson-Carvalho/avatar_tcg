package com.oak.oak_protocol;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

public class OakRealTime {
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final Socket socket;

    private final ObjectMapper mapper = new ObjectMapper();

    public OakRealTime(Socket socket, BufferedReader reader, BufferedWriter writer) {
        this.socket = socket;
        this.reader = reader;
        this.writer = writer;
    }

    // Recebe uma mensagem inteira (JSON) do cliente
    public String receive() throws IOException {
        if (socket.isClosed()) return null;

        // Ler header do protocolo
        String protocolLine = reader.readLine();
        if (protocolLine == null || !"OAK_PROTOCOL".equals(protocolLine.trim())) {
            return null; // Protocolo inválido
        }

        // Ler método (pode ser ignorado em fullduplex)
        String methodLine = reader.readLine();

        // Ler rota (pode ser ignorado em fullduplex)
        String routeLine = reader.readLine();

        StringBuilder jsonBody = new StringBuilder();
        String line;

        // Ler até encontrar linha vazia
        while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
            if (jsonBody.length() > 0) jsonBody.append("\n");
            jsonBody.append(line);
        }

        return jsonBody.length() > 0 ? jsonBody.toString() : null;
    }

    public Socket getSocket() {
        return socket;
    }

    public void send(String data) throws IOException {
        System.out.println("Enviando mensagen: "+data);
        writer.write("OAK_PROTOCOL\r\n");
        writer.write("REALTIME\r\n"); // Método
        writer.write("/game\r\n"); // Rota
        writer.write(data);
        writer.write("\r\n\r\n"); // Duas quebras de linha para terminar
        writer.flush();
    }

    public void sendJson(Object data) throws IOException {
        String json = new ObjectMapper().writeValueAsString(data);
        send(json);
    }

    public boolean isOpen() {
        return !socket.isClosed() && socket.isConnected();
    }

    public void close() throws IOException {
        if (!socket.isClosed()) socket.close();
    }
}
