package com.oak.avatar_tcg.model;

import java.util.HashMap;
import java.util.Map;

public class MatchManager {
    private static final Map<String, Match> activeMatches = new HashMap<>();

    // MatchRepository

    public static void addMatch(Match match) {
        // pega do banco
        activeMatches.put(match.getId(), match);
    }

    public static Match getMatch(String matchId) {
        return activeMatches.get(matchId);
    }

    public static void removeMatch(String matchId) {
        // salva no banco antes
        activeMatches.remove(matchId);
    }

    public static Map<String, Match> getAllMatches() {
        return activeMatches;
    }
}
