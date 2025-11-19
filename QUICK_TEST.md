# Быстрый старт тестирования

## Автоматическая проверка подключения

Запустите скрипт для автоматической проверки:
```bash
./check_db_connection.sh
```

Скрипт покажет все необходимые параметры для запуска тестов.

## Ручная настройка

### Шаг 1: Определение имени пользователя PostgreSQL

**На macOS (Homebrew):** обычно используется ваше имя пользователя системы:
```bash
whoami  # покажет ваше имя пользователя (например: smolevanataliia)
```

**На Linux:** обычно используется `postgres`

### Шаг 2: Подготовка БД

#### Для macOS (Homebrew):
```bash
# Создайте БД (используйте ваше имя пользователя)
createdb food_delivery

# Создайте схему
psql -d food_delivery -f src/main/resources/schema.sql
```

#### Для Linux (стандартная установка):
```bash
# Создайте БД
createdb -U postgres food_delivery

# Создайте схему
psql -U postgres -d food_delivery -f src/main/resources/schema.sql
```

### Шаг 3: Запуск простого теста подключения

#### Для macOS (используйте ваше имя пользователя):
```bash
mvn test -Dtest=SimpleConnectionTest \
         -Ddb.user=$(whoami) \
         -Ddb.password=""
```

#### Для Linux:
```bash
mvn test -Dtest=SimpleConnectionTest \
         -Ddb.user=postgres \
         -Ddb.password=postgres
```

Если видите `✅ Подключение к базе данных успешно!` - всё работает!

### Шаг 4: Запуск всех тестов

#### macOS:
```bash
mvn test -Ddb.user=$(whoami) -Ddb.password=""
```

#### Linux:
```bash
mvn test -Ddb.user=postgres -Ddb.password=postgres
```

**Результат успешного запуска:**
```
Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Альтернатива: Использование скриптов

```bash
# Автоматическая проверка подключения
./check_db_connection.sh

# Автоматический запуск всех тестов
./RUN_TESTS.sh
```

## Если тесты не проходят

1. Проверьте, что PostgreSQL запущен:
   ```bash
   pg_isready
   # или
   systemctl status postgresql  # Linux
   brew services list           # macOS
   ```

2. Проверьте параметры подключения:

   **macOS (Homebrew):**
    - URL: `jdbc:postgresql://localhost:5432/food_delivery`
    - User: ваше имя пользователя (проверьте через `whoami`)
    - Password: обычно пустой или ваш системный пароль

   **Linux:**
    - URL: `jdbc:postgresql://localhost:5432/food_delivery`
    - User: `postgres`
    - Password: `postgres` (или ваш пароль)

3. Измените параметры при необходимости:
   ```bash
   # macOS
   mvn test -Ddb.user=$(whoami) -Ddb.password=""
   
   # Linux
   mvn test -Ddb.user=postgres -Ddb.password=postgres
   
   # Или с полными параметрами
   mvn test -Ddb.url=jdbc:postgresql://localhost:5432/food_delivery \
            -Ddb.user=your_user \
            -Ddb.password=your_password
   ```

## Структура тестов

- **SimpleConnectionTest** - самый простой, проверяет только подключение
- **DatabaseConnectionTest** - тесты класса подключения
- **ClientRepositoryTest** - тесты работы с клиентами
- **CartRepositoryTest** - тесты работы с корзинами
- **DatabaseIntegrationTest** - полные интеграционные тесты

Подробнее см. [TESTING.md](TESTING.md)