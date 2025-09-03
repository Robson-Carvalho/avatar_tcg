let isLogin = true;

function toggleAuth() {
  isLogin = !isLogin;
  document.getElementById("authTitle").innerText = isLogin
    ? "Login"
    : "Cadastro";
  document.getElementById("authBtn").innerText = isLogin
    ? "Entrar"
    : "Cadastrar";

  document.getElementById("toggleText").innerText = isLogin
    ? "Não tem conta?"
    : "Já tem uma conta?";
  document.getElementById("toggleBtn").innerText = isLogin
    ? "Cadastre-se"
    : "Login";

  document.getElementById("name").classList.toggle("hidden", isLogin);
  document.getElementById("nickname").classList.toggle("hidden", isLogin);
  document
    .getElementById("confirmPassword")
    .classList.toggle("hidden", isLogin);

  hideError();
}

async function handleAuth() {
  hideError();

  const email = document.getElementById("email").value.trim();
  const password = document.getElementById("password").value.trim();

  if (!email || !password) {
    showError("Preencha todos os campos!");
    return;
  }

  let bodyData = { email, password };
  let endpoint = "/auth/login";

  if (!isLogin) {
    const name = document.getElementById("name").value.trim();
    const nickname = document.getElementById("nickname").value.trim();
    const confirmPassword = document
      .getElementById("confirmPassword")
      .value.trim();

    if (!name || !nickname || !confirmPassword) {
      showError("Preencha todos os campos do cadastro!");
      return;
    }

    if (password !== confirmPassword) {
      showError("As senhas não conferem!");
      return;
    }

    bodyData = { name, nickname, email, password };
    endpoint = "/auth/register";
  }

  try {
    const response = await fetch(`${API_URL}${endpoint}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(bodyData),
    });

    const data = await response.json();

    if (!response.ok) {
      showError(data.error || "Erro desconhecido");
      return;
    }

    if (data.token) {
      localStorage.setItem("token", data.token);
      localStorage.setItem("avatar_tcg_user_id", data.user.id);
      localStorage.setItem("avatar_tcg_deck_id", data.deck_id);
      localStorage.setItem("avatar_tcg_user_name", data.user.name);
      localStorage.setItem("avatar_tcg_user_nickname", data.user.nickname);
      document.getElementById("auth").classList.add("hidden");
      document.getElementById("dashboard").classList.remove("hidden");

      showMatches();
    }
  } catch {
    showError("Falha de conexão com a API!");
  }
}

function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("avatar_tcg_user_id");
  localStorage.removeItem("avatar_tcg_deck_id");
  localStorage.removeItem("avatar_tcg_user_name");
  localStorage.removeItem("avatar_tcg_user_nickname");
  document.getElementById("dashboard").classList.add("hidden");
  document.getElementById("auth").classList.remove("hidden");
}

window.onload = () => {
  const token = localStorage.getItem("token");

  if (token) {
    document.getElementById("userName").innerText = localStorage
      .getItem("avatar_tcg_user_name")
      .split(" ")[0];
    document.getElementById("auth").classList.add("hidden");
    document.getElementById("dashboard").classList.remove("hidden");
  }
};
