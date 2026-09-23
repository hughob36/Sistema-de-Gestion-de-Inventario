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

# Copiamos el JAR generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Configuramos variables de entorno para memoria
ENV JAVA_TOOL_OPTIONS="-Xmx256m -Xms128m"

# Puerto en el que correrá el gateway según su configuración
EXPOSE 443

ENTRYPOINT ["java", "-jar", "app.jar"]