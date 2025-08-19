package com.oak.legends_of_three.controller;

import com.oak.http.WebSocket;
import com.oak.http.WebSocketHandler;

public class WebSocketController {

    public WebSocketHandler websocket() {
        return new WebSocketHandler(){
            @Override
            public void onOpen(WebSocket webSocket) {

            }

            @Override
            public void onMessage(WebSocket webSocket, String message) {

            }

            @Override
            public void onClose(WebSocket webSocket) {

            }
        };

    }
}
