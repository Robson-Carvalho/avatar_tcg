package com.oak.legends_of_three.model;

import java.util.UUID;

public class Match {
    private String id;
    private String player1ID;
    private String player2ID;
    private Boolean visible;

    public Match(){
        this.id = UUID.randomUUID().toString();
    }

    public Match(String player1ID, String player2ID, Boolean visible){
        this.player1ID = player1ID;
        this.player2ID = player2ID;
        this.visible = visible;
    }

    public String getId() {
        return this.id;
    }
}
