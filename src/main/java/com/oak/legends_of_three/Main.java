package com.oak.legends_of_three;

import com.oak.http.HttpServer;
import com.oak.legends_of_three.controller.AuthController;
import com.oak.legends_of_three.controller.UserController;
import com.oak.legends_of_three.database.Migrations;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        Migrations.runMigrations();

        HttpServer server = new HttpServer(8080);

        UserController userController = new UserController();
        AuthController authController = new AuthController();

        server.get("/", (req, res) -> {
            res.setHeader("Content-Type", "application/json");
            res.send("{\"message\":\"Welcome to Legends of Three\"}");
        });

        // User routes
        server.get("/users/email", userController::getUserByEmail);
        server.get("/users/nickname", userController::getUserByNickname);

        // Auth routes
        server.post("/auth/login", authController::login);
        server.post("/auth/register", authController::register);

        server.start();
    }
}