package com.oak.avatar_tcg.game;

import com.google.gson.Gson;

import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

public class GameState {
    private final String id;
    private String type;
    private String message;
    private Player playerOne;
    private Player playerTwo;
    private String state;
    private String turnPlayerId;
    private String playerWin;

    public GameState(String matchID, String playerOneID, String playerTwoID) throws Exception {
        this.id = matchID != null ? matchID : UUID.randomUUID().toString();
        this.type = "GAME_UPDATE";
        this.message = "Game state updated";
        this.state = "IN_PROGRESS";
        this.turnPlayerId = ThreadLocalRandom.current().nextBoolean() ? playerOneID : playerTwoID;
        this.playerWin = "void";

        this.playerOne = new Player(playerOneID);
        this.playerTwo = new Player(playerTwoID);
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Player getPlayerOne() { return playerOne; }
    public void setPlayerOne(Player playerOne) { this.playerOne = playerOne; }

    public Player getPlayerTwo() { return playerTwo; }
    public void setPlayerTwo(Player playerTwo) { this.playerTwo = playerTwo; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getTurnPlayerId() { return turnPlayerId; }
    public void setTurnPlayerId(String turnPlayerId) { this.turnPlayerId = turnPlayerId; }

    public String getPlayerWin() { return playerWin; }

    public void setPlayerWin(String playerWin) { this.playerWin = playerWin; }

    public void battle(){

    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
