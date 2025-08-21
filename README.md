<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <title>⚔️ Avatar TCG: Online ⚔️</title>

  <!-- Meta social (ajuda no SEO/preview do GitHub e redes) -->
  <meta name="description" content="Avatar TCG: Online — um jogo de cartas 1v1 em tempo real inspirado no universo de Avatar: A Lenda de Aang. Construa decks, domine subdobras e torne-se o Avatar." />
  <meta property="og:title" content="⚔️ Avatar TCG: Online ⚔️" />
  <meta property="og:description" content="Jogo de cartas online 1v1 em tempo real inspirado em Avatar: A Lenda de Aang." />
  <meta property="og:type" content="website" />
  <meta property="og:image" content="https://raw.githubusercontent.com/your-user/your-repo/main/.github/banner.png" />
  <meta name="theme-color" content="#0ea5e9" />

  <!-- Estilos mínimos e elegantes -->
  <style>
    :root{
      --bg:#0b1220;
      --card:#0f172a;
      --muted:#94a3b8;
      --text:#e2e8f0;
      --brand:#38bdf8;
      --accent:#f59e0b;
      --success:#22c55e;
      --warn:#f97316;
      --danger:#ef4444;
      --shadow:0 10px 25px rgba(0,0,0,.25);
      --radius:18px;
    }
    *{box-sizing:border-box}
    body{
      margin:0;
      font-family: ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, Ubuntu, Cantarell, Noto Sans, "Helvetica Neue", Arial, "Apple Color Emoji","Segoe UI Emoji";
      background: radial-gradient(1200px 800px at 10% 10%, rgba(56,189,248,.08), transparent 50%),
                  radial-gradient(1200px 800px at 90% 10%, rgba(245,158,11,.08), transparent 50%),
                  linear-gradient(180deg, #0b1220 0%, #0b1220 100%);
      color: var(--text);
      line-height:1.6;
    }
    .wrap{max-width:1000px;margin:auto;padding:32px 18px 80px}
    header{
      text-align:center;
      padding:26px 18px 10px;
    }
    h1{
      margin:0 0 8px;
      font-size: clamp(28px, 4vw, 44px);
      letter-spacing:.5px;
    }
    .lead{
      max-width:820px;
      margin:10px auto 0;
      color:var(--muted);
    }
    .badges{
      margin:16px 0 4px;
      display:flex;gap:10px;justify-content:center;flex-wrap:wrap
    }
    .badges img{height:20px}
    nav.toc{
      margin:22px auto 26px;
      background:rgba(15,23,42,.6);
      border:1px solid rgba(148,163,184,.2);
      box-shadow: var(--shadow);
      border-radius: var(--radius);
      padding:14px;
    }
    nav.toc ul{display:flex;flex-wrap:wrap;gap:10px;margin:0;padding:0;list-style:none;justify-content:center}
    nav.toc a{
      text-decoration:none;
      color:var(--text);
      border:1px solid rgba(148,163,184,.25);
      padding:8px 12px;border-radius:12px;
      transition:.15s ease;
      font-size:14px
    }
    nav.toc a:hover{border-color:var(--brand); color:#fff; background:rgba(56,189,248,.12)}
    section{
      background:rgba(15,23,42,.6);
      border:1px solid rgba(148,163,184,.2);
      border-radius: var(--radius);
      padding:22px;
      margin:16px 0 22px;
      box-shadow: var(--shadow);
    }
    h2{margin-top:0;font-size:clamp(20px,3vw,26px)}
    h3{margin-bottom:6px}
    p{margin:10px 0}
    ul{margin:8px 0 0 18px}
    code, pre{
      background:#0a0f1a;border:1px solid rgba(148,163,184,.2);
      border-radius:12px;
    }
    code{padding:2px 6px}
    pre{padding:14px; overflow:auto}
    .grid{display:grid;gap:14px}
    @media(min-width:820px){.grid-2{grid-template-columns:1fr 1fr}}
    table{
      width:100%; border-collapse:separate; border-spacing:0; margin-top:8px;
      border:1px solid rgba(148,163,184,.2); border-radius:14px; overflow:hidden
    }
    th, td{padding:12px 14px; text-align:left}
    thead{background:rgba(148,163,184,.12)}
    tbody tr:nth-child(odd){background:rgba(148,163,184,.06)}
    .callout{
      border-left:4px solid var(--brand);
      background:rgba(56,189,248,.08);
      padding:12px 14px; border-radius:12px; margin:10px 0
    }
    .quote{
      font-style:italic; color:#cbd5e1; padding:12px 16px; border-left:4px solid var(--accent);
      background:rgba(245,158,11,.08); border-radius:12px
    }
    footer{opacity:.7; text-align:center; margin-top:28px; font-size:14px}
    .kbd{
      font-family: ui-monospace,SFMono-Regular,Menlo,Monaco,Consolas,"Liberation Mono","Courier New",monospace;
      font-size:12px; padding:2px 6px; background:#0a0f1a; border:1px solid rgba(148,163,184,.25); border-radius:6px
    }
  </style>
</head>
<body>
  <div class="wrap">
    <header>
      <h1 align="center">⚔️ Avatar TCG: Online ⚔️</h1>
      <p class="lead" align="center">
        Um jogo de cartas online inspirado no universo de <b>Avatar: A Lenda de Aang</b>, onde mestres dobradores
        enfrentam batalhas estratégicas em tempo real para provar quem é o verdadeiro Avatar.
      </p>
      <div class="badges">
        <img alt="License" src="https://img.shields.io/badge/license-MIT-f59e0b" />
        <img alt="Contribuições" src="https://img.shields.io/badge/contribuições-bem--vindas-8b5cf6" />
      </div>
    </header>
    <nav class="toc" aria-label="Navegação">
      <ul>
        <li><a href="#sobre-o-jogo">Sobre</a></li>
        <li><a href="#colecao-e-progressao">Coleção</a></li>
        <li><a href="#objetivo">Objetivo</a></li>
        <li><a href="#como-jogar">Como Jogar</a></li>
        <li><a href="#tecnologias">Tecnologias</a></li>
        <li><a href="#instalacao">Instalação</a></li>
        <li><a href="#exemplos-de-cartas">Exemplos de Cartas</a></li>
        <li><a href="#futuras-funcionalidades">Roadmap</a></li>
        <li><a href="#contribuicao">Contribuição</a></li>
        <li><a href="#licenca">Licença</a></li>
        <li><a href="#creditos">Créditos</a></li>
      </ul>
    </nav>
    <!-- === CONTEÚDO QUE VOCÊ ENVIOU (ajustado/formatado) === -->
    <section id="sobre-o-jogo">
      <h2>📜 Sobre o Jogo</h2>
      <p><strong>Avatar TCG: Online</strong> é um jogo <strong>multiplayer 1v1 em tempo real</strong>, que combina estratégia, coleção de cartas e o poder elemental dos quatro reinos.
        Os jogadores devem montar decks únicos e usar suas habilidades de dobra para superar seus oponentes em duelos intensos.</p>
      <p>Cada carta representa guerreiros, mestres e técnicas inspiradas nas artes de dobra, permitindo combinações criativas e estratégias imprevisíveis.</p>
      <div class="callout">
        <strong>Destaques:</strong> PvP em tempo real · Sinergias elementais · Subdobras raras · Progressão baseada em coleção
      </div>
    </section>
    <section id="colecao-e-progressao">
      <h2>🃏 Coleção e Progressão</h2>
      <ul>
        <li>Expanda sua coleção adquirindo pacotes de cartas.</li>
        <li>Raridades distintas: <strong>Comum</strong>, <strong>Rara</strong>, <strong>Épica</strong> e <strong>Lendária</strong>.</li>
        <li>Subdobras especiais como <em>Dobra de Metal</em>, <em>Relâmpago</em> e <em>Sangue</em> surgem em cartas exclusivas.</li>
      </ul>
      <table aria-label="Tabela de raridades">
        <thead>
          <tr><th>Raridade</th><th>Probabilidade (sugestão)</th><th>Exemplo</th></tr>
        </thead>
        <tbody>
          <tr><td>Comum</td><td>~70%</td><td>Soldado da Tribo da Água</td></tr>
          <tr><td>Rara</td><td>~20%</td><td>Guardião da Nação do Fogo</td></tr>
          <tr><td>Épica</td><td>~9%</td><td>Mestre da Terra (Metal)</td></tr>
          <tr><td>Lendária</td><td>~1%</td><td>Mestre do Relâmpago / Mestre de Sangue</td></tr>
        </tbody>
      </table>
      <p style="color:var(--muted);margin-top:6px">As probabilidades podem variar conforme balanceamento de temporada.</p>
    </section>
    <section id="objetivo">
      <h2>🏆 Objetivo</h2>
      <ul>
        <li><strong>Seu destino é se tornar o Avatar:</strong></li>
        <li>Domine todos os quatro elementos.</li>
        <li>Construa estratégias imprevisíveis.</li>
        <li>Desafie jogadores em batalhas épicas.</li>
        <li>Prove que você é o maior dobrador do mundo!</li>
      </ul>
      <div class="quote">"Elementos, eu vos saúdo... Agora, vamos duelar!" 🌊⛰️🔥🌪️</div>
    </section>
    <section id="como-jogar">
      <h2>🎮 Como Jogar</h2>
      <div class="grid grid-2">
        <div>
          <h3>1) Monte seu Deck</h3>
          <p>Selecione cartas que criem <strong>sinergias elementais</strong> e <strong>curva de custo</strong> equilibrada. Combine defesas, ataques e táticas de controle.</p>
          <h3>2) Entre no Duelo</h3>
          <p>Partidas <strong>1v1 em tempo real</strong>. Cada turno exige decisões rápidas: invocar, defender, usar habilidades de dobra ou preparar um combo.</p>
          <h3>3) Use Subdobras</h3>
          <p>Subdobras adicionam camadas estratégicas (Metal, Relâmpago, Sangue). São raras, mas decisivas.</p>
        </div>
        <div>
          <h3>Regras Essenciais</h3>
          <ul>
            <li>Vitória ao reduzir os <em>pontos de vida</em> do oponente a 0.</li>
            <li><span class="kbd">Iniciativa</span> alterna por turno para manter o ritmo justo.</li>
            <li>Cartas de <em>técnica</em> podem alterar o tabuleiro temporariamente.</li>
            <li>Efeitos de estado: <em>Queimadura</em>, <em>Congelamento</em>, <em>Pedrificação</em>, <em>Turbilhão</em>.</li>
          </ul>
        </div>
      </div>
    </section>
    <section id="tecnologias">
      <h2>🛠️ Tecnologias (exemplo)</h2>
      <ul>
        <li>Servidor HTTP/WebSocket: <code>Java</code> (OAK Server) • Conexões em tempo real</li>
        <li>Banco de dados: <code>PostgreSQL</code></li>
        <li>Cliente: <code>React</code> + <code>TypeScript</code> + <code>Tailwind</code></li>
        <li>Build/Dev: <code>Vite</code> • Testes: <code>Jest</code></li>
      </ul>
      <p class="callout">Adapte esta seção às libs que você realmente usa no projeto.</p>
    </section>
    <section id="instalacao">
      <h2>⚙️ Instalação & Execução</h2>
      <h3>Pré-requisitos</h3>
      <ul>
        <li><code>Node.js</code> 18+ (cliente)</li>
        <li><code>Java 17+</code> (servidor)</li>
        <li><code>PostgreSQL</code> 14+ (ou compatível)</li>
      </ul>
      <h3>Clonar o repositório</h3>
      <pre><code>git clone https://github.com/SEU-USUARIO/SEU-REPO.git
cd SEU-REPO</code></pre>

<h3>Servidor</h3>
<pre>
    <code>
# configure variáveis de ambiente (exemplo)
export DB_URL=jdbc:postgresql://localhost:5432/avatar_tcg
export DB_USER=postgres
export DB_PASS=postgres
    </code>
</pre>

<h3>Cliente</h3>
<pre><code>cd client
npm install
npm run dev   # desenvolvimento
npm run build # produção
npm run preview
</code></pre>
<p style="color:var(--muted)">Verifique as portas e a URL do WebSocket (ex.: <code>ws://localhost:8080/game</code>) nas configs do cliente.</p>
</section>
<section id="exemplos-de-cartas">
      <h2>🧩 Exemplos de Cartas</h2>
      <div class="grid grid-2">
        <div>
          <pre><code>{
"name": "Soldado da Tribo da Água",
"element": "WATER",
"phase": "YOUNG",
"attack": 50,
"life": 55,
"defense": 45,
"rarity": "COMMON",
"description": "Um soldado iniciante da tribo da água..."
}</code></pre>
</div>
<div>
<pre><code>{
"name": "Mestre do Relâmpago",
"element": "FIRE",
"phase": "MASTER",
"attack": 95,
"life": 70,
"defense": 40,
"rarity": "LEGENDARY",
"description": "Canaliza descargas elétricas com precisão..."
}</code></pre>
</div>
</div>
<p class="callout">Sugestão: adicione imagens/arte das cartas em <code>/assets/cards</code> e um banner em <code>/.github/banner.png</code>.</p>
</section>
    <section id="futuras-funcionalidades">
      <h2>🚀 Futuras Funcionalidades</h2>
      <ul>
        <li>🎮 Sistema ranqueado competitivo</li>
        <li>🌍 Eventos temáticos sazonais</li>
        <li>🧩 Modo história inspirado na série</li>
        <li>🤝 Troca de cartas entre jogadores</li>
      </ul>
    </section>
    <section id="contribuicao">
      <h2>🤝 Contribuição</h2>
      <ol>
        <li>Faça um fork do repositório</li>
        <li>Crie uma branch: <code>git checkout -b feat/sua-feature</code></li>
        <li>Commite suas mudanças: <code>git commit -m "feat: adiciona sua-feature"</code></li>
        <li>Envie a branch: <code>git push origin feat/sua-feature</code></li>
        <li>Abra um Pull Request</li>
      </ol>
      <p class="callout">Issues de melhorias, bugs e balanceamento são muito bem-vindas!</p>
    </section>
    <section id="licenca">
      <h2>📄 Licença</h2>
      <p>Este projeto está licenciado sob a licença <strong>MIT</strong>. Consulte o arquivo <code>LICENSE</code> para detalhes.</p>
    </section>
    <section id="creditos">
      <h2>💡 Inspiração & Créditos</h2>
      <p>Inspirado no universo de <strong>Avatar: A Lenda de Aang</strong>.</p>
      <p style="color:var(--muted)"><em>Disclaimer:</em> Projeto de fã, sem afiliação oficial a Nickelodeon, Viacom ou titulares dos direitos. Todos os nomes, marcas e elementos relacionados pertencem aos seus respectivos proprietários.</p>
    </section>
    <footer>
      Feito com 💙 por apaixonados por jogos de cartas e pela arte da dobra.
    </footer>
  </div>
</body>
</html>
