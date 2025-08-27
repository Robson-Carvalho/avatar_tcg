let socket;
let matchId = null;
let userId = localStorage.getItem("avatar_tcg_user_id");
let myTurn = false;

function connectToGame() {
    const token = localStorage.getItem("token");

    if (!token) return alert("Você precisa estar logado!");

    socket = new WebSocket("ws://localhost:8080/game");

    socket.onopen = () => {
        console.log("Conexão estabelecida.");
        socket.send(JSON.stringify({
            type: "joinQueue",
            token,
            userID: userId
        }));
    };

    socket.onmessage = (event) => {
        const receive = JSON.parse(event.data);
        handleServerMessage(receive);
    };

    socket.onclose = () => {
        console.log("Conexão encerrada.");
        endGame();
    };
}

function showWaitingMatch() {
    const gameContainer = document.getElementById("gameContainer");
    const waitingQueue = document.getElementById("waitingQueue");

    if (gameContainer) gameContainer.classList.remove("hidden");
    if (waitingQueue) waitingQueue.classList.remove("hidden");
}

function matchMake(id) {
    const gameContainer = document.getElementById("gameContainer");
    const waitingQueue = document.getElementById("waitingQueue");
    const game = document.getElementById("game");

    if (gameContainer) gameContainer.classList.remove("hidden");
    if (waitingQueue) waitingQueue.classList.add("hidden");
    if (game) game.classList.remove("hidden");

    if (id) matchId = id; // guarda o id da partida
}

function handleServerMessage(receive) {
    switch (receive.type) {
        case "IN_QUEUE":
            // jogador foi colocado na fila
            showWaitingMatch();
            break;

        case "MATCH_FOUND":
            console.log("oiii")
            matchMake(receive.message);
            break;

        case "UPDATE":
            console.log("UPDATE:", receive.message);
            break;

        case "ERROR":
            console.error("ERROR:", receive.message);
            alert("Erro: " + receive.message);
            break;

        default:
            console.warn("Mensagem desconhecida:", receive);
            break;
    }
}

function updateTurnInfo() {
    const turnInfo = document.getElementById("turnInfo");
    turnInfo.innerText = myTurn ? "Seu turno!" : "Turno do oponente";
}

function attack(cardId) {
    if (!myTurn) return alert("Não é seu turno!");

    socket.send(JSON.stringify({
        type: "attack",
        token: localStorage.getItem("token"),
        userID: userId,
        matchId: matchId,
        cardId
    }));
}

function changeActiveCard(cardId) {
    if (!myTurn) return alert("Não é seu turno!");
    socket.send(JSON.stringify({
        type: "changeCard",
        token: localStorage.getItem("token"),
        userID: userId,
        matchId: matchId,
        cardId
    }));
}

function surrender() {
    if (!socket) return;
    socket.send(JSON.stringify({
        type: "exit",
        token: localStorage.getItem("token"),
        matchId: matchId,
        userID: userId
    }));

    endGame();
}

function endGame() {
    document.getElementById("gameContainer").classList.add("hidden");
    document.getElementById("waitingQueue").classList.add("hidden");
    document.getElementById("game").classList.add("hidden");

    if (socket) socket.close();
    socket = null;
    matchId = null;
    myTurn = false;

    showMatches();
}

