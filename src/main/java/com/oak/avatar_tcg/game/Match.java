package com.oak.avatar_tcg.game;

import com.oak.http.websocket.WebSocket;

import java.util.UUID;

public class Match {
    private final String id;
    private final String PlayerOneID;
    private final String PlayerTwoID;
    private final WebSocket SocketPlayerOne;
    private final WebSocket SocketPlayerTwo;
    private final GameState gameState;

    public Match(String PlayerOneID, WebSocket SocketPlayerOne, String PlayerTwoID, WebSocket SocketPlayerTwo) throws Exception {
        this.id = UUID.randomUUID().toString();
        this.PlayerOneID = PlayerOneID;
        this.PlayerTwoID = PlayerTwoID;
        this.SocketPlayerOne = SocketPlayerOne;
        this.SocketPlayerTwo = SocketPlayerTwo;

        this.gameState = new GameState(this.id, this.PlayerOneID, this.PlayerTwoID);
    }

    public void battle(){
        this.gameState.battle();
    }

    public String getGameStateJson() {
        return this.gameState.toJson();
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public String getId() {
        return this.id;
    }

    public String getPlayerOneID() {
        return this.PlayerOneID;
    }

    public String getPlayerTwoID() {
        return this.PlayerTwoID;
    }

    public WebSocket getSocketPlayerOne() {
        return this.SocketPlayerOne;
    }

    public WebSocket getSocketPlayerTwo() {
        return this.SocketPlayerTwo;
    }
}
