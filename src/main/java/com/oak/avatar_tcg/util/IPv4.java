package com.oak.avatar_tcg.util;

public class IPv4 {

    public static String getLocalIPv4() {
        return System.getenv("HOST_SERVER");
    }
}