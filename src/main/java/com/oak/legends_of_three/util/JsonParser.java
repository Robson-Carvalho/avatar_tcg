package com.oak.legends_of_three.util;

import java.util.HashMap;
import java.util.Map;

public class JsonParser {
    public static Map<String, String> parseSimpleJson(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.isEmpty()) {
            return map;
        }

        // Remove curly braces and whitespace
        json = json.replaceAll("[{}\"]", "").trim();

        // Split into key-value pairs
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                map.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return map;
    }
}