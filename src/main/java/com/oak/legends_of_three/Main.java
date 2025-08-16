package com.oak.legends_of_three;

import com.oak.http.HttpServer;
import com.oak.legends_of_three.database.Migrations;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        Migrations.runMigrations();

        HttpServer server = new HttpServer(8080);

        server.get("/", (req, res) -> {
            res.send("Hello World");
        });

        server.start();
    }
}