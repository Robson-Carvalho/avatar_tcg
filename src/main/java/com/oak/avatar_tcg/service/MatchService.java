package com.oak.avatar_tcg.service;

import com.oak.avatar_tcg.model.Match;
import com.oak.avatar_tcg.repository.MatchRepository;
import java.util.ArrayList;
import java.util.List;

public class MatchService {
    private final MatchRepository matchRepository;

    public MatchService() {
        this.matchRepository = new MatchRepository();
    }

    public void save(Match match) throws IllegalArgumentException {
        matchRepository.save(match);
    }

    public List<Match> findAllByUserID(String id) {
        synchronized (matchRepository) {
            List<Match> matches = new ArrayList<>();

            for (Match m : matchRepository.findAll()) {
                if (m.getPlayerOneID().equals(id) || m.getPlayerTwoID().equals(id)) {
                    matches.add(m);
                }
            }

            return matches;
        }
    }
}