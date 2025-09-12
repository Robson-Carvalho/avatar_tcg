package com.oak.http.config;

import com.oak.http.HttpRequest;
import com.oak.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public HttpRequest parseRequest(BufferedReader in) throws IOException {
        // Ler request line
        String requestLineStr = in.readLine();
        if (requestLineStr == null) {
            throw new IOException("Aviso: requisição vazia");
        }

        // Print raw desde o início
        System.out.println("=== RAW HTTP REQUEST ===");
        System.out.println(requestLineStr);

        // Parsing request line
        String[] requestLine = requestLineStr.split(" ");
        if (requestLine.length < 3) {
            throw new IOException("Invalid request line: " + requestLineStr);
        }

        String method = requestLine[0];
        String path = requestLine[1];
        String httpVersion = requestLine[2];

        // Validar versão HTTP
        if (!httpVersion.startsWith("HTTP/1.0") && !httpVersion.startsWith("HTTP/1.1")) {
            throw new IOException("Unsupported HTTP version: " + httpVersion);
        }

        // Headers
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            System.out.println(line); // Print cada header
            String[] header = line.split(": ", 2);
            if (header.length == 2) {
                headers.put(header[0].toLowerCase(), header[1]);
            }
        }
        System.out.println(); // Print linha vazia

        // Body
        String body = "";
        if (headers.containsKey("content-length")) {
            try {
                int contentLength = Integer.parseInt(headers.get("content-length"));

                if (contentLength > 0) {
                    if (contentLength > 10485760) {
                        throw new IOException("Body too large");
                    }

                    char[] bodyChars = new char[contentLength];
                    int read = 0;
                    while (read < contentLength) {
                        int r = in.read(bodyChars, read, contentLength - read);
                        if (r == -1) break;
                        read += r;
                    }
                    if (read < contentLength) {
                        throw new IOException("Incomplete request body");
                    }
                    body = new String(bodyChars);
                    System.out.println(body); // Print body
                }
            } catch (NumberFormatException e) {
                throw new IOException("Invalid Content-Length value");
            }
        }

        System.out.println("=========================");

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
