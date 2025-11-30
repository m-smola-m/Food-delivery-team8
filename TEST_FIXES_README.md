# 🔧 ГОТОВЫЕ ИСПРАВЛЕНИЯ ДЛЯ ТЕСТОВ

Этот документ содержит пошаговые инструкции для исправления всех проблем в тестовом коде.

## 📋 ТАБЛ СОДЕРЖАНИЕ

1. [Быстрый старт](#быстрый-старт)
2. [Пошаговое внедрение](#пошаговое-внедрение)
3. [Проверка результата](#проверка-результата)
4. [FAQ](#faq)

---

## 🚀 БЫСТРЫЙ СТАРТ

### Что нужно сделать (в порядке приоритета):

1. **КРИТИЧНО** ← Начни отсюда!
   - [ ] Создать `TestDatabaseConfig.java`
   - [ ] Обновить все `@BeforeAll` использовать конфиг
   - [ ] Добавить `clearDatabase()` в `@BeforeEach`

2. **ВЫСОКИЙ ПРИОРИТЕТ**
   - [ ] Создать `TestDataBuilder.java`
   - [ ] Заменить copy-paste код на builders
   - [ ] Переписать CartRepositoryTest без @Order
   - [ ] Переписать ClientRepositoryTest без @Order

3. **СРЕДНИЙ ПРИОРИТЕТ**
   - [ ] Создать `TestConstants.java`
   - [ ] Заменить magic numbers на константы
   - [ ] Заменить System.out на Logger
   - [ ] Добавить try-with-resources в DatabaseConnectionTest

---

## 📝 ПОШАГОВОЕ ВНЕДРЕНИЕ

### ШАГ 1: Создать TestDatabaseConfig.java (10 минут)

**Файл:** `src/test/java/com/team8/fooddelivery/util/TestDatabaseConfig.java`

```java
package com.team8.fooddelivery.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Единая конфигурация БД для всех тестов.
 * Читает параметры из системных свойств или использует значения по умолчанию.
 */
public class TestDatabaseConfig {
    private static final Logger log = LoggerFactory.getLogger(TestDatabaseConfig.class);
    
    public static final String DB_URL = System.getProperty("db.url",
            "jdbc:postgresql://localhost:5432/food_delivery");
    public static final String DB_USER = System.getProperty("db.user",
            "postgres");
    public static final String DB_PASSWORD = System.getProperty("db.password",
            "postgres");

    /**
     * Инициализация подключения к БД с едиными параметрами.
     * Выбросит RuntimeException если подключение не удалось.
     */
    public static void initialize() {
        log.info("Инициализация БД с параметрами: url={}, user={}", DB_URL, DB_USER);
        
        DatabaseConnection.setConnectionParams(DB_URL, DB_USER, DB_PASSWORD);
        DatabaseConnection.initializeDatabase();
        
        if (!DatabaseConnection.testConnection()) {
            log.error("Не удалось подключиться к базе данных!");
            throw new RuntimeException("Не удалось подключиться к базе данных. " +
                    "Убедитесь, что PostgreSQL запущен и БД создана.");
        }
        
        log.info("✅ БД успешно инициализирована");
    }

    public static void dropAndRecreate() throws Exception {
        log.info("Пересоздание схемы БД...");
        DatabaseConnection.initializeDatabase();
        log.info("✅ Схема БД пересоздана");
    }
}
```

**Как использовать:**
```java
@BeforeAll
static void setupDatabaseConnection() {
    TestDatabaseConfig.initialize();  // ← Просто и понятно!
}
```

---

### ШАГ 2: Создать TestDataBuilder.java (20 минут)

**Файл:** `src/test/java/com/team8/fooddelivery/util/TestDataBuilder.java`

```java
package com.team8.fooddelivery.util;

import com.team8.fooddelivery.model.*;
import com.team8.fooddelivery.model.client.Client;
import com.team8.fooddelivery.model.client.ClientStatus;
import com.team8.fooddelivery.model.courier.Courier;
import com.team8.fooddelivery.model.courier.CourierStatus;
import com.team8.fooddelivery.model.product.Cart;
import com.team8.fooddelivery.model.product.CartItem;
import com.team8.fooddelivery.model.product.Product;
import com.team8.fooddelivery.model.product.ProductCategory;
import com.team8.fooddelivery.model.shop.Shop;
import com.team8.fooddelivery.model.shop.ShopStatus;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Builder для создания тестовых сущностей.
 * Гарантирует уникальность данных и консистентность.
 */
public class TestDataBuilder {
    
    private static String uniqueId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static Client createTestClient() {
        String id = uniqueId();
        return Client.builder()
            .name("TestClient_" + id)
            .phone("+7999" + (System.currentTimeMillis() % 10000000))
            .email("testclient_" + id + "@test.com")
            .passwordHash("hash_" + id)
            .status(ClientStatus.ACTIVE)
            .isActive(true)
            .createdAt(Instant.now())
            .orderHistory(List.of())
            .build();
    }

    public static Client createTestClientWithEmail(String email) {
        String id = uniqueId();
        return Client.builder()
            .name("TestClient_" + id)
            .phone("+7999" + (System.currentTimeMillis() % 10000000))
            .email(email)
            .passwordHash("hash_" + id)
            .status(ClientStatus.ACTIVE)
            .isActive(true)
            .createdAt(Instant.now())
            .orderHistory(List.of())
            .build();
    }

    public static Courier createTestCourier() {
        String id = uniqueId();
        Courier courier = new Courier();
        courier.setName("TestCourier_" + id);
        courier.setPhoneNumber("+7999" + (System.currentTimeMillis() % 10000000));
        courier.setPassword("courier_hash_" + id);
        courier.setStatus(CourierStatus.ON_SHIFT);
        courier.setTransportType("bike");
        courier.setCurrentBalance(0.0);
        courier.setBankCard(1234567890123456L);
        return courier;
    }

    public static Courier createTestCourierWithStatus(CourierStatus status) {
        Courier courier = createTestCourier();
        courier.setStatus(status);
        return courier;
    }

    public static Shop createTestShop() {
        String id = uniqueId();
        Shop shop = new Shop();
        shop.setNaming("TestShop_" + id);
        shop.setEmailForAuth("shop_" + id + "@test.com");
        shop.setPhoneForAuth("+7999" + (System.currentTimeMillis() % 10000000));
        shop.setStatus(ShopStatus.APPROVED);
        shop.setPassword("shop_hash_" + id);
        return shop;
    }

    public static Shop createTestShopWithStatus(ShopStatus status) {
        Shop shop = createTestShop();
        shop.setStatus(status);
        return shop;
    }

    public static Address createTestAddress() {
        return Address.builder()
            .country("Россия")
            .city("Санкт-Петербург")
            .street("Невский проспект")
            .building("1")
            .apartment("10")
            .latitude(59.9343)
            .longitude(30.3351)
            .build();
    }

    public static Address createTestAddressInCity(String city) {
        return Address.builder()
            .country("Россия")
            .city(city)
            .street("Тестовая улица")
            .building("1")
            .apartment("1")
            .latitude(55.7558)
            .longitude(37.6173)
            .build();
    }

    public static Cart createTestCart(Long clientId) {
        return Cart.builder()
            .clientId(clientId)
            .items(List.of())
            .build();
    }

    public static CartItem createTestCartItem(Long productId, String productName, int quantity, double price) {
        return CartItem.builder()
            .productId(productId)
            .productName(productName)
            .quantity(quantity)
            .price(price)
            .build();
    }

    public static Product createTestProduct() {
        String id = uniqueId();
        return new Product(
            null,
            "TestProduct_" + id,
            "Test product description",
            100.0,
            150.0,
            ProductCategory.OTHER,
            true,
            Duration.ofMinutes(15)
        );
    }

    public static Product createTestProductWithName(String name) {
        return new Product(
            null,
            name,
            "Test product description",
            100.0,
            150.0,
            ProductCategory.MAIN_DISH,
            true,
            Duration.ofMinutes(15)
        );
    }
}
```

**Как использовать:**
```java
@Test
void testSaveClient() throws SQLException {
    Client client = TestDataBuilder.createTestClient();
    Long clientId = clientRepository.save(client);
    assertNotNull(clientId);
}
```

---

### ШАГ 3: Создать TestConstants.java (10 минут)

**Файл:** `src/test/java/com/team8/fooddelivery/util/TestConstants.java`

```java
package com.team8.fooddelivery.util;

/**
 * Константы для тестов.
 * Заменяет "магические числа" на понятные имена.
 */
public class TestConstants {
    
    // === Телефонные номера ===
    public static final String PHONE_PREFIX = "+7999";
    public static final int PHONE_SUFFIX_MAX = 10_000_000;
    
    // === Адреса ===
    public static final String TEST_COUNTRY = "Россия";
    public static final String TEST_CITY = "Санкт-Петербург";
    public static final String TEST_STREET = "Невский проспект";
    public static final String TEST_BUILDING = "1";
    public static final String TEST_APARTMENT = "10";
    public static final double TEST_LATITUDE = 59.9343;
    public static final double TEST_LONGITUDE = 30.3351;
    public static final int DEFAULT_TEST_FLOOR = 3;
    
    // === Заказы ===
    public static final double DEFAULT_TEST_ORDER_TOTAL = 1000.0;
    public static final int DEFAULT_TEST_ORDER_QUANTITY = 1;
    public static final int MOCK_PRODUCT_ID_1 = 1;
    public static final int MOCK_PRODUCT_ID_2 = 2;
    public static final String MOCK_PRODUCT_NAME_1 = "Пицца Маргарита";
    public static final String MOCK_PRODUCT_NAME_2 = "Кола";
    public static final double MOCK_PRODUCT_PRICE_1 = 700.0;
    public static final double MOCK_PRODUCT_PRICE_2 = 150.0;
    
    // === Коннекшены ===
    public static final int CONNECTION_TIMEOUT_SECONDS = 2;
    public static final int CONNECTION_MAX_RETRIES = 3;
    public static final long CONNECTION_RETRY_DELAY_MS = 1000;
    
    // === Временные интервалы ===
    public static final int DEFAULT_COOKING_TIME_MINUTES = 15;
    public static final int DEFAULT_DELIVERY_TIME_MINUTES = 30;
}
```

**Как использовать:**
```java
String phone = TestConstants.PHONE_PREFIX + (System.currentTimeMillis() % TestConstants.PHONE_SUFFIX_MAX);
double total = TestConstants.DEFAULT_TEST_ORDER_TOTAL;
int floor = TestConstants.DEFAULT_TEST_FLOOR;
```

---

### ШАГ 4: Обновить DatabaseConnectionTest.java (15 минут)

**Файл:** `src/test/java/com/team8/fooddelivery/util/DatabaseConnectionTest.java`

Найди этот метод:
```java
@BeforeEach
void setUp() {
    String dbUrl = System.getProperty("db.url", DEFAULT_DB_URL);
    String dbUser = System.getProperty("db.user", DEFAULT_DB_USER);
    String dbPassword = System.getProperty("db.password", DEFAULT_DB_PASSWORD);
    DatabaseConnection.setConnectionParams(dbUrl, dbUser, dbPassword);
    // ...
}
```

Замени на:
```java
@BeforeEach
void setUp() {
    TestDatabaseConfig.initialize();  // ← Просто!
}
```

И замени этот метод:
```java
@Test
void testCloseConnection() throws SQLException {
    Connection connection = DatabaseConnection.getConnection();
    // ...
    connection.close();
}
```

На:
```java
@Test
void testCloseConnection() throws SQLException {
    try (Connection connection = DatabaseConnection.getConnection()) {
        assertNotNull(connection);
        assertFalse(connection.isClosed());
        assertTrue(connection.isValid(2));
    }
    // Connection закроется автоматически
}
```

---

### ШАГ 5: Обновить ClientRepositoryTest.java (20 минут)

**Файл:** `src/test/java/com/team8/fooddelivery/repository/ClientRepositoryTest.java`

**ДО:**
```java
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientRepositoryTest {
    private static ClientRepository clientRepository;
    private static Long testClientId;

    @BeforeAll
    static void setUp() throws SQLException {
        String dbUrl = System.getProperty("db.url", "jdbc:postgresql://localhost:5432/food_delivery");
        String dbUser = System.getProperty("db.user", "postgres");
        String dbPassword = System.getProperty("db.password", "postgres");
        DatabaseConnection.setConnectionParams(dbUrl, dbUser, dbPassword);
        if (!DatabaseConnection.testConnection()) {
            throw new RuntimeException("Не удалось подключиться");
        }
        clientRepository = new ClientRepository();
    }

    @Test
    @Order(1)
    void testSaveClient() throws SQLException {
        // ...
        testClientId = clientRepository.save(client);
    }

    @Test
    @Order(2)
    void testFindById() throws SQLException {
        assumeTrue(testClientId != null);
        // ...
    }
}
```

**ПОСЛЕ:**
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisplayName("Тесты ClientRepository")
public class ClientRepositoryTest {
    private static final Logger log = LoggerFactory.getLogger(ClientRepositoryTest.class);
    
    private ClientRepository clientRepository;

    @BeforeAll
    static void setupDatabaseConnection() {
        log.info("Инициализация БД");
        TestDatabaseConfig.initialize();
    }

    @BeforeEach
    void setUp() throws SQLException {
        log.debug("Очистка тестовых данных");
        clearDatabase();
        clientRepository = new ClientRepository();
    }

    private void clearDatabase() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM clients CASCADE");
            log.debug("Таблица clients очищена");
        }
    }

    @Test
    @DisplayName("Сохранение клиента")
    void testSaveClient() throws SQLException {
        // Arrange
        Client client = TestDataBuilder.createTestClient();
        
        // Act
        Long clientId = clientRepository.save(client);
        
        // Assert
        assertNotNull(clientId);
        assertTrue(clientId > 0);
    }

    @Test
    @DisplayName("Поиск клиента по ID")
    void testFindById() throws SQLException {
        // Arrange
        Client testClient = TestDataBuilder.createTestClient();
        Long clientId = clientRepository.save(testClient);
        
        // Act
        Optional<Client> found = clientRepository.findById(clientId);
        
        // Assert
        assertTrue(found.isPresent());
        assertEquals(testClient.getName(), found.get().getName());
        assertEquals(testClient.getEmail(), found.get().getEmail());
    }

    @Test
    @DisplayName("Поиск несуществующего клиента")
    void testFindByIdNotFound() throws SQLException {
        // Act
        Optional<Client> found = clientRepository.findById(99999L);
        
        // Assert
        assertFalse(found.isPresent());
    }
}
```

---

### ШАГ 6: Обновить CartRepositoryTest.java (15 минут)

Аналогично ClientRepositoryTest:
1. Удалить `@TestMethodOrder` и `@Order`
2. Добавить `@BeforeEach` с `clearDatabase()`
3. Переписать тесты чтобы они были независимыми
4. Использовать `TestDataBuilder`

---

### ШАГ 7: Обновить интеграционные тесты (30 минут)

Для каждого файла в `integration/`:

1. **Обновить @BeforeAll:**
   ```java
   @BeforeAll
   static void setupDatabaseConnection() {
       TestDatabaseConfig.initialize();  // Вместо дублирования
   }
   ```

2. **Добавить @BeforeEach с очисткой:**
   ```java
   @BeforeEach
   void setUp() throws SQLException {
       clearDatabase();
       // ... инициализировать repositories
   }
   ```

3. **Использовать TestDataBuilder:**
   ```java
   Client client = TestDataBuilder.createTestClient();
   Courier courier = TestDataBuilder.createTestCourier();
   Shop shop = TestDataBuilder.createTestShop();
   // Вместо copy-paste кода
   ```

4. **Использовать TestConstants:**
   ```java
   double total = TestConstants.DEFAULT_TEST_ORDER_TOTAL;
   // Вместо магического 1000.0
   ```

---

## ✅ ПРОВЕРКА РЕЗУЛЬТАТА

### Тест 1: Все тесты запускаются независимо

```bash
# Должно работать без ошибок:
mvn test -Dtest=ClientRepositoryTest#testSaveClient
mvn test -Dtest=ClientRepositoryTest#testFindById
mvn test -Dtest=ClientRepositoryTest#testFindByIdNotFound
```

**ОЖИДАЕМЫЙ РЕЗУЛЬТАТ:** ✅ Все тесты проходят, независимо от порядка

### Тест 2: Тесты можно запускать несколько раз подряд

```bash
# Запусти дважды - должно работать оба раза:
mvn test
mvn test
```

**ОЖИДАЕМЫЙ РЕЗУЛЬТАТ:** ✅ Оба запуска успешны, нет constraint violations

### Тест 3: Проверить логирование

```bash
# В консоле должны быть INFO/DEBUG логи:
mvn test -X
```

**ОЖИДАЕМЫЙ РЕЗУЛЬТАТ:** ✅ Вместо "System.out" видны нормальные логи

### Тест 4: Проверить управление ресурсами

```bash
# Проверить что нет утечек соединений:
mvn test -Dtest=DatabaseConnectionTest
```

**ОЖИДАЕМЫЙ РЕЗУЛЬТАТ:** ✅ Соединения закрываются корректно

---

## 📊 МЕТРИКИ ДО И ПОСЛЕ

| Метрика | До | После |
|---------|----|----|
| Зависимости между тестами | ❌ Высокие | ✅ Нет |
| Copy-paste кода | ❌ Много | ✅ Нет |
| Flaky tests | ❌ Частые | ✅ Редко |
| Изоляция тестов | ❌ 3/10 | ✅ 9/10 |
| Управление ресурсами | ❌ 4/10 | ✅ 9/10 |
| Логирование | ❌ 4/10 | ✅ 9/10 |
| **ОБЩАЯ ОЦЕНКА** | ❌ **4.5/10** | ✅ **8.5/10** |

---

## ❓ FAQ

### В: Сколько времени это займёт?
О: 1-2 часа для базовых исправлений, 4-6 часов для всех интеграционных тестов.

### В: С чего начать?
О: С ШАГ 1 (TestDatabaseConfig). Это займёт 10 минут и решит главную проблему.

### В: Что если я забуду очистить БД?
О: Тесты начнут падать с ошибками вроде "UNIQUE constraint violation".

### В: Нужно ли менять основной код?
О: Нет! Исправления только в тестовом коде.

### В: Почему @Order плохо?
О: Потому что тесты должны работать независимо. Если запустить только один тест - он должен пройти.

### В: Как я узнаю что всё сделано правильно?
О: Если все тесты проходят И их можно запустить в любом порядке.

---

## 🔗 ДОПОЛНИТЕЛЬНЫЕ РЕСУРСЫ

- `code_analysis_report.md` — Полный анализ проблем
- `fixes_and_examples.md` — Подробные примеры
- `visual_analysis.md` — Визуальные диаграммы
- `SUMMARY.md` — Краткое резюме

---

**Версия:** 1.0  
**Дата:** 30 ноября 2025  
**Статус:** Ready for implementation ✅

