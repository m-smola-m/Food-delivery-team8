package com.team8.fooddelivery.service.impl;

import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.model.product.Product;
import com.team8.fooddelivery.model.product.ProductCategory;
import com.team8.fooddelivery.model.shop.Shop;
import com.team8.fooddelivery.model.shop.ShopStatus;
import com.team8.fooddelivery.model.shop.ShopType;
import com.team8.fooddelivery.model.shop.WorkingHours;
import com.team8.fooddelivery.repository.AddressRepository;
import com.team8.fooddelivery.repository.ShopRepository;
import com.team8.fooddelivery.repository.WorkingHoursRepository;
import com.team8.fooddelivery.service.DatabaseConnectionService;
import com.team8.fooddelivery.service.DatabaseInitializerService;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ShopProductServiceImplTest {

  private ShopProductServiceImpl shopProductService;
  private static Long testShopId;
  private static Long testProductId;
  private static Long testAddressId;
  private static Long testWorkingHoursId;

  @BeforeAll
  static void setupDatabase() throws SQLException {
    String dbUrl = System.getProperty("db.url", "jdbc:postgresql://localhost:5432/food_delivery");
    String dbUser = System.getProperty("db.user", "postgres");
    String dbPassword = System.getProperty("db.password", "postgres");
    DatabaseConnectionService.setConnectionParams(dbUrl, dbUser, dbPassword);
    DatabaseInitializerService.initializeDatabase();

    AddressRepository addrRepo = new AddressRepository();
    WorkingHoursRepository hoursRepo = new WorkingHoursRepository();
    ShopRepository shopRepo = new ShopRepository();

    Address address = Address.builder()
        .country("Russia")
        .city("Test City")
        .street("Test Street")
        .building("1")
        .build();
    testAddressId = addrRepo.save(address);

    WorkingHours workingHours = new WorkingHours(
        "09:00-21:00", "09:00-21:00", "09:00-21:00", "09:00-21:00",
        "09:00-21:00", "10:00-22:00", "10:00-22:00"
    );
    testWorkingHoursId = hoursRepo.save(workingHours);

    long suffix = System.currentTimeMillis();
    Shop shop = new Shop();
    shop.setNaming("Test Shop " + suffix);
    shop.setDescription("Test Description " + suffix);
    shop.setPublicEmail("public_" + suffix + "@shop.com");
    shop.setEmailForAuth("shop" + suffix + "@test.com");
    shop.setPhoneForAuth("+7999" + (suffix % 10000000));
    shop.setPublicPhone("+7444" + (suffix % 10000000));
    shop.setStatus(ShopStatus.APPROVED);
    shop.setOwnerName("Owner " + suffix);
    shop.setOwnerContactPhone("+7555" + (suffix % 10000000));
    shop.setType(ShopType.RESTAURANT);
    shop.setPassword("test_password");
    shop.setAddress(address);
    shop.setWorkingHours(workingHours);

    testShopId = shopRepo.save(shop);
  }

  @AfterAll
  static void cleanup() {
    // Очистка ресурсов
    if (testProductId != null) deleteRecord("products", "product_id", testProductId);
    if (testShopId != null) deleteRecord("shops", "shop_id", testShopId);
    if (testAddressId != null) deleteRecord("addresses", "id", testAddressId);
    if (testWorkingHoursId != null) deleteRecord("working_hours", "id", testWorkingHoursId);
  }

  private static void deleteRecord(String table, String idColumn, Long id) {
    try (Connection conn = DatabaseConnectionService.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM " + table + " WHERE " + idColumn + " = ?")) {
      stmt.setLong(1, id);
      stmt.executeUpdate();
    } catch (SQLException e) {
      System.err.println("Error cleaning up " + table + ": " + e.getMessage());
    }
  }

  @BeforeEach
  void setUp() {
    shopProductService = new ShopProductServiceImpl();
  }

  @Test
  @Order(1)
  @DisplayName("Get shop by ID")
  void testGetShopById() {
    Shop shop = shopProductService.getShopById(testShopId);
    assertNotNull(shop);
    assertEquals(testShopId, shop.getShopId());
  }

  @Test
  @Order(2)
  @DisplayName("Get shop by ID - not found")
  void testGetShopById_NotFound() {
    Shop shop = shopProductService.getShopById(-1L);
    assertNull(shop);
  }

  @Test
  @Order(3)
  @DisplayName("Add product to shop")
  void testAddProduct() {
    Product product = Product.builder()
        .name("Test Product")
        .description("Description")
        .weight(100.0)
        .price(10.0)
        .category(ProductCategory.MAIN_DISH)
        .available(true)
        .cookingTimeMinutes(Duration.ofMinutes(15))
        .build();
    Product added = shopProductService.addProduct(testShopId, product);
    assertNotNull(added);
    assertNotNull(added.getProductId());
    testProductId = added.getProductId();
  }

  @Test
  @Order(4)
  @DisplayName("Get shop products")
  void testGetShopProducts() {
    List<Product> products = shopProductService.getShopProducts(testShopId);
    assertNotNull(products, "Products list should not be null");
    assertFalse(products.isEmpty());
  }

  @Test
  @Order(5)
  @DisplayName("Get products by category")
  void testGetProductsByCategory() {
    List<Product> products = shopProductService.getProductsByCategory(testShopId, ProductCategory.MAIN_DISH);
    assertNotNull(products);
    assertFalse(products.isEmpty());
  }

  @Test
  @Order(7) // ИЗМЕНЕН ПОРЯДОК: Обновляем сам продукт (до удаления!)
  @DisplayName("Update product")
  void testUpdateProduct() {
    if (testProductId == null) fail("Product ID is null");

    Product product = Product.builder()
        .productId(testProductId)
        .name("Updated Product")
        .description("Updated Description")
        .weight(150.0)
        .price(15.0)
        .category(ProductCategory.MAIN_DISH)
        .available(true)
        .cookingTimeMinutes(Duration.ofMinutes(20))
        .build();

    Product updated = shopProductService.updateProduct(testShopId, testProductId, product);
    assertNotNull(updated);
    assertEquals("Updated Product", updated.getName());
  }

  @Test
  @Order(8) // ИЗМЕНЕН ПОРЯДОК: Удаляем продукт в самом конце цикла
  @DisplayName("Remove product")
  void testRemoveProduct() {
    if (testProductId == null) fail("Product ID is null");
    assertDoesNotThrow(() -> shopProductService.deleteProduct(testShopId, testProductId));
  }

  // --- Остальные тесты сдвинуты вниз ---

  @Test
  @Order(9)
  @DisplayName("Change shop status")
  void testChangeShopStatus() {
    shopProductService.changeShopStatus(testShopId, ShopStatus.REJECTED);
    Shop shop = shopProductService.getShopById(testShopId);
    assertEquals(ShopStatus.REJECTED, shop.getStatus());
    // Restore
    shopProductService.changeShopStatus(testShopId, ShopStatus.APPROVED);
  }

  @Test
  @Order(10)
  @DisplayName("Update shop info")
  void testUpdateShopInfo() {
    Shop shop = shopProductService.getShopById(testShopId);
    shop.setNaming("Updated Shop Name");
    Shop updated = shopProductService.updateShopInfo(testShopId, shop);
    assertNotNull(updated);
    assertEquals("Updated Shop Name", updated.getNaming());
  }

  @Test
  @Order(11)
  @DisplayName("Get products by category - empty result")
  void testGetProductsByCategory_Empty() {
    List<Product> products = shopProductService.getProductsByCategory(testShopId, ProductCategory.DRINK);
    assertNotNull(products);
    assertTrue(products.isEmpty());
  }

  @Test
  @Order(12)
  @DisplayName("Update product availability - product not found")
  void testUpdateProductAvailability_NotFound() {
    assertDoesNotThrow(() -> {
      shopProductService.updateProductAvailability(testShopId, -1L, true);
    });
  }

  @Test
  @Order(13)
  @DisplayName("Get shop products - empty")
  void testGetShopProducts_Empty() {
    List<Product> products = shopProductService.getShopProducts(-1L);
    assertNotNull(products);
    assertTrue(products.isEmpty());
  }

  @Test
  @Order(14)
  @DisplayName("Change shop status - shop not found")
  void testChangeShopStatus_NotFound() {
    assertDoesNotThrow(() -> {
      shopProductService.changeShopStatus(-1L, ShopStatus.PENDING);
    });
  }

  @Test
  @Order(15)
  @DisplayName("Get products by category - SQLException handling")
  void testGetProductsByCategory_SQLException() {
    List<Product> products = shopProductService.getProductsByCategory(-999L, ProductCategory.MAIN_DISH);
    assertNotNull(products);
  }

  @Test
  @Order(16)
  @DisplayName("Update product availability - product not found path")
  void testUpdateProductAvailability_ProductNotFound() {
    assertDoesNotThrow(() -> {
      shopProductService.updateProductAvailability(testShopId, -999L, true);
    });
  }

  @Test
  @Order(17)
  @DisplayName("Change shop status - shop not found path")
  void testChangeShopStatus_ShopNotFound() {
    assertDoesNotThrow(() -> {
      shopProductService.changeShopStatus(-999L, ShopStatus.PENDING);
    });
  }

  @Test
  @Order(18)
  @DisplayName("Get shop by ID - SQLException handling")
  void testGetShopById_SQLException() {
    Shop shop = shopProductService.getShopById(-999L);
    assertNull(shop);
  }
}