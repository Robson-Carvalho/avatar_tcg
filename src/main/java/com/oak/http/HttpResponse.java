package com.oak.http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpResponse {
    private final OutputStream outputStream;
    private int status = 200;
    private final Map<String, String> headers = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.headers.put("Content-Type", "text/plain");
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {return status;}

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void json(Object data) throws IOException {
        setHeader("Content-Type", "application/json");
        send(mapper.writeValueAsString(data));
    }

    public void send(String body) throws IOException {
        byte[] bytes = body.getBytes("UTF-8");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

        // Write status line
        out.write("HTTP/1.1 " + status + " \r\n");

        // Set Content-Length if not set
        if (!headers.containsKey("Content-Length")) {
            setHeader("Content-Length", String.valueOf(bytes.length));
        }

        // Write headers
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            out.write(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }

        // End of headers
        out.write("\r\n");
        out.flush();

        // Write body
        outputStream.write(bytes);
        outputStream.flush();
    }

    public void sendHandshake() throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        out.write("HTTP/1.1 " + status + " Switching Protocols\r\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            out.write(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }
        out.write("\r\n");
        out.flush();
    }

    public void sendHtml(String html) throws IOException {
        setHeader("Content-Type", "text/html");
        send(html);
    }

    public void sendText(String text) throws IOException {
        setHeader("Content-Type", "text/plain");
        send(text);
    }
}