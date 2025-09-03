package com.oak.oak_protocol.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JSON {
    private static final Gson gson = new Gson();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            System.out.println("Erro ao converter JSON: " + e.getMessage());
            return null;
        }
    }
}
