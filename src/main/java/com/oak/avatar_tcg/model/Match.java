package com.oak.avatar_tcg.model;

import java.util.UUID;

public class Match {
    private String id;
    private String playerOneID;
    private String playerTwoID;
    private String playerWin;


    public Match() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {return this.id;}
    public String getPlayerOneID() {return this.playerOneID;}
    public String getPlayerTwoID() {return this.playerTwoID;}
    public String getPlayerWin() {return this.playerWin;}

    public void setId(String id) {this.id = id;}
    public void setPlayerOneID(String playerOneID) {this.playerOneID = playerOneID;}
    public void setPlayerTwoID(String playerTwoID) {this.playerTwoID = playerTwoID;}
    public void setPlayerWin(String playerWin) {this.playerWin = playerWin;}
}
