// websocket.js (browser) — replaced by SSE + HTTP commands via local proxy.
// Keeps the same exported functions expected by the rest of the app.

let sse;
let matchId = null;
let userId = localStorage.getItem("avatar_tcg_user_id");
let myTurn = false;
let pingInterval = null;
let lastPingTime = 0;

function startPingLoop() {
  stopPingLoop();
  pingInterval = setInterval(() => {
    lastPingTime = performance.now();
    // ask proxy to send ping to AOK fullduplex
    fetch(`${API_URL}/game/send`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify({ type: 'ping' })
    }).catch(() => {});
  }, 1000);
}

function stopPingLoop() {
  if (pingInterval) clearInterval(pingInterval);
  pingInterval = null;
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
  const el = document.getElementById("pingValue");
  if (el) el.textContent = display;
}

function connectToGame() {
  const token = localStorage.getItem("token");
  if (!token) return alert("Você precisa estar logado!");

  // Start SSE stream
  if (sse) {
    try { sse.close(); } catch {}
    sse = null;
  }

  sse = new EventSource(`${API_URL}/game/stream?token=${encodeURIComponent(token)}`);

  sse.addEventListener('open', () => {
    console.log('SSE connected.');
    // Tell proxy to send joinQueue
    fetch(`${API_URL}/game/send`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        type: "joinQueue",
        userID: userId,
      })
    }).catch(() => {});
  });

  sse.addEventListener('message', (ev) => {
    try {
      const receive = JSON.parse(ev.data);
      handleIncoming(receive);
    } catch (e) {
      console.warn('Bad SSE message', e);
    }
  });

  // Relay typed events too
  ['IN_QUEUE','MATCH_FOUND','VICTORY_WITHDRAWAL','VICTORY','MATCH_FINISHED','UPDATE_GAME','PONG','ERROR','WARNING','pong'].forEach(evt => {
    sse.addEventListener(evt, (ev) => {
      try {
        const receive = JSON.parse(ev.data);
        handleIncoming(receive);
      } catch {}
    });
  });

  sse.addEventListener('error', (ev) => {
    console.error('SSE error', ev);
    // Auto-close
    endGame();
  });
}

function handleIncoming(receive) {
  if (!receive) return;

  if (receive.message === "pong" || receive.type === "PONG") {
    updatePing();
  }

  if (receive.type === "MATCH_FOUND" || receive.type === "UPDATE_GAME") {
    localStorage.setItem("gameState", JSON.stringify(receive));
  }

  handleServerMessage(receive);
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

function handleVictory(json) {
  const data = typeof json === 'string' ? JSON.parse(json) : json;
  const game = document.getElementById("game");
  if (game) game.classList.add("hidden");
  if (localStorage.getItem("avatar_tcg_user_id") == data.playerWin) {
    const victoryModal = document.getElementById("victoryModal");
    const gameContainer = document.getElementById("gameContainer");
    if (gameContainer) gameContainer.classList.remove("hidden");
    if (victoryModal) victoryModal.classList.remove("hidden");
  } else {
    console.log("Perdeu");
  }
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
    case "VICTORY":
      handleVictory(receive.data);
      break;
    case "MATCH_FINISHED":
      console.log("Match finalizada.");
      break;
    case "UPDATE_GAME":
      updateGame();
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
  }
}

function surrender() {
  fetch(`${API_URL}/game/send`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    },
    body: JSON.stringify({
      type: "exit",
      matchID: matchId,
      userID: userId,
    })
  }).catch(() => {});
  endGame();
}

function endGame() {
  document.getElementById("gameContainer").classList.add("hidden");
  document.getElementById("waitingQueue").classList.add("hidden");
  document.getElementById("game").classList.add("hidden");
  if (sse) try { sse.close(); } catch {}
  sse = null;
  matchId = null;
  myTurn = false;
  stopPingLoop();
  // tell proxy to close session (optional)
  fetch(`${API_URL}/game/close`, {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
  }).catch(() => {});
  showMatches();
}