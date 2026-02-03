# Paso 1: Compilación (Maven)
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
# Copiamos el pom y el código fuente
COPY pom.xml .
COPY src ./src
# Ejecutamos el empaquetado (esto crea la carpeta target en la nube)
RUN mvn clean package -DskipTests

# Paso 2: Ejecución (Runtime)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Copiamos el JAR desde la etapa de compilación anterior
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Comando para iniciar la app usando el puerto de Railway
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]