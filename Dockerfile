# Етап 1: Збірка (Build stage)
FROM maven:3.8.5-openjdk-17 AS build
COPY . /app
WORKDIR /app
# Використовуємо -f для вказівки шляху до pom.xml, якщо він у папці kursach
RUN mvn clean package -DskipTests

# Етап 2: Запуск (Run stage)
# ЗАМІНЕНО: використовуємо актуальний образ Eclipse Temurin
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /app/target/*.jar app.jar

# Налаштування для Render
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-Dserver.port=${PORT:8080}", "-jar", "/app.jar"]