package com.oak.oak_protocol;

import java.io.IOException;

@FunctionalInterface
public interface OakHandler {
    void handle(OakRequest request, OakResponse response) throws IOException;
}