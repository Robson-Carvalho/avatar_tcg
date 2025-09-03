function showCards() {
    document.getElementById("cardsModal").classList.remove("hidden");
    loadCards();
}

async function loadCards() {
    const token = localStorage.getItem("token");
    if (!token) return alert("Você precisa estar logado!");

    document.getElementById("loadingCards").classList.remove("hidden");
    document.getElementById("cardsContainer").classList.add("hidden");
    document.getElementById("noCards").classList.add("hidden");
    document.getElementById("cardsError").classList.add("hidden");

    try {
        const res = await fetch(`${API_URL}/card`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        if (!res.ok) throw new Error();

        const { cards } = await res.json();
        document.getElementById("loadingCards").classList.add("hidden");

        if (cards.length > 0) {
            renderCards(cards, "cardsContainer"); 
            document.getElementById("cardsContainer").classList.remove("hidden");
        } else {
            document.getElementById("noCards").classList.remove("hidden");
        }
    } catch {
        document.getElementById("loadingCards").classList.add("hidden");
        document.getElementById("cardsError").classList.remove("hidden");
    }
}

function closeCardsModal() {
    document.getElementById("cardsModal").classList.add("hidden");
}


function renderCards(cards, containerId) {
    const container = document.getElementById(containerId);
    container.innerHTML = "";

    cards.forEach(card => {
        const cardEl = createBattleCard(card, false); 
        container.appendChild(cardEl);
    });
}
