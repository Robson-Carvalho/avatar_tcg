package com.oak.legends_of_three.controller;

import com.oak.http.HttpRequest;
import com.oak.http.HttpResponse;
import com.oak.legends_of_three.model.User;
import com.oak.legends_of_three.service.AuthService;
import com.oak.legends_of_three.util.JsonParser;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class AuthController {
    private final AuthService authService;

    public AuthController() {
        this.authService = new AuthService();
    }

    public void login(HttpRequest request, HttpResponse response) throws IOException {
        try {
            Map<String, String> body = JsonParser.parseSimpleJson(request.body());
            String email = body.get("email");
            String password = body.get("password");

            if (email == null || password == null) {
                response.setStatus(400);
                response.send("{\"error\":\"Email and password are required\"}");
                return;
            }

            User user = authService.authenticate(email, password);
            String token = authService.generateToken(user);

            response.setHeader("Content-Type", "application/json");
            response.send(String.format(
                    "{\"token\":\"%s\",\"user\":%s}",
                    token, user.toJson()
            ));
        } catch (Exception e) {
            response.setStatus(401);
            response.send(String.format("{\"error\":\"%s\"}", e.getMessage()));
        }
    }

    public void register(HttpRequest request, HttpResponse response) throws IOException {
        try {
            Map<String, String> body = JsonParser.parseSimpleJson(request.body());
            String name = body.get("name");
            String nickname = body.get("nickname");
            String email = body.get("email");
            String password = body.get("password");

            if (name == null || nickname == null || email == null || password == null) {
                response.setStatus(400);
                response.send("{\"error\":\"All fields are required\"}");
                return;
            }

            User user = new User();
            user.setName(name);
            user.setNickname(nickname);
            user.setEmail(email);
            user.setPassword(password);

            User registeredUser = authService.getUserService().register(user);
            String token = authService.generateToken(registeredUser);

            response.setHeader("Content-Type", "application/json");
            response.send(String.format(
                    "{\"token\":\"%s\",\"user\":%s}",
                    token, registeredUser.toJson()
            ));
        } catch (SQLException e) {
            response.setStatus(500);
            response.send("{\"error\":\"Database error\"}");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.send(String.format("{\"error\":\"%s\"}", e.getMessage()));
        }
    }
}