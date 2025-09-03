package com.oak.oak_protocol;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.IOException;

public class OakResponse {
    public String status;
    public String message;
    public Object data;
    BufferedWriter output;

    private static final ObjectMapper mapper = new ObjectMapper();

    public OakResponse(BufferedWriter output) {
        this.output = output;
    }

    public void sendJson(Object data) throws IOException {
        send(mapper.writeValueAsString(data));
    }

    public void send(String data) throws IOException {
        output.write("OAK_PROTOCOL\r\n");
        output.write(data);
        output.write("\r\n");
        output.flush();
    }

}
