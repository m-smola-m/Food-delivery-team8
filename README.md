# Food Delivery (Примерная инструкция по проекту)

# Настройка PostgreSQL для Food Delivery Service

## Требования
- PostgreSQL 12 или выше
- Java 17

## Шаги настройки

### 1. Установка PostgreSQL
Убедитесь, что PostgreSQL установлен и запущен на вашей системе.

### 2. Создание базы данных
```sql
CREATE DATABASE food_delivery;
```

### 3. Создание схемы
Выполните SQL скрипт из файла `src/main/resources/schema.sql`:
```bash
psql -U postgres -d food_delivery -f src/main/resources/schema.sql
```

Или через psql:
```bash
psql -U postgres -d food_delivery
\i src/main/resources/schema.sql
```

### 4. Настройка подключения

**Важно:** Имя пользователя PostgreSQL зависит от вашей системы:

- **macOS (Homebrew):** обычно используется ваше имя пользователя системы (проверьте через `whoami`)
- **Linux:** обычно используется `postgres`

По умолчанию приложение использует следующие параметры:
- URL: `jdbc:postgresql://localhost:5432/food_delivery`
- User: `postgres` (на Linux) или ваше имя пользователя (на macOS)
- Password: `postgres` (на Linux) или пустой (на macOS)

#### Способ 1: Через системные свойства
```bash
java -Ddb.url=jdbc:postgresql://localhost:5432/food_delivery \
     -Ddb.user=your_user \
     -Ddb.password=your_password \
     YourMainClass
```

#### Способ 2: Программно
```java
DatabaseConnection.setConnectionParams(
    "jdbc:postgresql://localhost:5432/food_delivery",
    "your_user",
    "your_password"
);
```

### 5. Проверка подключения
```java
if (DatabaseConnection.testConnection()) {
    System.out.println("Подключение к БД успешно!");
} else {
    System.out.println("Ошибка подключения к БД");
}
```

## Использование Repository

Все Repository классы находятся в пакете `com.team8.fooddelivery.repository`:

- `AddressRepository` - работа с адресами
- `ClientRepository` - работа с клиентами
- `ShopRepository` - работа с магазинами
- `ProductRepository` - работа с продуктами
- `CourierRepository` - работа с курьерами
- `CartRepository` - работа с корзинами
- `OrderRepository` - работа с заказами
- `WorkingHoursRepository` - работа с рабочими часами

### Пример использования:
```java
ClientRepository clientRepository = new ClientRepository();

// Сохранение клиента
Client client = Client.builder()
    .name("Иван Иванов")
    .phone("+79991234567")
    .email("ivan@example.com")
    .passwordHash(PasswordUtils.hashPassword("password123"))
    .status(ClientStatus.ACTIVE)
    .isActive(true)
    .build();

Long clientId = clientRepository.save(client);

// Поиск клиента
Optional<Client> foundClient = clientRepository.findById(clientId);
Optional<Client> byPhone = clientRepository.findByPhone("+79991234567");

// Обновление клиента
client.setName("Иван Петров");
clientRepository.update(client);

// Удаление клиента
clientRepository.delete(clientId);
```

## Обработка ошибок

Все методы Repository могут выбрасывать `SQLException`. Рекомендуется обрабатывать их в сервисном слое:

```java
try {
    Long clientId = clientRepository.save(client);
} catch (SQLException e) {
    logger.error("Ошибка при сохранении клиента", e);
    throw new RuntimeException("Не удалось сохранить клиента", e);
}
```
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

# 🧪 Тестирование проекта

## Быстрый старт

### 1. Автоматическая проверка и запуск тестов

```bash
# Проверка подключения к БД
./check_db_connection.sh

# Запуск всех тестов
./RUN_TESTS.sh
```

### 2. Ручной запуск

#### macOS:
```bash
# Создание БД и схемы
createdb food_delivery
psql -d food_delivery -f src/main/resources/schema.sql

# Запуск тестов
mvn test -Ddb.user=$(whoami) -Ddb.password=""
```

#### Linux:
```bash
# Создание БД и схемы
createdb -U postgres food_delivery
psql -U postgres -d food_delivery -f src/main/resources/schema.sql

# Запуск тестов
mvn test -Ddb.user=postgres -Ddb.password=postgres
```

## Результаты тестов

При успешном запуске вы увидите:
```
Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Структура тестов

1. **SimpleConnectionTest** - проверка подключения к БД
2. **DatabaseConnectionTest** - тесты класса DatabaseConnection
3. **ClientRepositoryTest** - тесты работы с клиентами (7 тестов)
4. **CartRepositoryTest** - тесты работы с корзинами (5 тестов)
5. **DatabaseIntegrationTest** - полные интеграционные тесты (3 теста)

**Всего: 20 тестов**

## Что проверяют тесты

✅ Подключение к PostgreSQL  
✅ CRUD операции для всех сущностей  
✅ Связи между сущностями (foreign keys)  
✅ Полные сценарии использования системы  
✅ Валидация данных  
✅ Обработка ошибок  

## Дополнительная информация

- Подробная документация: [TESTING.md](TESTING.md)
- Быстрый старт: [QUICK_TEST.md](QUICK_TEST.md)
- Настройка БД: [DATABASE_SETUP.md](DATABASE_SETUP.md)

