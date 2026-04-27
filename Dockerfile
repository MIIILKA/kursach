# Етап 1: Збірка (Build stage)
FROM maven:3.8.5-openjdk-17 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# Етап 2: Запуск (Rаun stage)
FROM openjdk:17-jdk-slim
COPY --from=build /app/target/*.jar app.jar

# Render динамічно призначає порт, тому додаємо його у команду запуску
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-Dserver.port=${PORT:8080}", "-jar", "/app.jar"]