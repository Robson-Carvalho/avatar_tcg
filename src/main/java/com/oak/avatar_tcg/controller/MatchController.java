package com.oak.avatar_tcg.controller;

import com.oak.avatar_tcg.model.Match;
import com.oak.avatar_tcg.service.AuthService;
import com.oak.avatar_tcg.service.MatchService;
import com.oak.oak_protocol.OakRequest;
import com.oak.oak_protocol.OakResponse;

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

    public void getMatchs(OakRequest request, OakResponse response) throws IOException {
        try {
            String token = request.getData("token");

            String user_id = authService.validateToken(token);

            List<Match> matchs = matchService.findAllByUserID(user_id);

            response.sendJson(Map.of(
                    "status", "success",
                    "matchs", matchs
            ));
        } catch (Exception e) {
            response.sendJson(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}
