package com.oak.oak_protocol.util;

import java.util.Objects;

public class OakData {
    public String connection;
    public String type;
    public String name;
    public String nickname;
    public String email;
    public String password;
    public String token;
    public String deckID;
    public String userId;
    public String card1Id;
    public String card2Id;
    public String card3Id;
    public String card4Id;
    public String card5Id;

    public String getConnection() {
        return connection;
    }

    public String getType() {
        return type;
    }

    public String getData(String key){
        if (Objects.equals(key, "connection")){
            return connection;
        }
        else if(Objects.equals(key, "token")){
            return token;
        }
        else if (Objects.equals(key, "name")){
            return name;
        }
        else if (Objects.equals(key, "nickname")){
            return nickname;
        }
        else if (Objects.equals(key, "type")){
            return type;
        }
        else if (Objects.equals(key, "email")){
            return email;
        }
        else if (Objects.equals(key, "password")){
            return password;
        }
        else if (Objects.equals(key, "deckID")){
            return deckID;
        }
        else if (Objects.equals(key, "userID")){
            return userId;
        }
        else if (Objects.equals(key, "card1Id")){
            return card1Id;
        }
        else if (Objects.equals(key, "card2Id")){
            return card2Id;
        }
        else if (Objects.equals(key, "card3Id")){
            return card3Id;
        }
        else if (Objects.equals(key, "card4Id")){
            return card4Id;
        }
        else if (Objects.equals(key, "card5Id")){
            return card5Id;
        }

        return null;
    }
}