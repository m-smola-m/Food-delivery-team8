# Инструкция по запуску тестов

## Подготовка

Перед запуском тестов убедитесь, что:

1. **PostgreSQL установлен и запущен**
2. **База данных создана:**
   ```sql
   CREATE DATABASE food_delivery;
   ```

3. **Схема создана:**
   ```bash
   psql -U postgres -d food_delivery -f src/main/resources/schema.sql
   ```

## Настройка параметров подключения

По умолчанию тесты используют:
- URL: `jdbc:postgresql://localhost:5432/food_delivery`
- User: `postgres`
- Password: `postgres`

### Изменение параметров через системные свойства:

```bash
mvn test -Ddb.url=jdbc:postgresql://localhost:5432/food_delivery \
         -Ddb.user=your_user \
         -Ddb.password=your_password
```

## Запуск тестов

### Запуск всех тестов:
```bash
mvn test
```

### Запуск конкретного теста:
```bash
mvn test -Dtest=SimpleConnectionTest
mvn test -Dtest=DatabaseConnectionTest
mvn test -Dtest=ClientRepositoryTest
mvn test -Dtest=CartRepositoryTest
mvn test -Dtest=DatabaseIntegrationTest
```

### Запуск через IDE:
1. Откройте проект в IntelliJ IDEA или Eclipse
2. Найдите тестовый класс в папке `src/test/java`
3. Правой кнопкой -> Run Test

## Структура тестов

### 1. SimpleConnectionTest
**Самый простой тест** - проверяет только подключение к БД.
**Запустите его первым**, чтобы убедиться, что подключение работает.

### 2. DatabaseConnectionTest
Проверяет работу класса `DatabaseConnection`:
- Тестовое подключение
- Получение подключения
- Закрытие подключения
- Установка параметров

### 3. ClientRepositoryTest
Интеграционные тесты для `ClientRepository`:
- Сохранение клиента
- Поиск по ID, телефону, email
- Обновление клиента
- Получение всех клиентов
- Удаление клиента

### 4. CartRepositoryTest
Тесты для работы с корзинами:
- Создание корзины
- Добавление элементов
- Получение элементов
- Очистка корзины

### 5. DatabaseIntegrationTest
**Полные интеграционные тесты** - проверяют работу всей системы:
- Полный цикл: клиент -> корзина -> товары
- Создание магазина с продуктами
- Создание курьера и заказа

## Результаты тестов

### Успешный запуск:
```
✅ Подключение к базе данных успешно!
Tests run: X, Failures: 0, Errors: 0, Skipped: 0
```

### Ошибки подключения:
Если тесты не проходят из-за подключения, проверьте:
1. PostgreSQL запущен: `pg_isready` или `systemctl status postgresql`
2. База данных существует: `psql -l | grep food_delivery`
3. Схема создана: подключитесь к БД и проверьте таблицы: `\dt`
4. Параметры подключения корректны

## Отладка

### Просмотр логов:
Тесты используют SLF4J для логирования. Для более детальных логов можно настроить `logback.xml` или `log4j2.xml`.

### Проверка данных в БД:
```sql
-- Подключитесь к БД
psql -U postgres -d food_delivery

-- Проверьте таблицы
\dt

-- Проверьте данные
SELECT * FROM clients;
SELECT * FROM carts;
SELECT * FROM cart_items;
```

## Очистка тестовых данных

Тесты автоматически очищают созданные данные после выполнения. Если что-то пошло не так, можно очистить вручную:

```sql
-- Осторожно! Удалит все данные
TRUNCATE TABLE cart_items, carts, orders, clients, couriers, products, shops, addresses, working_hours CASCADE;
```

## Примечания

- Тесты создают реальные данные в БД, но автоматически их удаляют
- Рекомендуется использовать отдельную тестовую БД для production окружения
- Некоторые тесты зависят от порядка выполнения (используют `@Order`)

