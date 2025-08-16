package com.oak.http;

import java.util.Map;
import java.util.HashMap;

public record HttpRequest(String method, String path, Map<String, String> headers, String body) {
    public HttpRequest(String method, String path, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers != null ? headers : new HashMap<>();
        this.body = body;
    }

    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }
}