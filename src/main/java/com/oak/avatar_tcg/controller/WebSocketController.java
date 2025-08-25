package com.oak.avatar_tcg.controller;

import com.oak.avatar_tcg.game.Match;
import com.oak.avatar_tcg.game.MatchManager;
import com.oak.avatar_tcg.service.AuthService;
import com.oak.http.WebSocket;
import com.oak.http.WebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WebSocketController {
    private final ConcurrentLinkedQueue<WebSocket> waitingQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<WebSocket, String> playersInQueue = new ConcurrentHashMap<>();

    final MatchManager matchManager = new MatchManager();
    final AuthService authService = new AuthService();

    public WebSocketHandler websocket() {

        return new WebSocketHandler() {

            private void handleJoin(WebSocket socket, String userID) {
                try {
                    WebSocket opponent = waitingQueue.poll();

                    if (opponent != null) {
                        String opponentID = playersInQueue.get(opponent);

                        String matchID = matchManager.createIfNotPlaying(socket, userID, opponent, opponentID);

                        playersInQueue.remove(socket);
                        playersInQueue.remove(opponent);
                        waitingQueue.remove(socket);

                        System.out.println("Match created: " + matchID);

                        sendMessage(socket, "MATCH_CREATED:" + matchID);
                        sendMessage(opponent, "MATCH_CREATED:" + matchID);

                        // matchManager.getStateMatch(userID) - função que envia estado e deck do player
                        // matchManager.getStateMatch(opponentID) - função que envia estado e deck do player
                    } else {
                        waitingQueue.add(socket);
                        playersInQueue.put(socket, userID);
                        sendMessage(socket, "IN_QUEUE:Waiting for opponent");
                    }
                } catch (IllegalStateException e) {
                    sendMessage(socket, "ERROR:Player already in a match");
                } catch (Exception e) {
                    sendMessage(socket, "ERROR:Failed to join match");
                }
            }

            private void handleActivateCard(WebSocket socket, String userID, String matchID, String cardID) {
                try {
                    if (!matchManager.isPlayerInMatch(userID, matchID)) { // Corrigido aqui
                        sendMessage(socket, "ERROR:Player not in this match");
                        return;
                    }

                    // Lógica de ativação de carta
                    // matchManager.activateCard(matchID, userID, cardID);

                    sendMessage(socket, "CARD_ACTIVATED:" + cardID);
                    broadcastToMatchExceptSender(matchID, socket, "OPPONENT_ACTIVATED_CARD:" + cardID);

                } catch (Exception e) {
                    sendMessage(socket, "ERROR:Failed to activate card");
                }
            }

            private void handlePlay(WebSocket socket, String userID, String matchID, String contentText) {
                try {
                    if (!matchManager.isPlayerInMatch(userID, matchID)) { // Corrigido aqui
                        sendMessage(socket, "ERROR:Player not in this match");
                        return;
                    }

                    // Lógica de jogada
                    // matchManager.makePlay(matchID, userID, contentText);

                    sendMessage(socket, "PLAY_ACCEPTED:" + contentText);
                    broadcastToMatchExceptSender(matchID, socket, "OPPONENT_PLAYED:" + contentText);

                } catch (Exception e) {
                    sendMessage(socket, "ERROR:Failed to process play");
                }
            }

            private void handleClose(WebSocket socket, String userID, String matchID) throws IOException {
                try {
                    if (matchID != null && !matchID.isEmpty()) {
                        WebSocket socketOpponent = matchManager.getOpponentInMatch(userID, matchID);

                        if(socketOpponent != null){
                            // Avisa ao oponente que ganhou por desconexão
                            sendMessage(socketOpponent, "VICTORY:Opponent disconnected");

                            // Fecha apenas o socket do oponente (o atual já está fechando)
                            socketOpponent.close();
                        }

                        // Finaliza a partida
                        matchManager.endMatch(matchID);
                    }

                    System.out.println("Player " + userID + " exited gracefully");
                } catch (Exception e) {
                    System.out.println("Error during player exit: " + e.getMessage());
                } finally {
                    socket.close();
                }
            }

            private void handleUnknownType(WebSocket socket) throws IOException {
                sendMessage(socket, "ERROR:Unknown command type");
            }

            @Override
            public void onOpen(WebSocket socket) throws IOException {
                String clientInfo = "connected: " + socket.getSocket().getRemoteSocketAddress();
                System.out.println("Player connected: " + clientInfo);
                sendMessage(socket, "CONNECTED:Welcome to Avatar TCG");
            }

            @Override
            public void onMessage(WebSocket socket, Map<String, String> message) throws IOException {
                try {
                    String type = message.get("type");
                    String token = message.get("token");
                    String userID = message.get("userID");
                    String matchID = message.get("matchID");
                    String contentText = message.get("contentText");
                    String cardID = message.get("cardID");

                    if (type == null) {
                        handleUnknownType(socket);
                        return;
                    }

                    switch (type) {
                        case "joinQueue" -> {
                            try {
                                String authenticatedUserID = authService.validateToken(token);
                                handleJoin(socket, authenticatedUserID);
                            } catch (Exception e) {
                                sendMessage(socket, "ERROR:Invalid token");
                                socket.close();
                            }
                        }
                        case "activateCard" -> {
                            if (validateGameAction(socket, token, userID, matchID)) {
                                handleActivateCard(socket, userID, matchID, cardID);
                            }
                        }
                        case "play" -> {
                            if (validateGameAction(socket, token, userID, matchID)) {
                                handlePlay(socket, userID, matchID, contentText);
                            }
                        }
                        case "exit" -> handleClose(socket, userID, matchID);
                        default -> handleUnknownType(socket);
                    }
                } catch (Exception e) {
                    System.out.println("Error processing message: " + e.getMessage());
                    sendMessage(socket, "ERROR:Internal server error");
                }
            }

            @Override
            public void onClose(WebSocket socket) {
                String clientInfo = "closed: " + socket.getSocket().getRemoteSocketAddress();

                playersInQueue.remove(socket);
                waitingQueue.remove(socket);

                System.out.println("Player disconnected: " + clientInfo);
            }

            private boolean validateGameAction(WebSocket socket, String token, String userID, String matchID) {
                try {
                    String authenticatedUserID = authService.validateToken(token);

                    if (!authenticatedUserID.equals(userID)) {
                        sendMessage(socket, "ERROR:User ID mismatch");
                        return false;
                    }

                    if (matchID == null || matchID.isEmpty()) {
                        sendMessage(socket, "ERROR:Match ID required");
                        return false;
                    }

                    return true;
                } catch (Exception e) {
                    sendMessage(socket, "ERROR:Invalid authentication");
                    return false;
                }
            }
        };
    }

    private void sendMessage(WebSocket socket, String message) {
        try {
            if (socket != null && socket.isOpen()) {
                socket.send(message);
            }
        } catch (Exception e) {
            System.out.println("Error sending message to " + socket.getSocket().getRemoteSocketAddress() + ": " + e.getMessage());
        }
    }

    private void broadcastToMatch(String matchID, String message) {
        try {
            Match match = matchManager.getMatch(matchID);

            if (match != null) {
                sendMessage(match.getSocketPlayerOne(), message);
                sendMessage(match.getSocketPlayerTwo(), message);
            }
        } catch (Exception e) {
            System.out.println("Error broadcasting to match " + matchID + ": " + e.getMessage());
        }
    }

    private void broadcastToMatchExceptSender(String matchID, WebSocket sender, String message) {
        try {
            Match match = matchManager.getMatch(matchID);
            if (match != null) {
                if (!match.getSocketPlayerOne().equals(sender)) {
                    sendMessage(match.getSocketPlayerOne(), message);
                }
                if (!match.getSocketPlayerTwo().equals(sender)) {
                    sendMessage(match.getSocketPlayerTwo(), message);
                }
            }
        } catch (Exception e) {
            System.out.println("Error broadcasting to match " + matchID + ": " + e.getMessage());
        }
    }
}