package com.oak.http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private final BufferedWriter writer;
    private int statusCode = 200;
    private final Map<String, String> headers = new HashMap<>();
    private String body;

    public HttpResponse(BufferedWriter writer) {
        this.writer = writer;
        this.headers.put("Content-Type", "text/plain");
    }

    public void setStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void send(String body) throws IOException {
        this.body = body;
        sendResponse();
    }

    private void sendResponse() throws IOException {
        writer.write("HTTP/1.1 " + statusCode + " " + getStatusMessage(statusCode) + "\r\n");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            writer.write(header.getKey() + ": " + header.getValue() + "\r\n");
        }
        writer.write("\r\n");
        if (body != null) {
            writer.write(body);
        }
        writer.flush();
    }

    private String getStatusMessage(int statusCode) {
        switch (statusCode) {
            case 200: return "OK";
            case 201: return "Created";
            case 404: return "Not Found";
            case 500: return "Internal Server Error";
            default: return "Unknown";
        }
    }
}