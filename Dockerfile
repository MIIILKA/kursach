# Етап 1: Збірка
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
# Копіюємо вміст поточної папки (де лежить Dockerfile) у /app
COPY . .
# Тепер ми в /app, де лежить pom.xml, тому просто запускаємо збірку
RUN mvn clean package -DskipTests

# Етап 2: Запуск
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Копіюємо jar з етапу збірки. Він буде в папці target/
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-Dserver.port=${PORT:8080}", "-jar", "app.jar"]