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

# Copiar o JAR com nome explícito (ajuste conforme o nome gerado)
COPY --from=build /app/target/avatar_tcg-*.jar app.jar

CMD ["java", "-jar", "app.jar"]