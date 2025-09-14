<h1 align="center">Avatar TCG 💾</h1>

<div align="center">
    <img src="https://img.shields.io/badge/status-active-success.svg" />
    <img src="https://img.shields.io/badge/multiplayer-1v1-blue.svg" />
    <img src="https://img.shields.io/badge/status-active-success.svg" />
</div>

<br/>

### 📖 Sobre o Projeto

Avatar TCG é um jogo de cartas digital onde jogadores competem em partidas 1v1, utilizando estratégias baseadas nos elementos do universo de Avatar: A Lenda de Aang. O jogo foi desenvolvido com foco em desempenho e escalabilidade, utilizando WebSocket para comunicação em tempo real, ThreadPool para otimização de tarefas concorrentes e técnicas de sincronização para evitar race conditions, garantindo uma experiência fluida e justa.

### 🚧 Pré-requisitos

Antes de começar, certifique-se de ter instalado em sua máquina:

- Docker
- Git
- Python 3.8+ (para testes de carga)

### 📝 Detalhes

Lorem

### 📥 Clonando o Repositório

```bash
git clone https://github.com/Robson-Carvalho/avatar_tcg.git
cd avatar_tcg
```

### 🚀 Executando a Aplicação

O projeto é composto por três componentes principais: o banco de dados PostgreSQL, o servidor backend e o cliente frontend. Siga os passos abaixo para configurar e executar cada um.

**1. Banco de Dados PostgreSQL**

Inicie um contêiner Docker para o banco de dados PostgreSQL:

```bash
docker run -d --name postgres-avatar -p 5432:5432 \
  -e POSTGRES_DB=avatar_tcg \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -v postgres_data:/var/lib/postgresql/data \
  postgres:15
```

**2. Servidor Backend**

Construa e execute o contêiner para o servidor backend na mesma máquina do container do banco de dados:

```bash
docker build -t avatar-tcg-server .
docker run -d --name avatar-server -p 8080:8080 \
  -e JWT_SECRET=4ab47e54c2f73ad4c0eb3974709721cd \
  -e HOST_SERVER=10.0.0.151 \
  avatar-tcg-server
```

> _"**Nota**: Substitua **`10.0.0.151`** pelo IP da máquina que está executando o servidor, se necessário."_

**3. Cliente Frontend**

Construa e execute o contêiner para o cliente frontend:

> _"**Nota**: No arquivo ./client/src/scripts/env.js, substitua o **`IP_SERVER`** atual pelo IP da máquina que está executando o servidor, se necessário."_

```js
const IP_SERVER = "10.0.0.151" # altere para o IP da máquina que está executando o servidor
```

```bash
docker build -t avatar-tcg-client ./client
docker run -d --name avatar-client -p 3000:3000 avatar-tcg-client
```

Após executar esses comandos, o jogo estará acessível em `http://localhost:3000`.

### 🛑 Parando todos os serviços

Para interromper e remover todos os contêineres, execute:

```bash
docker stop postgres-avatar &&
docker rm -fpostgres-avatar &&
docker stop avatar-server &&
docker rm -f avatar-server &&
docker stop avatar-client &&
docker rm -f avatar-client &&
```

### 📊 Execução de Testes de Carga

Os testes de carga ajudam a avaliar o desempenho do servidor sob alta demanda. Para executá-los, siga os passos abaixo:

1. Navegue até o diretório de testes de estresse:

```bash
cd avatar_tcg/stress
```

2. Crie e ative um ambiente virtual Python:

```bash
python3 -m venv venv

source venv/Scripts/activate 
# ou para linux
source venv/bin/activate
```

3. Instale as dependências necessárias:

```bash
pip install requests websocket-client
```

4. Configure o IP do servidor no arquivo `stress/main.py` para corresponder ao IP da máquina que está executando o servidor backend (definido em `HOST_SERVER`).
5. Execute os testes de carga:

```bash
python main.py
```

> _**Atenção**: Certifique-se de que o IP do servidor esteja corretamente configurado no arquivo `main.py` antes de executar os testes._ 6. Para sair do ambiente venv execute:

```bash
deactivate
```

### 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. Faça um fork do repositório.
2. Crie uma branch para sua feature: `git checkout -b minha-feature`.
3. Commit suas alterações: `git commit -m 'Adiciona minha feature'`.
4. Envie para o repositório remoto: `git push origin minha-feature`.
5. Abra um Pull Request.

### 📜 Licença

Este projeto é licenciado sob a Licença MIT - veja o arquivo LICENSE para mais detalhes.

### 📬 Contato

Para dúvidas ou sugestões, entre em contato com Robson Carvalho ou abra uma issue no repositório.
