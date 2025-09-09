package com.oak.http.interfaces;

import com.oak.http.HttpRequest;
import com.oak.http.HttpResponse;

import java.io.IOException;

@FunctionalInterface
public interface HttpHandler {
    void handle(HttpRequest request, HttpResponse response) throws IOException;
}