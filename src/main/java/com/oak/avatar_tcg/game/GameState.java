package com.oak.avatar_tcg.game;

import com.google.gson.Gson;
import com.oak.avatar_tcg.model.Card;

import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

public class GameState {
    private final String id;
    private final String matchID;
    private String type;
    private String message;
    private Player playerOne;
    private Player playerTwo;
    private String state;
    private String turnPlayerId;
    private String playerWin;

    public GameState(String matchID, String playerOneID, String playerTwoID) {
        this.id = UUID.randomUUID().toString();
        this.matchID = matchID;
        this.type = "UPDATE_GAME";
        this.message = "Game state updated";
        this.state = "IN_PROGRESS";
        this.turnPlayerId = ThreadLocalRandom.current().nextBoolean() ? playerOneID : playerTwoID;
        this.playerWin = "void";

        this.playerOne = new Player(playerOneID);
        this.playerTwo = new Player(playerTwoID);
    }

    public void battle(){
        Card cardPlayerOne = this.playerOne.getActivationCard();
        Card cardPlayerTwo = this.playerTwo.getActivationCard();

        if(this.playerOne.reduceLifeCard(cardPlayerTwo.getAttack())){
            this.playerTwo.addPoint();
        }
        if(this.playerTwo.reduceLifeCard(cardPlayerOne.getAttack())){
            this.playerOne.addPoint();
        }

        this.playerOne.setPlayedCard(false);
        this.playerTwo.setPlayedCard(false);

        if(playerOne.getPoints() >= 3){
            this.state = "FINISHED";
            this.playerWin = playerOne.getId();
        }
        if(playerTwo.getPoints() >= 3){
            this.state = "FINISHED";
            this.playerWin = playerTwo.getId();
        }
    }

    public String getId() { return id; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public void setPlayerWin(String playerWin) { this.playerWin = playerWin; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public Player getPlayerOne() { return playerOne; }

    public Player getPlayerTwo() { return playerTwo; }

    public String getTurnPlayerId() { return turnPlayerId; }
    public void setTurnPlayerId(String turnPlayerId) { this.turnPlayerId = turnPlayerId; }

    public String getPlayerWin() { return playerWin; }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
