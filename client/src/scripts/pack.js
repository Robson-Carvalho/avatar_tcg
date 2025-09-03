async function openPack() {
    const token = localStorage.getItem("token");
    if (!token) return alert("Precisa estar logado");

    document.getElementById("packModal").classList.remove("hidden");
    document.getElementById("loadingPack").classList.remove("hidden");
    document.getElementById("packContainer").classList.add("hidden");
    document.getElementById("packError").classList.add("hidden");

    try {
        const res = await fetch(`${API_URL}/card/open`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        
        if (!res.ok) {
            document.getElementById("packError").classList.remove("hidden");
            throw new Error()
        };

        const { cards } = await res.json();
        document.getElementById("loadingPack").classList.add("hidden");
        
        if (cards.length > 0) {
            renderPackCards(cards, "packContainer"); 
            document.getElementById("packContainer").classList.remove("hidden");
        } else {
            document.getElementById("packVoid").classList.remove("hidden");
        }
    } catch {
        document.getElementById("loadingPack").classList.add("hidden");
        document.getElementById("packError").classList.remove("hidden");
    }
}

function closePackModal() {
    document.getElementById("packModal").classList.add("hidden");
}

function renderPackCards(cards, containerId) {
    const container = document.getElementById(containerId);
    container.innerHTML = "";

    cards.forEach(card => {
        const cardEl = createBattleCard(card, false); 
        container.appendChild(cardEl);
    });
}
