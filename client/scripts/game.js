let socket;
let matchId = null;
let userId = localStorage.getItem("avatar_tcg_user_id");
let myTurn = false;

function connectToGame() {
    const token = localStorage.getItem("token");
    if (!token) return alert("Você precisa estar logado!");

    socket = new WebSocket("ws://localhost:8080/game");

    socket.onopen = () => {
        console.log("Conectado ao servidor WebSocket!");
        socket.send(JSON.stringify({
            type: "joinQueue",
            token,
            userID: userId
        }));
    };

    socket.onmessage = (event) => {
        console.log("oi", event.data);

        //const msg = JSON.parse(event.data);
        //handleServerMessage(msg);
    };

    socket.onclose = () => {
        console.log("Conexão encerrada.");
        endGame();
    };
}

function handleServerMessage(msg) {
    console.log("Mensagem recebida:", msg);

    switch (msg.type) {
        case "queueStatus":
            alert(msg.payload.message);
            break;

        case "matchStart":
            matchId = msg.payload.matchId;
            myTurn = msg.payload.yourTurn;
            document.getElementById("dashboard").classList.add("hidden");
            document.getElementById("gameContainer").classList.remove("hidden");
            updateTurnInfo();
            break;

        case "matchUpdate":
            myTurn = (msg.payload.turn === userId);
            renderBattlefield(msg.payload);
            updateTurnInfo();
            break;

        case "gameOver":
            alert("Fim de jogo! Vencedor: " + msg.payload.winner);
            endGame();
            break;

        case "error":
            alert("Erro: " + msg.payload.message);
            break;
    }
}

// Mostrar de quem é o turno
function updateTurnInfo() {
    const turnInfo = document.getElementById("turnInfo");
    turnInfo.innerText = myTurn ? "Seu turno!" : "Turno do oponente";
}

// Enviar ataque
function attack(cardId) {
    if (!myTurn) return alert("Não é seu turno!");
    socket.send(JSON.stringify({
        type: "attack",
        token: localStorage.getItem("token"),
        userID: userId,
        cardId
    }));
}

function changeActiveCard(cardId) {
    if (!myTurn) return alert("Não é seu turno!");
    socket.send(JSON.stringify({
        type: "changeCard",
        token: localStorage.getItem("token"),
        userID: userId,
        cardId
    }));
}

function surrender() {
    if (!socket) return;
    socket.send(JSON.stringify({
        type: "exitGame",
        token: localStorage.getItem("token"),
        userID: userId
    }));
    endGame();
}

function endGame() {
    document.getElementById("gameContainer").classList.add("hidden");
    document.getElementById("dashboard").classList.remove("hidden");

    if (socket) socket.close();
    socket = null;
    matchId = null;
    myTurn = false;
}
