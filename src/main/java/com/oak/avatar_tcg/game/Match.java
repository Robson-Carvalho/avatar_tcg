package com.oak.avatar_tcg.game;

import com.oak.avatar_tcg.model.Card;
import com.oak.http.WebSocket;

import java.util.List;
import java.util.UUID;

public class Match {
    private String id;
    private String PlayerOneID;
    private String PlayerTwoID;
    private WebSocket SocketPlayerOne;
    private WebSocket SocketPlayerTwo;
    private List<Card> playerOneCards;
    private List<Card> playerTwoCards;

    public Match(String PlayerOneID, WebSocket SocketPlayerOne, String PlayerTwoID, WebSocket SocketPlayerTwo) {
        this.id = UUID.randomUUID().toString();
        this.PlayerOneID = PlayerOneID;
        this.PlayerTwoID = PlayerTwoID;
        this.SocketPlayerOne = SocketPlayerOne;
        this.SocketPlayerTwo = SocketPlayerTwo;
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

    public void battle(){
        // resolver jogo
    }

    public void getStateMatch(){
        // retornar JSON do estado atual
    }
}
