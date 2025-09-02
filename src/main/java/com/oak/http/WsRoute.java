package com.oak.http;

import java.util.regex.Pattern;

public class WsRoute {
    final Pattern pattern;
    final WebSocketHandler handler;
    final String[] paramNames;

    WsRoute(Pattern pattern, WebSocketHandler handler, String[] paramNames) {
        this.pattern = pattern;
        this.handler = handler;
        this.paramNames = paramNames;
    }
}
