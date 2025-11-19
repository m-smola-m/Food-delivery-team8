# 1. Используем официальный JDK для сборки
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Копируем проект
COPY . .

# Собираем jar
RUN ./mvnw -q -e -DskipTests package || mvn -q -e -DskipTests package

# 2. Lightweight образ для запуска
FROM eclipse-temurin:17-jre

WORKDIR /app

# Копируем собранный jar
COPY --from=build /app/target/*.jar app.jar

# Порт приложения, если нужен
EXPOSE 8080

# Запуск
ENTRYPOINT ["java", "-jar", "app.jar"]
