# Food Delivery Service

Минимальная инструкция по подготовке БД и запуску тестов без вспомогательных скриптов.

## Требования
- Java 17
- PostgreSQL 12+
- Maven 3.9+
- Проект без Spring (plain JDBC + Lombok)

## Подготовка базы данных (однократно)
1. Создайте пользователя и базу (пример с дефолтными учетными данными):
   ```bash
   psql -U postgres -c "CREATE USER fooddelivery_user WITH PASSWORD 'fooddelivery_pass';"
   psql -U postgres -c "CREATE DATABASE food_delivery OWNER fooddelivery_user;"
   ```
2. Примените основную схему:
   ```bash
   PGPASSWORD=fooddelivery_pass psql -U fooddelivery_user -d food_delivery -f src/main/resources/sql/007_main_schema.sql
   ```
   При необходимости выполните отдельные файлы из `src/main/resources/sql/004_create_order_tables/` (payments, адрес, delivery_time).

## Настройка подключения
По умолчанию используется `jdbc:postgresql://localhost:5432/food_delivery`, пользователь `fooddelivery_user`, пароль `fooddelivery_pass`.
Переопределение:
- Переменные окружения: `DB_URL`, `DB_USER`, `DB_PASSWORD`
- JVM-параметры: `-Ddb.url=... -Ddb.user=... -Ddb.password=...`

## Запуск тестов вручную
1. Собрать проект без тестов (поможет заранее выявить проблемы компиляции):
   ```bash
   mvn -DskipTests compile
   ```
2. Запустить нужные наборы (указывайте свои креды при необходимости):
   ```bash
   mvn test -Ddb.user=fooddelivery_user -Ddb.password=fooddelivery_pass -Dtest=ClientRepositoryTest,CartRepositoryTest
   mvn test -Ddb.user=fooddelivery_user -Ddb.password=fooddelivery_pass -Dtest=ShopProductIntegrationTest
   mvn test -Ddb.user=fooddelivery_user -Ddb.password=fooddelivery_pass -Dtest=OrderCourierIntegrationTest
   mvn test -Ddb.user=fooddelivery_user -Ddb.password=fooddelivery_pass -Dtest=OrderInteractionIntegrationTest
   ```
   Тестовые данные не очищаются, поэтому прогоны можно повторять поочередно или выборочно.

## Полезное
- Проверка подключения: `./check_db_connection.sh`
- Запуск схемы целиком: `./run_scheme.sh` (если нужна полная переинициализация)

## Очистка/обновление БД перед повторным запуском
Если нужно сбросить данные и применить схему заново:
1. Остановите приложение/тесты, чтобы соединения не держали блокировки.
2. Выполните полный скрипт:
   ```bash
   PGPASSWORD=fooddelivery_pass psql -U fooddelivery_user -d food_delivery -f src/main/resources/sql/000_drop_tables.sql
   PGPASSWORD=fooddelivery_pass psql -U fooddelivery_user -d food_delivery -f src/main/resources/sql/007_main_schema.sql
   ```
   При необходимости повторно прогоните отдельные файлы из `src/main/resources/sql/004_create_order_tables/`.
3. Запустите приложение или нужные тесты с теми же параметрами подключения.
