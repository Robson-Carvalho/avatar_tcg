package com.oak.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpRequest {
    private final String method;
    private final String path;
    private final String body;
    private final Map<String, String> headers;
    private final Map<String, String> params = new HashMap<>();

    private static final ObjectMapper mapper = new ObjectMapper();

    public HttpRequest(String method, String path, String body, Map<String, String> headers) {
        this.method = method;
        this.path = path;
        this.body = body;
        this.headers = headers != null ? headers : new HashMap<>();
    }

    public void addParam(String name, String value) {params.put(name, value);}
    public String getMethod() { return method; }
    public String getPath() { return path; }

    public String getHeader(String name) {
        return headers.getOrDefault(name.toLowerCase(), null);
    }

    public String getBearerToken() {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("Authorization") && entry.getValue().startsWith("Bearer ")) {
                return entry.getValue().substring(7);
            }
        }
        return null;
    }

    public Map<String, Object> getJsonBodyAsMap() {
        try {
            if (body == null || body.isEmpty()) return Collections.emptyMap();
            return mapper.readValue(body, Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON body", e);
        }
    }
}
