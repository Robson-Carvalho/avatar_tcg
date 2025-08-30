function openDeckModal() {
    document.getElementById("deckModal").classList.remove("hidden");
    loadDeck();
}

function closeDeckModal() {
    document.getElementById("deckModal").classList.add("hidden");
}

async function loadDeck() {
    const token = localStorage.getItem("token");
    if (!token) return alert("Precisa estar logado");

    try {
        const deckRes = await fetch(`${API_URL}/deck`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        const { deck } = await deckRes.json(); 

        localStorage.setItem("avatar_tcg_deck_id", deck.id);

        const cardsRes = await fetch(`${API_URL}/card`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        const { cards } = await cardsRes.json();

        renderDeck(deck, cards);
    } catch (err) {
        console.error(err);
        alert("Erro ao carregar deck");
    }
}

function renderDeck(deck, cards) {
    const deckSlots = document.getElementById("deckSlots");
    deckSlots.innerHTML = "";

    // cria 5 slots
    for (let i = 1; i <= 5; i++) {
        const slotId = deck[`card${i}Id`];
        const slot = document.createElement("div");
        slot.className = "deck-slot border-2 border-dashed border-gray-400 rounded-lg h-40 flex items-center justify-center bg-gray-50";
        slot.dataset.slot = `card${i}Id`;
        slot.ondragover = ev => ev.preventDefault();
        slot.ondrop = ev => handleDrop(ev, slot);

        if (slotId) {
            const card = cards.find(c => c.id === slotId);
            if (card) {
                slot.appendChild(createCardElement(card, true));
                slot.dataset.cardId = card.id; // 👈 ESSENCIAL: manter o id no dataset
            } else {
                // segurança: se id não existir mais na coleção de cartas
                slot.innerHTML = "<span class='text-gray-400'>Slot vazio</span>";
                slot.dataset.cardId = ""; // 👈 garante vazio
            }
        } else {
            slot.innerHTML = "<span class='text-gray-400'>Slot vazio</span>";
            slot.dataset.cardId = ""; // 👈 garante vazio
        }

        deckSlots.appendChild(slot);
    }

    // cartas disponíveis
    const available = document.getElementById("availableCards");
    available.innerHTML = "";
    cards.forEach(c => {
        available.appendChild(createCardElement(c));
    });
}

function createCardElement(card, inDeck = false) {
    const div = document.createElement("div");
    div.className = "card bg-white rounded-lg shadow p-2 flex flex-col items-center relative";
    div.draggable = !inDeck; // cartas dentro do deck não podem ser arrastadas novamente (opcional)
    div.dataset.id = card.id;

    if (!inDeck) {
        div.ondragstart = ev => {
            ev.dataTransfer.setData("cardId", card.id);
            ev.dataTransfer.setData("cardName", card.name);
            ev.dataTransfer.setData("cardElement", card.element);
        };
    }

    div.innerHTML = `
        <div class="w-20 h-28 p-2 bg-gray-200 rounded flex items-center justify-center text-center">
            <span class="text-xs">${card.name}</span>
        </div>
        <p class="text-xs mt-1">${card.element}</p>
    `;

    if (inDeck) {
        const removeBtn = document.createElement("button");
        removeBtn.innerText = "✖";
        removeBtn.className = "absolute top-1 right-1 text-red-500 hover:text-red-700 text-sm";
        removeBtn.onclick = () => {
            const slot = div.parentElement;
            slot.innerHTML = "<span class='text-gray-400'>Slot vazio</span>";
            slot.dataset.cardId = ""; // limpa o slot
        };
        div.appendChild(removeBtn);
    }

    return div;
}

function handleDrop(ev, slot) {
    ev.preventDefault();
    const cardId = ev.dataTransfer.getData("cardId");
    const cardName = ev.dataTransfer.getData("cardName");
    const cardElement = ev.dataTransfer.getData("cardElement");
    if (!cardId) return;

    slot.innerHTML = "";
    slot.appendChild(createCardElement({ id: cardId, name: cardName, element: cardElement }, true));
    slot.dataset.cardId = cardId;

    slot.dataset.newlyAdded = "true";
}

async function saveDeck() {
    const token = localStorage.getItem("token");
    if (!token) return;

    const deckSlots = document.querySelectorAll("#deckSlots .deck-slot");

  
  
    const body = { id: localStorage.getItem("avatar_tcg_deck_id"), userId: localStorage.getItem("avatar_tcg_user_id") }; 

    deckSlots.forEach((slot, i) => {
        body[`card${i+1}Id`] = slot.dataset.cardId || null;
    });

    try {
        const res = await fetch(`${API_URL}/deck`, {
            method: "PUT",
            headers: { 
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}` 
          },
          body: JSON.stringify(body)
        });
      
        if (!res.ok) {
            const errorData = await res.json();
            throw new Error(errorData.error || "Erro ao salvar deck");
        }

        alert("Deck salvo com sucesso!");
    } catch (err) {
        console.error(err);
        alert(err.message);
    }
}
