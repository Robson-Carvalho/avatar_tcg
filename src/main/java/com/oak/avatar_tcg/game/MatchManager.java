package com.oak.avatar_tcg.game;

import com.oak.avatar_tcg.service.MatchService;
import com.oak.http.WebSocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatchManager {
    private final ConcurrentHashMap<String, Match> matches = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> playerToMatch = new ConcurrentHashMap<>();

    private final MatchService  matchService =  new MatchService();

    public String createMatch(WebSocket socket1, String player1, WebSocket socket2, String player2) throws Exception {
        Match match = new Match(player1, socket1, player2, socket2);
        matches.put(match.getId(), match);
        playerToMatch.put(player1, match.getId());
        playerToMatch.put(player2, match.getId());
        return match.getId();
    }

    public void handleAction(String action,String cardID, String userID, String matchID) {
        Match match = matches.get(matchID);

        if(match.getGameState().getTurnPlayerId().equals(userID)){
            if(action.equals("activateCard")){
                if (match.getPlayerOneID().equals(userID)) {
                    match.getGameState().getPlayerOne().setActivationCard(cardID);
                }else{
                    match.getGameState().getPlayerTwo().setActivationCard(cardID);
                }
            }
            else if(action.equals("playCard")) {
                if (match.getPlayerOneID().equals(userID)) {
                    match.getGameState().getPlayerOne().setPlayedCard(true);
                }else{
                    match.getGameState().getPlayerTwo().setPlayedCard(true);
                }

                if (match.getGameState().getPlayerOne().getPlayedCard()  && match.getGameState().getPlayerTwo().getPlayedCard()){
                    match.battle();
                }

                if(match.getGameState().getTurnPlayerId().equals(match.getPlayerOneID())) {
                    match.getGameState().setTurnPlayerId(match.getPlayerTwoID());
                }else{
                    match.getGameState().setTurnPlayerId(match.getPlayerOneID());
                }
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

    // partida finalizada por desconexão ou desistência
    public void endMatch(String matchID, String userID) {
        Match match = matches.remove(matchID);

        if (!match.getId().isEmpty()) {
            playerToMatch.remove(match.getPlayerOneID());
            playerToMatch.remove(match.getPlayerTwoID());
        }

        if(match.getPlayerOneID().equals(userID)){
            match.getGameState().setPlayerWin(match.getPlayerTwoID());
        }else{
            match.getGameState().setPlayerWin(match.getPlayerOneID());
        }

        com.oak.avatar_tcg.model.Match newMatch = new com.oak.avatar_tcg.model.Match();

        newMatch.setPlayerOneID(match.getPlayerOneID());
        newMatch.setPlayerTwoID(match.getPlayerTwoID());

        newMatch.setPlayerWin(match.getGameState().getPlayerWin());

        matchService.save(newMatch);

        matches.remove(matchID);
    }

    // partida finalizada por fluxo normal
    public void endMatch(String matchID) {
        Match match = matches.remove(matchID);

        if (!match.getId().isEmpty()) {
            playerToMatch.remove(match.getPlayerOneID());
            playerToMatch.remove(match.getPlayerTwoID());
        }

        com.oak.avatar_tcg.model.Match newMatch = new com.oak.avatar_tcg.model.Match();

        newMatch.setPlayerOneID(match.getPlayerOneID());
        newMatch.setPlayerTwoID(match.getPlayerTwoID());
        newMatch.setPlayerWin(match.getGameState().getPlayerWin());

        matchService.save(newMatch);

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
