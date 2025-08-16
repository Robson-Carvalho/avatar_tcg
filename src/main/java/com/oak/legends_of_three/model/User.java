package com.oak.legends_of_three.model;

import java.util.UUID;

public class User {
    private String id;
    private String name;
    private String nickname;
    private String email;
    private String password;

    public User() {
        this.id = UUID.randomUUID().toString();
    }

    public User(String name, String nickname, String email, String password) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

    public  String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String toJson() {
        return String.format(
                "{\"id\":\"%s\",\"name\":\"%s\",\"nickname\":\"%s\",\"email\":\"%s\"}",
                id, name, nickname, email
        );
    }
}