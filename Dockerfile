# Etapa 1: Build
FROM eclipse-temurin:24-jdk AS build

RUN apt-get update && apt-get install -y maven

WORKDIR /app
COPY pom.xml ./
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:24-jre

WORKDIR /app

# Variáveis de ambiente padrão
# 10.0.0.151 IP dinâmico - Alterar para o Endereço IPv4 do host do postgres
ENV DB_URL=jdbc:postgresql://10.0.0.151:5432/avatar_tcg
ENV DB_USER=postgres
ENV DB_PASSWORD=postgres
ENV JWT_SECRET=016998dad6c68fea05b9df7f6f56fb96

COPY --from=build /app/target/avatar_tcg-*.jar app.jar

CMD ["java", "-jar", "app.jar"]