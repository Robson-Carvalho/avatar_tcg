# Usa imagem oficial do Nginx
FROM nginx:alpine

# Remove configuração default
RUN rm /etc/nginx/conf.d/default.conf

# Copia configuração customizada do Nginx
COPY nginx.conf /etc/nginx/conf.d/

# Copia todo o frontend para o container
COPY . /usr/share/nginx/html

# Expondo porta 80
EXPOSE 80

# Comando padrão do Nginx
CMD ["nginx", "-g", "daemon off;"]
