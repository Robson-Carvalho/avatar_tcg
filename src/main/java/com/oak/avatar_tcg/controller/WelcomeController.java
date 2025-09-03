package com.oak.avatar_tcg.controller;

import com.oak.oak_protocol.OakRequest;
import com.oak.oak_protocol.OakResponse;

import java.io.IOException;
import java.util.Map;

public class WelcomeController {

    public void welcome(OakRequest request, OakResponse response) throws IOException {
        try {
            response.sendJson(Map.of(
                    "status", "success",
                "message", "Welcome to Oak Server!"
            ));
        } catch (IllegalArgumentException e) {
            response.sendJson(Map.of(
                    "status", "error",
                    "message", "Internal error!"
            ));
        }
    }
}
