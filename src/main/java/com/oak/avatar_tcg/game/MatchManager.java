package com.oak.avatar_tcg.game;

import com.oak.http.WebSocket;
import java.util.concurrent.ConcurrentHashMap;

public class MatchManager {
    private final ConcurrentHashMap<String, Match> matches = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> playerToMatch = new ConcurrentHashMap<>();

    public String createMatch(WebSocket socket1, String player1, WebSocket socket2, String player2) {
        Match match = new Match(player1, socket1, player2, socket2);
        matches.put(match.getId(), match);
        playerToMatch.put(player1, match.getId());
        playerToMatch.put(player2, match.getId());
        return match.getId();
    }


    public boolean isPlayerPlaying(String playerID) {
        return playerToMatch.containsKey(playerID);
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
        if (match != null) {
            playerToMatch.remove(match.getPlayerOneID());
            playerToMatch.remove(match.getPlayerTwoID());
        }
    }


    public void getStateMatch() {
        // Implementação futura
    }
}
