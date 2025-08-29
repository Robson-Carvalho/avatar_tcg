let socket;
let matchId = null;
let userId = localStorage.getItem("avatar_tcg_user_id");
let myTurn = false;

let lastPingTime = 0;

function startPingLoop() {
    setInterval(() => {
        if (socket.readyState === WebSocket.OPEN) {
            lastPingTime = performance.now();

            socket.send(JSON.stringify({ type: "ping" }));
        }
    }, 1000); 
}

function updatePing() {
    const latencyMs = performance.now() - lastPingTime;

    const safeLatency = Math.max(latencyMs, 0);

    let display;
    if (safeLatency < 1) {
        display = `${(safeLatency * 1000).toPrecision(3)} μs`;
    } else if (safeLatency > 1000) {
        display = `${(safeLatency / 1000).toFixed(3)} s`;
    } else {
        display = `${safeLatency.toFixed(3)} ms`;
    }

    document.getElementById("pingValue").textContent = display;
}

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

        if (receive.message === "pong") {
             updatePing();
        } 
        
        if (receive.data) {    
            localStorage.setItem("gameState", event.data);
        }

        handleServerMessage(receive);
    };

    socket.onclose = () => {
        console.log("Conexão encerrada.");
        
        if (!document.getElementById("victoryModal").classList.contains("hidden")) {
            return;
        }

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

    if (gameContainer) gameContainer.classList.remove("hidden");

    const waitingQueue = document.getElementById("waitingQueue");

    const game = document.getElementById("game");
    if (waitingQueue) waitingQueue.classList.add("hidden");

    if (game) game.classList.remove("hidden");

    if (id) matchId = id; 

    startPingLoop();
    updateGame();
}

function handleWithdrawal() {
    const game = document.getElementById("game");
    if (game) game.classList.add("hidden");

    showVictoryModal();
}

function showVictoryModal() {
    const victoryModal = document.getElementById("victoryModal");
    const gameContainer = document.getElementById("gameContainer");
    
    if (gameContainer) gameContainer.classList.remove("hidden"); 
    if (victoryModal) victoryModal.classList.remove("hidden");   
}

function hideVictoryModal() {
    const victoryModal = document.getElementById("victoryModal");
    const gameContainer = document.getElementById("gameContainer");

    if (victoryModal) victoryModal.classList.add("hidden");
    if (gameContainer) gameContainer.classList.add("hidden"); 

    endGame();
}

function handleServerMessage(receive) {
    switch (receive.type) {
        case "IN_QUEUE":
            showWaitingMatch();
            break;

        case "MATCH_FOUND":
            matchMake(receive.message);
            break;
        
        case "VICTORY_WITHDRAWAL":
            handleWithdrawal();
            break;

        case "MATCH_FINISHED":
            console.log("Match finalizada.");
            break;

        case "UPDATE":
            console.log("UPDATE:", receive.message);
            break;

        case "ERROR":
            console.error("ERROR:", receive.message);
            alert("Erro: " + receive.message);
            break;
        
        case "WARNING":
            console.error("AVISO:", receive.message);
            alert("Atenção: " + receive.message);
            break;
        
        case "PONG":
            break;

        default:
            console.warn("Mensagem desconhecida:", receive);
            break;
    }
}

function surrender() {
    if (!socket) return;
    socket.send(JSON.stringify({
        type: "exit",
        token: localStorage.getItem("token"),
        matchID: matchId,
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

