package com.oak.legends_of_three.model;

import java.util.HashMap;
import java.util.Map;

public class MatchManager {
    private static final Map<String, Match> activeMatches = new HashMap<>();

    public static void addMatch(Match match) {
        activeMatches.put(match.getId(), match);
    }

    public static Match getMatch(String matchId) {
        return activeMatches.get(matchId);
    }

    public static void removeMatch(String matchId) {
        activeMatches.remove(matchId);
    }

    public static Map<String, Match> getAllMatches() {
        return activeMatches;
    }
}
