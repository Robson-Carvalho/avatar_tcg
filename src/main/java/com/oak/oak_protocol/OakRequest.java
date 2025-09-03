package com.oak.oak_protocol;

import com.oak.oak_protocol.util.OakData;

public class OakRequest {
    private String command;
    private String path;
    private final OakData data;

    public OakRequest(String command, String path, OakData data) {
        this.command = command;
        this.path = path;
        this.data = data;
    }

    public String getData(String key) {
        return data.getData(key);
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}