package com.oak.http;

import java.io.*;
import java.net.Socket;

public class WebSocket {
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;

    public WebSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void send(String message) throws IOException {
        // Implementação simplificada (deveria usar frames WS)
        out.write(message + "\n");
        out.flush();
    }

    public String receive() throws IOException {
        // Leitura simplificada (deveria decodificar frames WS)
        return in.readLine();
    }

    public void close() throws IOException {
        socket.close();
    }
}