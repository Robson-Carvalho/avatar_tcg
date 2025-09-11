package com.oak.avatar_tcg.controller;

import com.oak.http.HttpRequest;
import com.oak.http.HttpResponse;
import com.oak.avatar_tcg.model.User;
import com.oak.avatar_tcg.service.AuthService;
import com.oak.avatar_tcg.service.UserService;
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
                response.json(Map.of("error", "Email e senha são requeridos"));
                return;
            }

            User user = authService.authenticate(email, password);
            String token = authService.generateToken(user);

            Map<String, Object> userWithoutPassword = user.toJsonWithoutPassword();

            response.json(Map.of(
                    "token", token,
                    "user", userWithoutPassword
            ));
            System.out.println("User logged in");
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            response.setStatus(400);
            response.json(Map.of("error", "E-mail ou senha inválidos"));
        }
    }

    public void delete(HttpRequest request, HttpResponse response) throws IOException {
        try {
            Map<String, Object> body = request.getJsonBodyAsMap();

            String id = (String) body.get("id");

            if (id == null) {
                response.setStatus(400);
                response.json(Map.of("error", "ID é requerido"));
                return;
            }

            authService.delete(id);

            response.json(Map.of(
                    "message", "Player apagado com sucesso"
            ));
            System.out.println("User deleted");
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            response.setStatus(400);
            response.json(Map.of("error", "ID inválido"));
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
                return;
            }

            User registeredUser = userService.register(user);
            String token = authService.generateToken(registeredUser);

            Map<String, Object> userWithoutPassword = user.toJsonWithoutPassword();

            response.json(Map.of(
                    "token", token,
                    "user", userWithoutPassword
            ));

            System.out.println("User registered");
        } catch (SQLException e) {
            response.setStatus(500);
            response.json(Map.of("error", "Database error"));
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}