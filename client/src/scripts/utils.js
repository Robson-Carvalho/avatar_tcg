const API_URL = "http://localhost:3000";

function testAlert() {
    alert("test")
}

function showError(message) {
    const errorDiv = document.getElementById("errorMsg");
    if (!errorDiv) return;
    errorDiv.innerText = message;
    errorDiv.classList.remove("hidden");
}

function hideError() {
    const errorDiv = document.getElementById("errorMsg");
    if (!errorDiv) return;
    errorDiv.innerText = "";
    errorDiv.classList.add("hidden");
}

function renderCards(cards, div) {
    const container = document.getElementById(div);
    container.innerHTML = ""; 
    
    cards.forEach(card => {
        const cardElement = document.createElement("div");
        cardElement.className = "card bg-white rounded-lg shadow-md p-4 flex flex-col items-center";
        
        cardElement.innerHTML = `
            <div class="w-24 h-32 bg-gray-200 rounded-md mb-2 flex items-center justify-center">
                <span class="text-gray-500">${card.name}</span>
            </div>
            <h3 class="font-bold text-center">${card.name}</h3>
            <p class="text-sm text-gray-600 mt-1">Elemento: ${card.element}</p>
            <p class="text-sm text-gray-600 mt-1">Fase: ${card.phase}</p>
            <p class="text-sm text-gray-600 mt-1">Ataque: ${card.attack}</p>
            <p class="text-sm text-gray-600 mt-1">Vida: ${card.life}</p>
            <p class="text-sm text-gray-600 mt-1">Defesa: ${card.defense}</p>
            <p class="text-sm text-gray-500 mt-1">${card.rarity}</p>
        `;
        container.appendChild(cardElement);
    });
}

