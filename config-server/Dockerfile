# ETAPA 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copiamos solo los archivos necesarios para compilar
COPY pom.xml .
COPY src ./src

# Salteamos los tests explícitamente con true
RUN mvn clean package -DskipTests=true

# ETAPA 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV JAVA_TOOL_OPTIONS="-Xmx256m -Xms128m"

# Nota: El puerto expuesto es informativo.
# Config Server usará 8081 y Eureka usará 8761 según sus respectivos application.yml y el docker-compose.
EXPOSE 8081 8761

ENTRYPOINT ["java", "-jar", "app.jar"]