package com.oak.http;

import java.util.regex.Pattern;

class WsRoute {
    final Pattern pattern;
    final WebSocketHandler handler;

    WsRoute(Pattern pattern, WebSocketHandler handler) {
        this.pattern = pattern;
        this.handler = handler;
    }
}