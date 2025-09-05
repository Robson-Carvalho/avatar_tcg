package com.oak.avatar_tcg.controller;

import com.oak.http.HttpRequest;
import com.oak.http.HttpResponse;

import java.io.IOException;
import java.util.Map;

public class WelcomeController {

    public void welcome(HttpRequest request, HttpResponse response) throws IOException {
        try {
            response.json(Map.of(
                "message", "Welcome to OAK Server!"
            ));
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.json(Map.of("error", e.getMessage()));
        }
    }
}
