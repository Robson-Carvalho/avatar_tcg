function updateGame() {
  try {
    const data = localStorage.getItem("gameState");
    if (!data) return;

    const json = JSON.parse(data);
    const gameState = JSON.parse(json.data);

    updateStatusContent(gameState);
  } catch (e) {
    console.error("Erro ao atualizar jogo:", e);
  }
}

function updateStatusContent(gameState) {
  const buttonPlayCard = document.getElementById("buttonPlayCard");

  if (localStorage.getItem("avatar_tcg_user_id") != gameState.turnPlayerId) {
    document.getElementById("currentTurn").innerText = "Turno do oponente";

    if (buttonPlayCard) {
      buttonPlayCard.disabled = true;
      buttonPlayCard.classList.add("cursor-not-allowed");
      buttonPlayCard.classList.remove("cursor-pointer");
    }
  } else {
    document.getElementById("currentTurn").innerText = "Seu turno";

    if (buttonPlayCard) {
      buttonPlayCard.disabled = false;
      buttonPlayCard.classList.add("cursor-pointer");
      buttonPlayCard.classList.remove("cursor-not-allowed");
    }
  }

  updateGameContent(gameState);
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
    const cardElement = createCardElement(card, true);
    document.getElementById("myHand").appendChild(cardElement);
  });

  if (player.activationCard) {
    const playedCard = player.cards.find((c) => c.id === player.activationCard);
    if (playedCard) {
      const cardElement = createCardElement(playedCard, false);
      document.getElementById("hero").appendChild(cardElement);
    }
  }

  if (opponent.activationCard) {
    const playedCard = opponent.cards.find(
      (c) => c.id === opponent.activationCard
    );
    if (playedCard) {
      const cardElement = createCardElement(playedCard, false);
      document.getElementById("opponent").appendChild(cardElement);
    }
  }

  if (gameState.turnPlayerId === userId) {
    setupHeroSlot(gameState);
  }
}

function createCardElement(card, isDraggable) {
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
    "w-28",
    "h-36",
    "flex",
    "flex-col",
    "justify-between",
  ];

  if (isDraggable) {
    cardDiv.classList.add(
      "cursor-grab",
      "hover:scale-105",
      "hover:border-green-400",
      "hover:shadow-lg",
      "draggable-card"
    );
    cardDiv.draggable = true;

    // 🔹 Evento direto aqui, sem depender de querySelectorAll
    cardDiv.addEventListener("dragstart", (e) => {
      const cardData = JSON.parse(cardDiv.dataset.card);

      // salva todos os dados da carta no dataTransfer
      e.dataTransfer.setData("cardId", cardData.id);
      e.dataTransfer.setData("cardName", cardData.name);
      e.dataTransfer.setData("cardElement", cardData.element);
      e.dataTransfer.setData("cardAttack", cardData.attack);
      e.dataTransfer.setData("cardDefense", cardData.defense);
      e.dataTransfer.setData("cardLife", cardData.life);
      e.dataTransfer.setData("cardRarity", cardData.rarity);

      cardDiv.classList.add("opacity-50", "scale-95");
    });

    cardDiv.addEventListener("dragend", () => {
      cardDiv.classList.remove("opacity-50", "scale-95");
    });
  }

  cardDiv.className = baseClasses.join(" ");

  // cores por elemento
  const elementColors = {
    WATER: "bg-blue-100 border-blue-300",
    FIRE: "bg-red-100 border-red-300",
    EARTH: "bg-green-100 border-green-300",
    AIR: "bg-gray-100 border-gray-300",
    BLOOD: "bg-red-200 border-red-400",
    METAL: "bg-zinc-100 border-zinc-300",
    LIGHTNING: "bg-yellow-100 border-yellow-300",
    AVATAR: "bg-purple-100 border-purple-300",
  };
  if (elementColors[card.element])
    cardDiv.classList.add(...elementColors[card.element].split(" "));

  // raridade
  const rarityClasses = {
    COMMON: "bg-gray-200 text-gray-800",
    RARE: "bg-green-200 text-green-800",
    EPIC: "bg-red-200 text-red-800",
    LEGENDARY: "bg-yellow-200 text-yellow-800",
  };
  const styles = rarityClasses[card.rarity] || "";

  cardDiv.innerHTML = `
      <div class="text-sm font-bold text-gray-800 truncate">${
        card.name.split(" ")[0]
      }</div>
      <div class="text-xs text-gray-600"><span class="font-semibold">${
        card.element
      }</span></div>
      <div class="text-xs space-y-1">
          <div class="flex justify-between"><span class="font-medium">ATK:</span><span class="text-red-600">${
            card.attack
          }</span></div>
          <div class="flex justify-between"><span class="font-medium">DEF:</span><span class="text-blue-600">${
            card.defense
          }</span></div>
          <div class="flex justify-between"><span class="font-medium">HP:</span><span class="text-green-600">${
            card.life
          }</span></div>
      </div>
      <div class="text-xs text-center mt-1 px-2 py-1 rounded-full ${styles}">${
    card.rarity
  }</div>
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

  heroSlot.addEventListener("dragover", (e) => {
    e.preventDefault();
    heroSlot.classList.add("bg-blue-100", "border-blue-500", "border-2");
  });

  heroSlot.addEventListener("dragleave", () => {
    heroSlot.classList.remove("bg-blue-100", "border-blue-500", "border-2");
    heroSlot.classList.add("bg-gray-50");
  });

  heroSlot.addEventListener("drop", (e) => {
    e.preventDefault();
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

    if (cardData.id && heroSlot.children.length === 0) {
      // Remove carta da mão
      const cardElement = document.querySelector(
        `[data-card-id="${cardData.id}"]`
      );
      if (cardElement) cardElement.remove();

      // Renderiza no campo
      heroSlot.appendChild(createCardElement(cardData, false));

      // Envia jogada
      activationCard(gameState, cardData.id);
    }
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

  const isPlayerOne = userId === gameState.playerOne.id;
  const player = isPlayerOne ? gameState.playerOne : gameState.playerTwo;

  socket.send(
    JSON.stringify({
      type: "playCard",
      token: localStorage.getItem("token"),
      userID: localStorage.getItem("avatar_tcg_user_id"),
      cardID:  player.activationCard,
      matchID: gameState.matchID
    })
  );

  console.log("Jogar!");
}
