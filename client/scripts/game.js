
function updateGame() {
  const data = localStorage.getItem("gameState");

  const json = JSON.parse(data);

  const gameState = JSON.parse(json.data);

  updateContent(gameState);
}

function updateContent(gameState) {  
  if (localStorage.getItem("avatar_tcg_user_id") != gameState.turnPlayerId) {
    document.getElementById("currentTurn").innerText = "Turno do oponente"
  } else {
    document.getElementById("currentTurn").innerText = "Seu turno"
  };

   
}
