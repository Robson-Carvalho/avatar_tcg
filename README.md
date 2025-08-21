 <header>
      <h1 align="center">⚔️ Avatar TCG: Online ⚔️</h1>
      <p class="lead" align="center">
        Um jogo de cartas online inspirado no universo de <b>Avatar: A Lenda de Aang</b>, onde mestres dobradores
        enfrentam batalhas estratégicas em tempo real para provar quem é o verdadeiro Avatar.
      </p>
      <div class="badges" align="center">
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
        <li><a href="#instalacao">Instalação e Execução</a></li>
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
          <h4>1) Monte seu Deck</h4>
          <p>Selecione cartas que criem <strong>sinergias elementais</strong> e <strong>curva de custo</strong> equilibrada. Combine defesas, ataques e táticas de controle.</p>
          <h4>2) Entre no Duelo</h4>
          <p>Partidas <strong>1v1 em tempo real</strong>. Cada turno exige decisões: invocar, defender ou pular turno.</p>
          <h4>3) Use Subdobras</h4>
          <p>Subdobras adicionam camadas estratégicas (Metal, Relâmpago, Sangue). São raras, mas decisivas.</p>
        </div>
      </div>
    </section>
    <section id="tecnologias">
      <h2>🛠️ Tecnologias</h2>
      <ul>
        <li>Servidor HTTP/WebSocket: <code>Java</code> (OAK Server)</li>
        <li>Banco de dados: <code>PostgreSQL</code></li>
      </ul>
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


