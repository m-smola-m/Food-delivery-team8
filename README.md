# Food Delivery Service

Минимальная инструкция по подготовке БД и запуску проекта как с фронтендом (JSP) так и без него.

## Требования
- Java 17 (JDK)
- Maven 3.9+
- PostgreSQL 12+
- Docker (опционально, для быстрой БД)

## TL;DR
| Цель | Команда |
|------|---------|
| Поднять БД | `docker compose up`
| Накатить схему(опционально, если не сработает) | `PGPASSWORD=postgres psql -h localhost -U postgres -d food_delivery -f src/main/resources/sql/007_main_schema.sql`
| Собрать | `mvn clean package`
| **Запустить с фронтом** | `java -jar target/food-delivery-0.0.1-SNAPSHOT.jar`
| **Запустить только API/JDBC** | `mvn exec:java -Dexec.mainClass=com.team8.fooddelivery.MainApplication` *(или используйте сервлеты через IDE/Tomcat)*

## Подготовка базы данных (на случай, если что-то пойдет не так)
1. Создайте пользователя и БД (если не пользуетесь compose):
   ```bash
   psql -U postgres -c "CREATE USER postgres WITH PASSWORD 'postgres';"
   psql -U postgres -c "CREATE DATABASE food_delivery OWNER postgres;"
   ```
   > Все дальнейшие SQL скрипты **выполняйте под `postgres`**, чтобы объекты сразу создавались нужным владельцем. Если раньше таблицы создавались под `postgres`, выровняйте владельцев, иначе тесты не смогут выполнять `DROP TABLE`:
   ```bash
   psql -U postgres -d food_delivery -c "ALTER DATABASE food_delivery OWNER TO postgres;"
   psql -U postgres -d food_delivery -c "REASSIGN OWNED BY postgres TO postgres;"
   psql -U postgres -d food_delivery -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;"
   psql -U postgres -d food_delivery -c "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;"
   ```
2. Примените основную схему:
   ```bash
   PGPASSWORD=postgres psql -h localhost -U postgres -d food_delivery -f src/main/resources/sql/007_main_schema.sql
   ```
3. (Опционально) заполните тестовыми данными из `src/main/resources/sql/test_data/*.sql` в порядке нумерации.

## Запуск с фронтендом (встроенный Tomcat + JSP)
1. Убедитесь, что в `pom.xml` настроен `maven-shade-plugin` (уже включен) – он собирает исполняемый JAR с Tomcat и JSP.
2. Соберите проект:
   ```bash
   mvn clean package -DskipTests
   ```
3. Запустите приложение:
   ```bash
   # стандартный порт 8080, измените через переменную PORT, если занят
   java -jar target/food-delivery-0.0.1-SNAPSHOT.jar
   # пример запуска на 9090 при конфликте
   PORT=9090 java -jar target/food-delivery-0.0.1-SNAPSHOT.jar
   ```
   - UI доступен по `http://localhost:8080/`.
4. Доступные роли:
   - Клиент: `/login?role=CLIENT` → корзина, профиль, заказы.
   - Курьер: `/login?role=COURIER` → дашборд смены/заказов.
   - Магазин: `/login?role=SHOP` → кабинет, товары, заказы, статусы.

## Запуск только backend (без JSP)
Если требуется использовать только JDBC/сервисы (например, для интеграции или тестов):
1. Поднимите БД и примените схему (см. выше).
2. Соберите проект:
   ```bash
   mvn clean package -DskipTests
   ```
3. Используйте любой контейнер сервлетов (Tomcat/Jetty) или `mvn exec:java` для запуска `MainApplication`. Для headless режима можно отключить извлечение webapp и обращаться к сервлетам/REST напрямую, либо запускать интеграционные тесты.

## Настройка подключения
По умолчанию: `jdbc:postgresql://localhost:5432/food_delivery`, логин `postgres`, пароль `postgres`.
- Переопределение через переменные окружения: `DB_URL`, `DB_USER`, `DB_PASSWORD`.
- Или JVM-параметры: `-Ddb.url=... -Ddb.user=... -Ddb.password=...`.

## Тесты
Запускаются через Maven:
```bash
mvn test -Ddb.user=postgres -Ddb.password=postgres -Dtest=ClientRepositoryTest,CartRepositoryTest
```
(дополнительно есть интеграционные тесты для заказов/курьеров/магазинов в `src/test/java/com/team8/fooddelivery/integration`).

## Частые проблемы
- **"jakarta.servlet.jsp.jstl-api missing"** – зависит от репозитория; shade-плагин уже исключает конфликтующую версию, просто выполните `mvn dependency:purge-local-repository` при необходимости.
- **`NoClassDefFoundError: org/apache/catalina/startup/Tomcat`** – убедитесь, что используете shaded JAR из `target/food-delivery-0.0.1-SNAPSHOT.jar` после сборки.
- **Ошибка доступа к БД** – проверьте креды, что `docker compose up -d db` поднял контейнер, и применена схема.
- **UTF-8 warning** – безопасно для локальной разработки; при желании добавьте `project.build.sourceEncoding` в `pom.xml`.

## Полезные команды
```bash
# Быстро пересобрать и перезапустить фронт
mvn clean package -DskipTests && java -jar target/food-delivery-0.0.1-SNAPSHOT.jar

# Очистить БД и накатить схему заново
PGPASSWORD=postgres psql -h localhost -U postgres -d food_delivery -f src/main/resources/sql/000_drop_tables.sql
PGPASSWORD=postgres psql -h localhost -U postgres -d food_delivery -f src/main/resources/sql/007_main_schema.sql
```
