# ETAPA 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copiamos solo los archivos necesarios para compilar
COPY pom.xml .
COPY src ./src

# Salteamos los tests explicitamente con true
RUN mvn clean package -DskipTests=true

# ETAPA 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV JAVA_TOOL_OPTIONS="-Xmx256m -Xms128m"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]