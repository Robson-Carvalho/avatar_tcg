docker stop postgres-avatar &&
docker rm postgres-avatar &&
docker volume rm postgres_data &&
docker run -d  --name postgres-avatar -p 5432:5432 -e POSTGRES_DB=avatar_tcg -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -v postgres_data:/var/lib/postgresql/data postgres:15

docker stop avatar-server &&
docker rm -f avatar-server &&
docker build -t avatar-tcg-server . &&
docker run -d --name avatar-server -p 8080:8080 -e JWT_SECRET=4ab47e54c2f73ad4c0eb3974709721cd -e HOST_SERVER=10.0.0.151 avatar-tcg-server 

docker stop avatar-client &&
docker rm -f avatar-client &&
docker build --pull --no-cache -t avatar-tcg-client ./client &&
docker run -d --name avatar-client -p 3000:3000 avatar-tcg-client


### Limpar tudo
# Remove container se existir
docker rm -f avatar-tcg-client 2>/dev/null || true

# Remove imagem se existir
docker rmi -f avatar-tcg-client 2>/dev/null || true

# Limpa cache de build
docker builder prune -f

# Build fresco
docker build --pull --no-cache -t avatar-tcg-client .

# Verifica a imagem
docker images | grep avatar-tcg-client