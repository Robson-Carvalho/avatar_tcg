package com.oak.avatar_tcg.game;

import com.oak.http.WebSocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatchManager {
    private final ConcurrentHashMap<String, Match> matches = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> playerToMatch = new ConcurrentHashMap<>();

    public String createMatch(WebSocket socket1, String player1, WebSocket socket2, String player2) throws Exception {
        Match match = new Match(player1, socket1, player2, socket2);
        matches.put(match.getId(), match);
        playerToMatch.put(player1, match.getId());
        playerToMatch.put(player2, match.getId());
        return match.getId();
    }

    public void handleAction(String action,String cardID, String userID, String matchID) {
        Match match = matches.get(matchID);

        if(action.equals("activateCard")){

            if (match.getPlayerOneID().equals(userID)) {
                match.getGameState().getPlayerOne().setActivationCard(cardID);
            }else{
                match.getGameState().getPlayerTwo().setActivationCard(cardID);
            }
        } else if( action.equals("play")) {
            if (match.getPlayerOneID().equals(userID)) {
                match.getGameState().getPlayerOne().setPlayedCard(true);
            }else{
                match.getGameState().getPlayerTwo().setPlayedCard(true);
            }

            if (match.getGameState().getPlayerOne().getPlayedCard()  && match.getGameState().getPlayerTwo().getPlayedCard()){
                match.battle();
            }
        }
    }

    public String getStateMatch(String matchId) {
        return matches.get(matchId).getGameStateJson();
    }

    public WebSocket getOpponentInMatch(String playerID, String matchID) {
        Match match = matches.get(matchID);

        if (match == null) return null;

        if (match.getPlayerOneID().equals(playerID)) return match.getSocketPlayerTwo();
        if (match.getPlayerTwoID().equals(playerID)) return match.getSocketPlayerOne();

        return null;
    }

    public Match getMatch(String matchID) {
        return matches.get(matchID);
    }

    public void endMatch(String matchID) {
        Match match = matches.remove(matchID);

        if (!match.getId().isEmpty()) {
            playerToMatch.remove(match.getPlayerOneID());
            playerToMatch.remove(match.getPlayerTwoID());
        }

        // adicionar método para salvar a partida aqui - repository match

        matches.remove(matchID);
    }

    public String getUserIDBySocket(WebSocket socket) {
        for (Match match : matches.values()) {
            if (match.getSocketPlayerOne() == socket) return match.getPlayerOneID();
            if (match.getSocketPlayerTwo() == socket) return match.getPlayerTwoID();
        }
        return null;
    }

    public String getMatchIDBySocket(WebSocket socket) {
        for (Map.Entry<String, Match> entry : matches.entrySet()) {
            Match match = entry.getValue();
            if (match.getSocketPlayerOne() == socket || match.getSocketPlayerTwo() == socket) {
                return entry.getKey();
            }
        }
        return null;
    }
}
