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

    
    for (let i = 1; i <= 5; i++) {
        const slotId = deck[`card${i}Id`];
        const slot = document.createElement("div");
        slot.className = "deck-slot border-2 border-dashed border-gray-400 rounded-lg flex items-center justify-center bg-gray-50 p-2";
        slot.dataset.slot = `card${i}Id`;
        slot.ondragover = ev => ev.preventDefault();
        slot.ondrop = ev => handleDrop(ev, slot);

        if (slotId) {
            const card = cards.find(c => c.id === slotId);
            if (card) {
                const cardEl = createBattleCard(card, false); 
                addRemoveButton(cardEl, slot);
                slot.appendChild(cardEl);
                slot.dataset.cardId = card.id;
            } else {
                slot.innerHTML = "<span class='text-gray-400'>Slot vazio</span>";
                slot.dataset.cardId = "";
            }
        } else {
            slot.innerHTML = "<span class='text-gray-400'>Slot vazio</span>";
            slot.dataset.cardId = "";
        }

        deckSlots.appendChild(slot);
    }

    
    const available = document.getElementById("availableCards");
    available.innerHTML = "";
    cards.forEach(c => {
        available.appendChild(createBattleCard(c, true)); 
    });
}


function addRemoveButton(cardEl, slot) {
    const removeBtn = document.createElement("button");
    removeBtn.innerText = "✖";
    removeBtn.className = "absolute top-1 right-1 text-red-500 hover:text-red-700 text-sm";
    removeBtn.onclick = () => {
        slot.innerHTML = "<span class='text-gray-400'>Slot vazio</span>";
        slot.dataset.cardId = "";
    };
    cardEl.classList.add("relative"); 
    cardEl.appendChild(removeBtn);
}

function handleDrop(ev, slot) {
    ev.preventDefault();
    const cardId = ev.dataTransfer.getData("cardId");
    const cardName = ev.dataTransfer.getData("cardName");
    const cardElement = ev.dataTransfer.getData("cardElement");
    const cardAttack = ev.dataTransfer.getData("cardAttack");
    const cardDefense = ev.dataTransfer.getData("cardDefense");
    const cardLife = ev.dataTransfer.getData("cardLife");
    const cardRarity = ev.dataTransfer.getData("cardRarity");

    if (!cardId) return;

    slot.innerHTML = "";
    const cardEl = createBattleCard(
        {
            id: cardId,
            name: cardName,
            element: cardElement,
            attack: cardAttack,
            defense: cardDefense,
            life: cardLife,
            rarity: cardRarity
        },
        false
    );
    addRemoveButton(cardEl, slot);
    slot.appendChild(cardEl);
    slot.dataset.cardId = cardId;
    slot.dataset.newlyAdded = "true";
}

async function saveDeck() {
    const token = localStorage.getItem("token");
    if (!token) return;

    const deckSlots = document.querySelectorAll("#deckSlots .deck-slot");

    const body = { 
        deckID: localStorage.getItem("avatar_tcg_deck_id"), 
        userId: localStorage.getItem("avatar_tcg_user_id") 
    }; 

    deckSlots.forEach((slot, i) => {
        body[`card${i+1}Id`] = slot.dataset.cardId || null;
    });

    try {
        const res = await fetch(`${API_URL}/deck/update`, {
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
