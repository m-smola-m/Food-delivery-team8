# Food Delivery (Maven Spring Boot) — каркас
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
