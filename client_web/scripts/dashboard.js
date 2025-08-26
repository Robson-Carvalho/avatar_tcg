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
        const res = await fetch(`${API_URL}/matches`, {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!res.ok) throw new Error("Erro ao carregar partidas");

        const data = await res.json();
        const matches = data.matches || data;

        if (matches && matches.length > 0) {
            renderMatches(matches);
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

    div.innerHTML = `
        <div class="flex justify-between items-center">
            <h3 class="text-lg font-semibold text-gray-800">Partida #${match.id}</h3>
            <span class="px-3 py-1 rounded-full text-sm font-medium ${
        match.won ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
    }">
                ${match.won ? 'Vitória' : 'Derrota'}
            </span>
        </div>
        <div class="mt-2 text-gray-600">
            <p>Data: ${new Date(match.date).toLocaleDateString()}</p>
            <p>Pontuação: ${match.score || 'N/A'}</p>
            ${match.opponent ? `<p>Oponente: ${match.opponent}</p>` : ''}
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