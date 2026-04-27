# Етап 1: Збірка
FROM maven:3.8.5-openjdk-17 AS build
COPY . /app
# ЗМІНЕНО: заходимо в папку, де реально лежить pom.xml
WORKDIR /app/kursach_final
RUN mvn clean package -DskipTests

# Етап 2: Запуск
FROM eclipse-temurin:17-jdk-alpine
# ЗМІНЕНО: шлях до jar файлу тепер теж враховує підпапку
COPY --from=build /app/kursach_final/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-Dserver.port=${PORT:8080}", "-jar", "/app.jar"]