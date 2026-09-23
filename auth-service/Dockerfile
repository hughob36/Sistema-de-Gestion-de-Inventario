# ETAPA 1: Build y ejecución de tests
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copiamos el archivo de dependencias y el código fuente
COPY pom.xml .
COPY src ./src

# Compilamos y ejecutamos los tests automáticamente
RUN mvn clean package

# ETAPA 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiamos el JAR generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Configuramos variables de entorno para memoria
ENV JAVA_TOOL_OPTIONS="-Xmx256m -Xms128m"

# Puerto informativo general para los servicios de negocio
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]