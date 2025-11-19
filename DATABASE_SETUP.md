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
java
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

java
try {
    Long clientId = clientRepository.save(client);
} catch (SQLException e) {
    logger.error("Ошибка при сохранении клиента", e);
    throw new RuntimeException("Не удалось сохранить клиента", e);
}
```
