function showLoadingState() {
    const matchesContainer = document.getElementById("matchs");
    matchesContainer.innerHTML = `
        <div class="text-center py-8">
            <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
            <p class="text-gray-600 mt-2">Carregando partidas...</p>
        </div>
    `;
    matchesContainer.classList.remove("hidden");
}

function showMatches() {
    const matchesContainer = document.getElementById("matchs");
    matchesContainer.classList.remove("hidden");
    loadMatches();
}

async function loadMatches() {
    const token = localStorage.getItem("token");
    if (!token) {
        return;
    }

    showLoadingState();

    try {
        const res = await fetch(`${API_URL}/match`, {
            method: "GET", 
            headers: { "Authorization": `Bearer ${token}` },
        });


        if (!res.ok) throw new Error("Erro ao carregar partidas");

        const json = await res.json();
        const matchs = (json.matchs || []).slice().reverse();

        if (matchs.length > 0) {
            renderMatches(matchs);
        } else {
            showNoMatches();
        }
    } catch (error) {
        console.error("Erro ao carregar partidas:", error);
        showNoMatches();
    }
}


function renderMatches(matches) {
    const matchesContainer = document.getElementById("matchs");

    matchesContainer.innerHTML = '';

    matches.forEach(match => {
        const matchElement = createMatchElement(match);
        matchesContainer.appendChild(matchElement);
    });

    matchesContainer.classList.remove("hidden");
}

function createMatchElement(match) {
    const div = document.createElement("div");
    div.className = "match-item bg-white p-4 rounded-lg shadow-md mb-4";

    let winner = match.playerWin == localStorage.getItem("avatar_tcg_user_id")

    div.innerHTML = `
        <div class="flex  justify-between items-center">
            <h3 class="text-lg font-semibold text-gray-800">Partida ID: ${match.id}</h3>
            <span class="px-3 py-1 rounded-full text-sm font-medium ${winner ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}">
                ${winner ? 'Vitória' : 'Derrota'}
            </span>
        </div>
    `;

    return div;
}

function showNoMatches() {
    const matchesContainer = document.getElementById("matchs");
    matchesContainer.innerHTML = `
        <div class="text-center py-8">
            <div class="text-gray-500 text-xl mb-2">🏆</div>
            <p class="text-gray-600 text-lg font-medium">Não há partidas jogadas</p>
            <p class="text-gray-500 mt-1">Jogue sua primeira partida para ver estatísticas aqui!</p>
        </div>
    `;
    matchesContainer.classList.remove("hidden");
}


document.addEventListener('DOMContentLoaded', function() {
    showMatches();
});