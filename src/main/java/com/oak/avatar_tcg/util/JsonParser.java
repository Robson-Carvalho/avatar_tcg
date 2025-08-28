package com.oak.avatar_tcg.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class JsonParser {
    private static final Gson gson = new Gson();

    public static Map<String, Object> parseJsonToMap(String json) {
        return gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }
}