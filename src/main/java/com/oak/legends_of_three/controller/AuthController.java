package com.oak.legends_of_three.controller;

import com.oak.http.HttpRequest;
import com.oak.http.HttpResponse;
import com.oak.legends_of_three.model.User;
import com.oak.legends_of_three.service.AuthService;
import com.oak.legends_of_three.service.UserService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    public AuthController() {
        this.authService = new AuthService();
        this.userService = new UserService();
    }

    public void login(HttpRequest request, HttpResponse response) throws IOException {
        try {
            Map<String, Object> body = request.getJsonBodyAsMap();

            String email = (String) body.get("email");
            String password = (String) body.get("password");

            if (email == null || password == null) {
                response.setStatus(400);
                response.json(Map.of("error", "Email and password are required"));
                return;
            }

            User user = authService.authenticate(email, password);
            String token = authService.generateToken(user);

            Map<String, Object> userWithoutPassword = Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "nickname", user.getNickname(),
                "email", user.getEmail()
            );

            response.json(Map.of(
                    "token", token,
                    "user", userWithoutPassword
            ));

        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            response.setStatus(401);
            response.json(Map.of("error", "Invalid email or password"));
        }
    }

    public void register(HttpRequest request, HttpResponse response) throws IOException {
        try {
            Map<String, Object> body = request.getJsonBodyAsMap();

            User user = new User();
            user.setName((String) body.get("name"));
            user.setNickname((String) body.get("nickname"));
            user.setEmail((String) body.get("email"));
            user.setPassword((String) body.get("password"));

            if(user.getEmail() == null || user.getNickname() == null || user.getPassword() == null){
                response.setStatus(400);
                response.json(Map.of("error", "Name, Email, Password and nickname are required"));
            }

            User registeredUser = userService.register(user);
            String token = authService.generateToken(registeredUser);

            Map<String, Object> userWithoutPassword = Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "nickname", user.getNickname(),
                "email", user.getEmail()
            );

            response.json(Map.of(
                    "token", token,
                    "user", userWithoutPassword
            ));

        } catch (SQLException e) {
            response.setStatus(500);
            response.json(Map.of("error", "Database error"));
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.json(Map.of("error", e.getMessage()));
        }
    }
}