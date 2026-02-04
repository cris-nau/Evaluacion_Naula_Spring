# Paso 1: Compilación
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Paso 2: Ejecución
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# --- CORRECCIÓN ---
# Quitamos los corchetes para que ${PORT} funcione correctamente
ENTRYPOINT java -Dserver.port=${PORT} -jar app.jar