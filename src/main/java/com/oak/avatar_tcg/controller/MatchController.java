package com.oak.avatar_tcg.controller;

import com.oak.avatar_tcg.model.Match;
import com.oak.avatar_tcg.service.AuthService;
import com.oak.avatar_tcg.service.MatchService;
import com.oak.http.HttpRequest;
import com.oak.http.HttpResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MatchController {
    private final MatchService matchService;
    private final AuthService authService;

    public MatchController() {
        this.authService = new AuthService();
        this.matchService = new MatchService();
    }

    public void getMatchs(HttpRequest request, HttpResponse response) throws IOException {
        try {
            String token = request.getBearerToken();

            String user_id = authService.validateToken(token);

            List<Match> matchs = matchService.findAllByUserID(user_id);

            response.json(Map.of(
                    "matchs", matchs
            ));

        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            response.setStatus(403);
            response.json(Map.of("error", e.getMessage()));
        }
    }
}
