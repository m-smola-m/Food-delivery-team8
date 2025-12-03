package com.team8.fooddelivery.service.impl;

import com.team8.fooddelivery.model.product.Cart;
import com.team8.fooddelivery.model.product.CartItem;
import com.team8.fooddelivery.repository.CartRepository;
import com.team8.fooddelivery.service.DatabaseConnectionService;
import com.team8.fooddelivery.service.DatabaseInitializerService;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CartServiceImplTest {

  private final CartServiceImpl cartService = new CartServiceImpl();
  private final CartRepository cartRepository = new CartRepository();

  private static Long testClientId;

  @BeforeAll
  static void globalSetup() {
    DatabaseConnectionService.setConnectionParams(
        System.getProperty("db.url", "jdbc:postgresql://localhost:5432/food_delivery"),
        System.getProperty("db.user", "postgres"),
        System.getProperty("db.password", "postgres"));
    DatabaseInitializerService.initializeDatabase();
    testClientId = createTestClient();
  }

  @AfterAll
  static void globalTearDown() {
    deleteTestClient(testClientId);
  }

  @BeforeEach
  void setUp() throws SQLException {
    DatabaseInitializerService.cleanTestData();
    testClientId = createTestClient();
  }

  @Test
  @Order(1)
  @DisplayName("Сценарий: Полный жизненный цикл (Create -> Add -> Update -> Total -> Remove)")
  void testFullLifecycle() throws SQLException {
    DatabaseInitializerService.resetDatabaseWithTestData();
    testClientId = createTestClient();

    // Создаем продукт для теста
    Long productId = createTestProduct();
    
    // 1. Создание (Create)
    Cart cart = cartService.createCartForClient(testClientId);

    assertNotNull(cart.getId(), "ID корзины должен быть сгенерирован");
    assertEquals(testClientId, cart.getClientId());

    // ПРОВЕРКА В БД (User Story check)
    assertTrue(cartRepository.findById(cart.getId()).isPresent(),
        "Запись корзины должна реально появиться в базе данных");

    // 2. Добавление товара (Add)
    CartItem item = CartItem.builder()
        .productId(productId)
        .productName("Pizza")
        .quantity(1)
        .price(100.0)
        .build();

    cartService.addItem(testClientId, item);

    // ПРОВЕРКА В БД
    List<CartItem> dbItems = cartRepository.findCartItemsByCartId(cart.getId());
    assertEquals(1, dbItems.size());
    assertEquals("Pizza", dbItems.get(0).getProductName());

    // 3. Добавление того же товара (суммирование)
    CartItem sameItem = CartItem.builder()
        .productId(productId)
        .quantity(2) // Добавляем еще 2
        .build();
    cartService.addItem(testClientId, sameItem);

    // ПРОВЕРКА В БД (1 + 2 = 3)
    dbItems = cartRepository.findCartItemsByCartId(cart.getId());
    assertEquals(3, dbItems.get(0).getQuantity(), "Количество должно сложиться в базе");

    // 4. Обновление (Update)
    cartService.updateItem(testClientId, productId, 5); // Ставим 5 штук

    // ПРОВЕРКА В БД
    dbItems = cartRepository.findCartItemsByCartId(cart.getId());
    assertEquals(5, dbItems.get(0).getQuantity(), "Количество должно обновиться в базе до 5");

    // 5. Проверка списка (List Items)
    List<CartItem> list = cartService.listItems(testClientId);
    assertFalse(list.isEmpty());

    // 6. Удаление товара (Remove)
    cartService.removeItem(testClientId, productId);

    // ПРОВЕРКА В БД
    dbItems = cartRepository.findCartItemsByCartId(cart.getId());
    assertTrue(dbItems.isEmpty(), "Товар должен удалиться из базы");
  }

  @Test
  @Order(2)
  @DisplayName("Сценарий: Если корзина уже есть, не создавать дубликат")
  void testCreateExisting() {
    try {
    // Первый вызов
        Cart first = cartService.createCartForClient(testClientId);
        Long firstId = first.getId();
    // Второй вызов
        Cart second = cartService.createCartForClient(testClientId);
        Long secondId = second.getId();
        assertEquals(firstId, secondId, "Должен вернуться ID уже существующей корзины");
        System.out.println(3423);
    }
    catch (Exception e) {
        System.out.println("Должен вернуть ошибку (корзина с таким id уже существует) " + e);
    }
  }

  @Test
  @Order(3)
  @DisplayName("Сценарий: Очистка корзины (Clear)")
  void testClearCart() throws SQLException {
    // Создаем продукты для теста
    Long productId1 = createTestProduct();
    Long productId2 = createTestProduct();
    
    // Подготовка данных
    cartService.addItem(testClientId, CartItem.builder().productId(productId1).productName("A").quantity(1).price(10.0).build());
    cartService.addItem(testClientId, CartItem.builder().productId(productId2).productName("B").quantity(1).price(10.0).build());

    // Действие
    cartService.clear(testClientId);

    // Проверка в БД
    List<CartItem> items = cartRepository.findCartItemsByCartId(cartService.getCartForClient(testClientId).getId());
    assertTrue(items.isEmpty(), "Корзина в БД должна быть пуста");
  }

  @Test
  @Order(4)
  @DisplayName("Логика: Неавторизованный пользователь (clientId = null)")
  void testNullClient() {
    Cart cart = cartService.createCartForClient(null);
    assertNull(cart.getClientId());
    assertNull(cart.getId()); // В базу не пишем
    assertTrue(cart.getItems().isEmpty());
  }

  @Test
  @Order(5)
  @DisplayName("Логика: Несуществующий клиент (Ошибка)")
  void testNonExistentClient() {
    Long fakeId = 99999999L;

    Exception exception = assertThrows(RuntimeException.class, () -> {
      cartService.createCartForClient(fakeId);
    });

    // Ошибка должна быть связана с валидацией или клиентом
    assertNotNull(exception);
  }

  @Test
  @Order(6)
  @DisplayName("Логика: Подсчет суммы (Total)")
  void testCalculateTotal() throws SQLException {
    // Убеждаемся, что корзина пуста перед тестом
    try {
      cartService.clear(testClientId);
      deleteCartDirectly(testClientId);
    } catch (Exception e) {
      // Игнорируем ошибки
    }
    
    // Создаем продукт для теста
    Long productId = createTestProduct();
    
    // Создаем новую корзину и добавляем элемент
    cartService.createCartForClient(testClientId);
    cartService.addItem(testClientId, CartItem.builder().productId(productId).productName("X").quantity(2).price(50.0).build());
    // 2 * 50 = 100

    Long total = cartService.calculateTotal(testClientId);
    assertEquals(100L, total, "Total should be 100 (2 * 50), but was: " + total); // Если in Cart.getTotalPrice() логика price * quantity
  }

  // ==========================================
  // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ (SQL)
  // ==========================================

  private static Long createTestClient() {
    // 1. Генерируем уникальный email, чтобы не получить ошибку Unique Constraint
    String uniqueEmail = "test_user_" + System.currentTimeMillis() + "@example.com";

    // 2. SQL ЗАПРОС.
    // !!! ЕСЛИ У ТЕБЯ ТАБЛИЦА НАЗЫВАЕТСЯ ПО-ДРУГОМУ - МЕНЯЙ ЗДЕСЬ !!!
    // Например: INSERT INTO users (login, pass) ...
    String uniquePhone = "+7999" + (System.currentTimeMillis() % 10000000);
    String sql = "INSERT INTO clients (name, phone, email, password_hash, status, is_active) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseConnectionService.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      stmt.setString(1, "TestJUnit");
      stmt.setString(2, uniquePhone);
      stmt.setString(3, uniqueEmail);
      stmt.setString(4, "hashed_password");
      stmt.setString(5, "ACTIVE");
      stmt.setBoolean(6, true);

      int affectedRows = stmt.executeUpdate();

      if (affectedRows == 0) {
        throw new SQLException("Creating client failed, no rows affected.");
      }

      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          return generatedKeys.getLong(1); // Возвращаем ID нового клиента
        } else {
          throw new SQLException("Creating client failed, no ID obtained.");
        }
      }
    } catch (SQLException e) {
      System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      System.err.println("ОШИБКА В ТЕСТЕ: Не удалось создать клиента в БД.");
      System.err.println("Проверь название таблицы и колонок в методе createTestClient!");
      System.err.println("Текст ошибки: " + e.getMessage());
      System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      throw new RuntimeException("Тесты остановлены из-за ошибки БД", e);
    }
  }

  private static void deleteTestClient(Long id) {
    if (id == null) return;
    try (Connection conn = DatabaseConnectionService.getConnection()) {
      // Удаляем корзины
      try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM carts WHERE client_id = ?")) {
        stmt.setLong(1, id);
        stmt.executeUpdate();
      }
      // Удаляем клиента
      try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM clients WHERE id = ?")) {
        stmt.setLong(1, id);
        stmt.executeUpdate();
      }
    } catch (SQLException e) {
      System.err.println("Не удалось удалить тестовые данные: " + e.getMessage());
    }
  }

  private void deleteCartDirectly(Long clientId) throws SQLException {
    try (Connection conn = DatabaseConnectionService.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM carts WHERE client_id = ?")) {
      stmt.setLong(1, clientId);
      stmt.executeUpdate();
    }
  }

  private Long createTestProduct() throws SQLException {
    // Создаем тестовый продукт
    // Схема: name, description, weight, price, category, is_available, cooking_time_minutes, shop_id
    String sql = "INSERT INTO products (name, description, weight, price, category, is_available, cooking_time_minutes, shop_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING product_id";

    try (Connection conn = DatabaseConnectionService.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      
      // Создаем тестовый магазин, если его нет
      Long shopId = createTestShop();
      
      String uniqueName = "Test Product " + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
      stmt.setString(1, uniqueName);
      stmt.setString(2, "Test Description");
      stmt.setDouble(3, 250.0);
      stmt.setDouble(4, 100.0);
      stmt.setString(5, "MAIN_DISH");
      stmt.setBoolean(6, true);
      stmt.setLong(7, 600L);
      stmt.setLong(8, shopId);

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) return rs.getLong(1);
    }
    throw new SQLException("Не удалось создать тестовый продукт");
  }

  private Long createTestShop() throws SQLException {
    // Проверяем, есть ли уже тестовый магазин
    String checkSql = "SELECT shop_id FROM shops WHERE naming = 'Test Shop' LIMIT 1";
    try (Connection conn = DatabaseConnectionService.getConnection();
        PreparedStatement stmt = conn.prepareStatement(checkSql);
        ResultSet rs = stmt.executeQuery()) {
      if (rs.next()) {
        return rs.getLong("shop_id");
      }
    }
    
    // Создаем тестовый магазин
    String sql = "INSERT INTO shops (naming, description, status, registration_date, email_for_auth, phone_for_auth, password, public_email, public_phone, owner_name, owner_contact_phone, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING shop_id";
    try (Connection conn = DatabaseConnectionService.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      
      long suffix = System.currentTimeMillis();
      stmt.setString(1, "Test Shop " + suffix);
      stmt.setString(2, "Test Description");
      stmt.setString(3, "APPROVED");
      stmt.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
      stmt.setString(5, "shop" + suffix + "@test.com");
      stmt.setString(6, "+7999" + (suffix % 10000000));
      stmt.setString(7, "test_password");
      stmt.setString(8, "public_" + suffix + "@shop.com");
      stmt.setString(9, "+7444" + (suffix % 10000000));
      stmt.setString(10, "Owner " + suffix);
      stmt.setString(11, "+7555" + (suffix % 10000000));
      stmt.setString(12, "RESTAURANT");

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) return rs.getLong(1);
    }
    throw new SQLException("Не удалось создать тестовый магазин");
  }
}
