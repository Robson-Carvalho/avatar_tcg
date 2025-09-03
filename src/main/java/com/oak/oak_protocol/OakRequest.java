package com.oak.oak_protocol;

import com.oak.oak_protocol.util.OakData;

import java.io.BufferedReader;
import java.io.BufferedWriter;

public class OakRequest {
    private String command;
    private String path;
    private final OakData data;
    BufferedReader input;

    public OakRequest(String command, String path, OakData data, BufferedReader input) {
        this.command = command;
        this.path = path;
        this.data = data;
        this.input = input;
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