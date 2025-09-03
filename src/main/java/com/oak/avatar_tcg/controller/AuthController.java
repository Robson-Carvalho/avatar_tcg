package com.oak.avatar_tcg.controller;

import com.oak.avatar_tcg.model.User;
import com.oak.avatar_tcg.service.AuthService;
import com.oak.avatar_tcg.service.UserService;
import com.oak.oak_protocol.OakRequest;
import com.oak.oak_protocol.OakResponse;

import java.io.IOException;
import java.util.Map;

public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    public AuthController() {
        this.authService = new AuthService();
        this.userService = new UserService();
    }

    public void login(OakRequest request, OakResponse response) throws IOException {
        try {
            String email = request.getData("email");
            String password = request.getData("password");

            if (email == null || password == null) {
                response.sendJson(Map.of(
                        "status", "error",
                        "message", "Email e senha são requeridos"
                ));
                return;
            }

            User user = authService.authenticate(email, password);
            String token = authService.generateToken(user);

            Map<String, Object> userWithoutPassword = user.toJsonWithoutPassword();

            response.sendJson(Map.of(
                    "token", token,
                    "user", userWithoutPassword
            ));

        } catch (Exception e) {
            response.sendJson(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    public void register(OakRequest request, OakResponse response) throws IOException {
        try {
            String name = request.getData("name");
            String nickname = request.getData("nickname");
            String email = request.getData("email");
            String password = request.getData("password");

            User user = new User();
            user.setName(name);
            user.setNickname(nickname);
            user.setEmail(email);
            user.setPassword(password);

            if(user.getEmail() == null || user.getNickname() == null || user.getPassword() == null){
                response.sendJson(Map.of(
                        "status", "error",
                        "message", "Nome, Email, Senha e nickname são precisos!"
                ));
                return;
            }

            User registeredUser = userService.register(user);
            String token = authService.generateToken(registeredUser);

            Map<String, Object> userWithoutPassword = user.toJsonWithoutPassword();

            response.sendJson(Map.of(
                    "token", token,
                    "user", userWithoutPassword
            ));
        } catch (Exception e) {
            response.sendJson(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}