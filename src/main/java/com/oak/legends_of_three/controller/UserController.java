package com.oak.legends_of_three.controller;

import com.oak.http.HttpRequest;
import com.oak.http.HttpResponse;
import com.oak.legends_of_three.model.User;
import com.oak.legends_of_three.service.UserService;
import java.io.IOException;
import java.sql.SQLException;

public class UserController {
    private final UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    public void getUserByEmail(HttpRequest request, HttpResponse response) throws IOException {
        try {
            String email = request.getHeader("email");
            if (email == null || email.isEmpty()) {
                response.setStatus(400);
                response.send("{\"error\":\"Email is required\"}");
                return;
            }

            User user = userService.findByEmail(email);
            if (user == null) {
                response.setStatus(404);
                response.send("{\"error\":\"User not found\"}");
                return;
            }

            response.setHeader("Content-Type", "application/json");
            response.send(user.toJson());
        } catch (SQLException e) {
            response.setStatus(500);
            response.send("{\"error\":\"Database error\"}");
            e.printStackTrace();
        }
    }

    public void getUserByNickname(HttpRequest request, HttpResponse response) throws IOException {
        try {
            String nickname = request.getHeader("nickname");
            if (nickname == null || nickname.isEmpty()) {
                response.setStatus(400);
                response.send("{\"error\":\"Nickname is required\"}");
                return;
            }

            User user = userService.findByNickname(nickname);
            if (user == null) {
                response.setStatus(404);
                response.send("{\"error\":\"User not found\"}");
                return;
            }

            response.setHeader("Content-Type", "application/json");
            response.send(user.toJson());
        } catch (SQLException e) {
            response.setStatus(500);
            response.send("{\"error\":\"Database error\"}");
            e.printStackTrace();
        }
    }
}