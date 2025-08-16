package com.oak.legends_of_three;

import com.oak.http.HttpServer;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(8080);

        server.get("/", (req, res) -> {
            res.send("Hello World");
        });

        server.start();
    }
}