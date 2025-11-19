# 1. Используем официальный JDK/Maven образ для сборки
# Меняем eclipse-temurin:17-jdk на maven:3.9.6-eclipse-temurin-17
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Копируем проект, включая pom.xml
COPY . .

# Собираем jar
# Теперь мы можем использовать 'mvn' напрямую, так как он установлен
RUN mvn -q -e -DskipTests package

# 2. Lightweight образ для запуска
FROM eclipse-temurin:17-jre

WORKDIR /app


COPY --from=build /app/target/*.jar app.jar

# Порт приложения, если нужен
EXPOSE 8080

# Запуск
ENTRYPOINT ["java", "-jar", "app.jar"]
