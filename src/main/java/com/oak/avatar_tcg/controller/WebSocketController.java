package com.oak.avatar_tcg.controller;

import com.oak.avatar_tcg.game.*;
import com.oak.avatar_tcg.service.AuthService;
import com.oak.avatar_tcg.service.DeckService;
import com.oak.avatar_tcg.util.JsonParser;
import com.oak.http.websocket.WebSocket;
import com.oak.http.interfaces.WebSocketHandler;

import java.io.IOException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WebSocketController {
    private final ConcurrentLinkedQueue<String> waitingQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<String, WebSocket> playerSockets = new ConcurrentHashMap<>();
    private final MatchManager matchManager = new MatchManager();

    private final AuthService authService = new AuthService();

    private synchronized void handleJoin(WebSocket socket, String userID) throws IOException {
        try {
            if (playerSockets.containsKey(userID)) {
                WebSocket existingSocket = playerSockets.get(userID);

                if (existingSocket != null && existingSocket.isOpen()) {
                    socket.send(JsonParser.toJson(new GameMessage("ERROR", "Você já está conectado em outra sessão")));
                    socket.close();
                    return;
                } else {
                    playerSockets.remove(userID);
                    waitingQueue.remove(userID);
                }
            }

            playerSockets.put(userID, socket);

            String opponentId = null;
            while (!waitingQueue.isEmpty()) {
                String firstId = waitingQueue.poll();
                WebSocket opponentSocket = playerSockets.get(firstId);

                if (opponentSocket != null && opponentSocket.isOpen()) {
                    opponentId = firstId;
                    break;
                } else {
                    playerSockets.remove(firstId);
                }
            }

            if (opponentId == null) {
                waitingQueue.add(userID);
                socket.send(JsonParser.toJson(new GameMessage("IN_QUEUE", "Aguardando oponente")));
                return;
            }

            WebSocket opponentSocket = playerSockets.get(opponentId);
            String matchID = matchManager.createMatch(socket, userID, opponentSocket, opponentId);

            playerSockets.remove(userID);
            playerSockets.remove(opponentId);

            System.out.println("Partida criada: " + matchID + " entre " + userID + " e " + opponentId);

            socket.send(JsonParser.toJson(new GameStateMessage("MATCH_FOUND", matchID, matchManager.getStateMatch(matchID))));
            opponentSocket.send(JsonParser.toJson(new GameStateMessage("MATCH_FOUND", matchID, matchManager.getStateMatch(matchID))));
        } catch (Exception e) {
            System.out.println("Erro ao processar join: " + e.getMessage());
            socket.send(JsonParser.toJson(new GameMessage("ERROR", "Falha ao entrar na partida")));
        }
    }

    private synchronized void handleClose(WebSocket socket, String userID, String matchID, boolean initiatedByClient) {
        try {
            if (matchID == null) {
                socket.send(JsonParser.toJson( new GameMessage("ERROR", "ID da partida é requerido")));
                return;
            }

            if (!matchID.isEmpty()) {
                WebSocket socketOpponent = matchManager.getOpponentInMatch(userID, matchID);

                if (socketOpponent != null && socketOpponent.isOpen()) {
                    socketOpponent.send(JsonParser.toJson(new GameMessage("VICTORY_WITHDRAWAL", "Opponent disconnected")));

                    if (!initiatedByClient) socketOpponent.send(JsonParser.toJson(new GameMessage("SERVER_CLOSING", "Fechamento limpo")));;
                }

                matchManager.endMatch(matchID, userID);
            } else {
                waitingQueue.remove(userID);
                playerSockets.remove(userID);
            }
        } catch (Exception e) {
            System.out.println("Error during player exit: " + e.getMessage());
        } finally {
            try {
                if (socket != null && socket.isOpen()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    private void handleAction(WebSocket socket, String action, String cardID, String userID, String matchID) {
        try {
            Match match = matchManager.getMatch(matchID);

            if (match == null) {
                socket.send(JsonParser.toJson(new GameMessage("ERROR", "Match not found")));
                return;
            }

            matchManager.handleAction(action, cardID, userID, matchID);

            if(!match.getGameState().getPlayerWin().equals("void")) {
                WebSocket playerOne = match.getSocketPlayerOne();
                WebSocket playerTwo = match.getSocketPlayerTwo();

                broadcastingGameState("VICTORY", playerOne, playerTwo, match.getId());

                playerOne.closeCleanly();
                playerTwo.closeCleanly();

                playerOne.send(JsonParser.toJson(new GameMessage("SERVER_CLOSING", "Fechamento limpo")));
                playerTwo.send(JsonParser.toJson(new GameMessage("SERVER_CLOSING", "Fechamento limpo")));

                matchManager.endMatch(matchID);

                return;
            }

            broadcastingGameState("UPDATE_GAME", match.getSocketPlayerOne(), match.getSocketPlayerTwo(), match.getId());
        } catch (Exception e) {
            System.out.println("Erro durante jogada do player: " + e.getMessage());
        }
    }

    private void broadcastingGameState(String type, WebSocket playerOne,WebSocket playerTwo, String matchID) throws IOException {
        if(playerOne.isOpen() && playerTwo.isOpen()){
            String data = JsonParser.toJson(new GameStateMessage(type, matchID, matchManager.getStateMatch(matchID)));

            playerOne.send(data);
            playerTwo.send(data);
        }
    }

    public WebSocketHandler websocket() {
        return new WebSocketHandler() {
            private final DeckService deckService = new DeckService();

            private void sendMessage(WebSocket socket, Object object) {
                try {
                    if (socket != null && socket.isOpen()) {
                        socket.send(JsonParser.toJson(object));
                    }
                } catch (Exception e) {
                    System.out.println("Error sending message: " + e.getMessage());
                }
            }

            private boolean validateGameAction(WebSocket socket, String token, String userID, String matchID) {
                try {
                    String authenticatedUserID = authService.validateToken(token);

                    if (!authenticatedUserID.equals(userID)) {
                        sendMessage(socket, new GameMessage("ERROR", "ID do usuário incompatível"));
                        return false;
                    }

                    if (matchID == null || matchID.isEmpty()) {
                        sendMessage(socket, new GameMessage("ERROR", "ID da partida é requerido"));
                        return false;
                    }

                    return true;
                } catch (Exception e) {
                    sendMessage(socket, new GameMessage("ERROR", "Autenticação inválida"));
                    return false;
                }
            }

            private void handleUnknownType(WebSocket socket) {
                sendMessage(socket, new GameMessage("ERROR", "Comando desconhecido"));
            }

            @Override
            public void onOpen(WebSocket socket) {
                int port = socket.getSocket().getPort();
                System.out.println("Player connected: " + port);
            }

            @Override
            public void onMessage(WebSocket socket, Map<String, Object> receive) {
                try {
                    String type = (String) receive.get("type");
                    String token = (String) receive.get("token");
                    String userID = (String) receive.get("userID");
                    String matchID = (String) receive.get("matchID");
                    String cardID = (String) receive.get("cardID");

                    if (type == null) {
                        handleUnknownType(socket);
                        return;
                    }

                    switch (type) {
                        case "joinQueue" -> {
                            try {
                                String authenticatedUserID = authService.validateToken(token);

                                if (deckService.findByUserId(authenticatedUserID).getCards().size() < 5) {
                                    sendMessage(socket, new GameMessage("WARNING", "Deck incompleto"));
                                    return;
                                }

                                handleJoin(socket, authenticatedUserID);
                            } catch (Exception e) {
                                sendMessage(socket, new GameMessage("ERROR", "Token invalido"));
                                socket.close();
                            }
                        }
                        case "activateCard" -> {
                            if (validateGameAction(socket, token, userID, matchID)) {

                                handleAction(socket, "activateCard", cardID, userID, matchID);
                            }
                        }
                        case "playCard" -> {
                            if (validateGameAction(socket, token, userID, matchID)) {

                                handleAction(socket, "playCard", cardID, userID, matchID);
                            }
                        }
                        case "ping" -> sendMessage(socket, new GameMessage( "PONG", "pong"));
                        case "exit" -> handleClose(socket, userID, matchID, true);
                        default -> handleUnknownType(socket);
                    }
                } catch (Exception e) {
                    System.out.println("Error processing message: " + e.getMessage());
                    sendMessage(socket, new GameMessage("ERROR", "Erro interno do processo"));
                }
            }

            @Override
            public void onClose(WebSocket socket) {
                String userID = matchManager.getUserIDBySocket(socket);
                String matchID = matchManager.getMatchIDBySocket(socket);

                if (userID != null) {
                    handleClose(socket, userID, matchID, false);
                }

                int port = socket.getSocket().getPort();
                System.out.println("Player disconnected: " + port);
            }
        };
    }
}
