package com.oak.http.config;

import com.oak.http.HttpRequest;
import com.oak.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public HttpRequest parseRequest(BufferedReader in) throws IOException {
        String line = in.readLine();

        if (line == null) {
            throw new IOException("Aviso: requisição vazia");
        }

        String[] requestLine = line.split(" ");
        if (requestLine.length < 2) {
            throw new IOException("Invalid request line: " + line);
        }
        String method = requestLine[0];
        String path = requestLine[1];

        // Headers
        Map<String, String> headers = new HashMap<>();
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            String[] header = line.split(": ", 2);
            if (header.length == 2) {
                headers.put(header[0].toLowerCase(), header[1]);
            }
        }

        // Body
        int contentLength = 0;
        if (headers.containsKey("content-length")) {
            try {
                contentLength = Integer.parseInt(headers.get("content-length"));
            } catch (NumberFormatException ignored) {}
        }

        char[] bodyChars = new char[contentLength];
        if (contentLength > 0) {
            int read = 0;
            while (read < contentLength) {
                int r = in.read(bodyChars, read, contentLength - read);
                if (r == -1) break;
                read += r;
            }
            if (read < contentLength) {
                throw new IOException("Incomplete request body");
            }
        }

        String body = new String(bodyChars);
        return new HttpRequest(method, path, body, headers);
    }

    public void handlePreflightRequest(HttpResponse response) throws IOException {
        response.setStatus(200);
        response.send("");
    }

    public void addCorsHeaders(HttpResponse response, HttpRequest request) {
        String origin = request.getHeader("Origin");

        if (origin == null) {
            origin = "*";
        }

        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        response.setHeader("Access-Control-Max-Age", "3600");
    }
}
