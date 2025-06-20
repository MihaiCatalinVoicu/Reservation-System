# Dockerfile pentru backend Spring Boot
FROM openjdk:21-jdk-slim

# Setează directorul de lucru
WORKDIR /app

# Copiază fișierul JAR al aplicației
COPY target/*.jar app.jar

# Expune portul 8080
EXPOSE 8080

# Comanda pentru a rula aplicația
ENTRYPOINT ["java", "-jar", "/app/app.jar"] 