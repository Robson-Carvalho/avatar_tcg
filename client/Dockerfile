
# Usa a imagem oficial do Node.js
FROM node:18-alpine

# Define o diretório de trabalho dentro do container
WORKDIR /usr/src/app

# Copia package.json (se houver) e instala dependências
COPY package*.json ./
RUN npm install || true

# Copia todo o restante do código (incluindo src)
COPY . .

EXPOSE 3000

# Comando para executar o script principal
CMD ["node", "server.js"]
