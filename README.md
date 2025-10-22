# Food Delivery (Maven Spring Boot) — каркас

Минимальный шаблон проекта: Maven + Spring Boot со стартовым классом. Готов для дальнейшего расширения.

## Требования
- Java 17+
- Maven 3.9+

## Структура
```
food-delivery-team8/
  ├─ pom.xml
  └─ src/
     ├─ main/
     │  ├─ java/
     │  │  └─ com/team8/fooddelivery/
     │  │     └─ FoodDeliveryApplication.java
     │  └─ resources/
     └─ test/
```

## Запуск
```bash
mvn spring-boot:run
```

или сборка jar и запуск:
```bash
mvn clean package
java -jar target/food-delivery-0.0.1-SNAPSHOT.jar
```

## Дальнейшие шаги
- Добавить доменные сущности, репозитории и сервисы по требованиям.
- Подключить БД (H2/PostgreSQL) и настройки в `application.yml`.
- Реализовать REST-контроллеры для клиентов, курьеров и ресторанов.
