docker run -d  --name postgres-avatar -p 5432:5432 -e POSTGRES_DB=avatar_tcg -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -v postgres_data:/var/lib/postgresql/data postgres:15

docker build -t avatar-tcg-app .
docker run -d --name avatar-app -p 8080:8080 avatar-tcg-app

docker build -t avatar-tcg-client .
docker run -d --name avatar-client -p 3000:3000 avatar-tcg-client

// apagar container
docker rm id_container 