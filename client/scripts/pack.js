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
        if (!res.ok) throw new Error();

        const { cards } = await res.json();
        document.getElementById("loadingPack").classList.add("hidden");

        if (cards.length > 0) {
            renderCards(cards, "packContainer");
            document.getElementById("packContainer").classList.remove("hidden");
        } else {
            document.getElementById("packError").classList.remove("hidden");
        }
    } catch {
        document.getElementById("loadingPack").classList.add("hidden");
        document.getElementById("packError").classList.remove("hidden");
    }
}

function closePackModal() {
    document.getElementById("packModal").classList.add("hidden");
}
