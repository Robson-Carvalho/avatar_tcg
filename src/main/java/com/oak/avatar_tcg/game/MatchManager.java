package com.oak.avatar_tcg.game;

import com.oak.http.WebSocket;
import java.util.concurrent.ConcurrentHashMap;

public class MatchManager {
    private final ConcurrentHashMap<String, Match> matches = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> playerToMatch = new ConcurrentHashMap<>();

    public String createIfNotPlaying(WebSocket socket1, String player1, WebSocket socket2, String player2) {
        return playerToMatch.compute(player1, (key, existingMatchId) -> {
            if (existingMatchId != null) {
                throw new IllegalStateException("Player " + player1 + " already in match");
            }
            if (playerToMatch.containsKey(player2)) {
                throw new IllegalStateException("Player " + player2 + " already in match");
            }

            Match match = new Match(player1, socket1, player2, socket2);
            matches.put(match.getId(), match);
            playerToMatch.put(player2, match.getId());
            return match.getId();
        });
    }

    public boolean isPlayerInMatch(String playerID, String matchID) {
        String playerMatchId = playerToMatch.get(playerID);
        return matchID.equals(playerMatchId); // CORRIGIDO: removi a negação
    }

    public boolean isPlayerPlaying(String playerID) {
        return playerToMatch.containsKey(playerID);
    }

    public WebSocket getOpponentInMatch(String playerID, String matchID) {
        Match match = matches.get(matchID);

        if (match != null && match.getPlayerOneID().equals(playerID)) {
            return match.getSocketPlayerTwo();
        }
        if (match != null && match.getPlayerTwoID().equals(playerID)) {
            return match.getSocketPlayerOne();
        }

        return null;
    }

    public Match getMatch(String matchID) {
        return matches.get(matchID);
    }

    public void endMatch(String matchID) {
        Match match = matches.remove(matchID);
        if (match != null) {
            playerToMatch.remove(match.getPlayerOneID());
            playerToMatch.remove(match.getPlayerTwoID());
        }
    }

    public void getStateMatch(){

    }
}