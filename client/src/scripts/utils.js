const API_URL = `http://${IP_SERVER}:8080`;

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
