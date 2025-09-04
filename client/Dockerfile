# Usa Node.js leve
FROM node:18-alpine

# Define diretório de trabalho
WORKDIR /usr/src/app

# Copia todo o código para dentro do container
COPY . .

# Expõe a porta (se necessário)
EXPOSE 3000

# Comando para rodar o script
CMD ["node", "server.js"]
