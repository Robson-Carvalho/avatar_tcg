function updateGame() {
  try {
    const data = localStorage.getItem("gameState");
    if (!data) return;

    const json = JSON.parse(data);
    const gameState = JSON.parse(json.data);

    updateStatusContent(gameState);
    updateGameContent(gameState);
  } catch (e) {
    console.error("Erro ao atualizar jogo:", e);
  }
}

function updateStatusContent(gameState) {
  const buttonPlayCard = document.getElementById("buttonPlayCard");
  const points = document.getElementById("points");
  const userId = localStorage.getItem("avatar_tcg_user_id");
  const isMyTurn = userId === gameState.turnPlayerId;

  document.getElementById("currentTurn").innerText = isMyTurn
    ? "Seu turno"
    : "Turno do oponente";

  const me =
    gameState.playerOne.id === userId
      ? gameState.playerOne
      : gameState.playerTwo;
  points.innerText = me.points;

  if (buttonPlayCard) {
    buttonPlayCard.disabled = !isMyTurn;
    buttonPlayCard.classList.toggle("cursor-pointer", isMyTurn);
    buttonPlayCard.classList.toggle("cursor-not-allowed", !isMyTurn);
  }
}

function updateGameContent(gameState) {
  const userId = localStorage.getItem("avatar_tcg_user_id");
  const isPlayerOne = userId === gameState.playerOne.id;
  const player = isPlayerOne ? gameState.playerOne : gameState.playerTwo;
  const opponent = isPlayerOne ? gameState.playerTwo : gameState.playerOne;

  document.getElementById("myHand").innerHTML = "";
  document.getElementById("hero").innerHTML = "";
  document.getElementById("opponent").innerHTML = "";

  player.cards.forEach((card) => {
    const cardElement = createBattleCard(card, true);
    document.getElementById("myHand").appendChild(cardElement);
  });

  if (player.activationCard) {
    const playedCard = player.cards.find((c) => c.id === player.activationCard);

    if (playedCard) {
      const cardElement = createBattleCard(playedCard, false);
      document.getElementById("hero").appendChild(cardElement);
    }
  }

  if (opponent.activationCard) {
    const playedCard = opponent.cards.find(
      (c) => c.id === opponent.activationCard
    );
    if (playedCard) {
      const cardElement = createBattleCard(playedCard, false);
      document.getElementById("opponent").appendChild(cardElement);
    }
  }

  if (gameState.turnPlayerId === userId) {
    setupHeroSlot(gameState);
  }
}

function createBattleCard(card, isDraggable) {
  const cardDiv = document.createElement("div");
  cardDiv.dataset.card = JSON.stringify(card);
  cardDiv.dataset.cardId = card.id;

  const baseClasses = [
    "bg-white",
    "rounded-lg",
    "shadow-md",
    "p-3",
    "m-2",
    "transition-all",
    "duration-200",
    "border-2",
    "border-gray-200",
    "w-40",
    "h-56",
    "flex",
    "flex-col",
    "justify-between",
  ];
  cardDiv.className = baseClasses.join(" ");

  if (isDraggable) {
    cardDiv.classList.add(
      "cursor-grab",
      "hover:scale-105",
      "hover:border-green-400",
      "hover:shadow-lg",
      "draggable-card"
    );
    cardDiv.draggable = true;

    cardDiv.addEventListener("dragstart", (e) => {
      const cardData = JSON.parse(cardDiv.dataset.card);
      Object.entries(cardData).forEach(([key, value]) => {
        e.dataTransfer.setData(
          `card${key.charAt(0).toUpperCase() + key.slice(1)}`,
          value
        );
      });
      cardDiv.classList.add("opacity-50", "scale-95");
    });

    cardDiv.addEventListener("dragend", () => {
      cardDiv.classList.remove("opacity-50", "scale-95");
    });
  }

  const elementColors = {
    WATER: "border-blue-300",
    FIRE: "border-red-300",
    EARTH: "border-amber-600",
    AIR: "border-sky-100",
    BLOOD: "border-rose-600",
    METAL: "border-slate-400",
    LIGHTNING: "border-yellow-300",
    AVATAR: "border-purple-300",
  };
  if (elementColors[card.element]) {
    cardDiv.classList.add(...elementColors[card.element].split(" "));
  }

  const rarityClasses = {
    COMMON: "bg-gray-200 text-gray-800",
    RARE: "bg-green-200 text-green-800",
    EPIC: "bg-red-200 text-red-800",
    LEGENDARY: "bg-yellow-200 text-yellow-800",
  };
  const rarityStyle = rarityClasses[card.rarity] || "";

  cardDiv.innerHTML = `
    <div class="text-lg font-bold text-gray-800 text-center break-words leading-tight">
      ${card.name}
    </div>
    <div class="text-sm text-gray-600 text-center mb-1">
      <span class="font-semibold">${card.element}</span>
    </div>
    <div class="text-sm space-y-1">
      <div class="flex justify-between">
        <span class="font-medium">ATK:</span>
        <span class="text-red-600 font-semibold">${card.attack}</span>
      </div>
      <div class="flex justify-between">
        <span class="font-medium">DEF:</span>
        <span class="text-blue-600 font-semibold">${card.defense}</span>
      </div>
      <div class="flex justify-between">
        <span class="font-medium">HP:</span>
        <span class="text-green-600 font-semibold">${card.life}</span>
      </div>
    </div>
    <div class="text-xs text-center mt-2 px-2 py-1 rounded-full ${rarityStyle}">
      <strong>${card.rarity}</strong>
    </div>
  `;

  return cardDiv;
}

function setupHeroSlot(gameState) {
  const heroSlot = document.getElementById("hero");

  heroSlot.classList.add(
    "min-h-[200px]",
    "bg-gray-50",
    "transition-colors",
    "duration-200"
  );

  if (heroSlot.dataset.listeners === "true") return;
  heroSlot.dataset.listeners = "true";

  heroSlot.addEventListener("dragover", (e) => {
    e.preventDefault();
    if (gameState.turnPlayerId === localStorage.getItem("avatar_tcg_user_id")) {
      heroSlot.classList.add("bg-blue-100", "border-blue-500", "border-2");
    }
  });

  heroSlot.addEventListener("dragleave", () => {
    heroSlot.classList.remove("bg-blue-100", "border-blue-500", "border-2");
    heroSlot.classList.add("bg-gray-50");
  });

  heroSlot.addEventListener("drop", (e) => {
    e.preventDefault();

    const data = localStorage.getItem("gameState");
    if (!data) return;
    const json = JSON.parse(data);
    const gameState = JSON.parse(json.data);

    if (gameState.turnPlayerId !== localStorage.getItem("avatar_tcg_user_id")) {
      console.warn("Não é seu turno!");
      return;
    }

    heroSlot.classList.remove("bg-blue-100", "border-blue-500", "border-2");
    heroSlot.classList.add("bg-gray-50");

    const cardData = {
      id: e.dataTransfer.getData("cardId"),
      name: e.dataTransfer.getData("cardName"),
      element: e.dataTransfer.getData("cardElement"),
      attack: e.dataTransfer.getData("cardAttack"),
      defense: e.dataTransfer.getData("cardDefense"),
      life: e.dataTransfer.getData("cardLife"),
      rarity: e.dataTransfer.getData("cardRarity"),
    };

    if (!cardData.id) return;

    heroSlot.innerHTML = "";
    const cardElement = document.querySelector(
      `[data-card-id="${cardData.id}"]`
    );
    if (cardElement) cardElement.remove();

    heroSlot.appendChild(createBattleCard(cardData, false));
    activationCard(gameState, cardData.id);
  });
}

function activationCard(gameState, cardId) {
  socket.send(
    JSON.stringify({
      type: "activateCard",
      token: localStorage.getItem("token"),
      cardID: cardId,
      userID: localStorage.getItem("avatar_tcg_user_id"),
      matchID: gameState.matchID,
    })
  );
  console.log("Ativou carta:", cardId);
}

function playCard() {
  const data = localStorage.getItem("gameState");
  const json = JSON.parse(data);
  const gameState = JSON.parse(json.data);
  const userId = localStorage.getItem("avatar_tcg_user_id");

  const isPlayerOne = userId === gameState.playerOne.id;
  const player = isPlayerOne ? gameState.playerOne : gameState.playerTwo;

  if (!player.activationCard) {
    alert("Ative uma carta!");
    return;
  }

  socket.send(
    JSON.stringify({
      type: "playCard",
      token: localStorage.getItem("token"),
      userID: userId,
      cardID: player.activationCard,
      matchID: gameState.matchID,
    })
  );
  console.log("Jogar!");
}
