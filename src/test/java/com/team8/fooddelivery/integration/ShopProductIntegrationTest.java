package com.team8.fooddelivery.integration;

import com.team8.fooddelivery.model.*;
import com.team8.fooddelivery.model.product.Product;
import com.team8.fooddelivery.model.product.ProductCategory;
import com.team8.fooddelivery.model.shop.Shop;
import com.team8.fooddelivery.model.shop.ShopStatus;
import com.team8.fooddelivery.model.shop.ShopType;
import com.team8.fooddelivery.model.shop.WorkingHours;
import com.team8.fooddelivery.repository.*;
import com.team8.fooddelivery.service.DatabaseConnectionService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Интеграционные тесты магазинов и продуктов")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShopProductIntegrationTest {

  private AddressRepository addressRepository;
  private WorkingHoursRepository workingHoursRepository;
  private ShopRepository shopRepository;
  private ProductRepository productRepository;

  @BeforeAll
  static void setupDatabaseConnectionService() {
    DatabaseConnectionService.initializeDatabase();
    String dbUrl = System.getProperty("db.url", "jdbc:postgresql://localhost:5432/food_delivery");
    String dbUser = System.getProperty("db.user", "postgres");
    String dbPassword = System.getProperty("db.password", "postgres");
    DatabaseConnectionService.setConnectionParams(dbUrl, dbUser, dbPassword);

    if (!DatabaseConnectionService.testConnection()) {
      throw new RuntimeException("Не удалось подключиться к базе данных");
    }
  }

  @BeforeEach
  void setUp() {
    addressRepository = new AddressRepository();
    workingHoursRepository = new WorkingHoursRepository();
    shopRepository = new ShopRepository();
    productRepository = new ProductRepository();
  }

  @Test
  @DisplayName("Создание магазина с продуктами")
  void testShopWithProducts() throws SQLException {
    Long shopAddressId = null;
    Long workingHoursId = null;
    Long shopId = null;
    Long product1Id = null;
    Long product2Id = null;

    try {
      // 1. Создание адреса для магазина
      Address shopAddress = Address.builder()
          .country("Россия")
          .city("Москва")
          .street("Тверская")
          .building("5")
          .latitude(55.7558)
          .longitude(37.6173)
          .build();
      shopAddressId = addressRepository.save(shopAddress);

      // 2. Создание рабочих часов
      WorkingHours workingHours = new WorkingHours(
          "09:00-21:00",
          "09:00-21:00",
          "09:00-21:00",
          "09:00-21:00",
          "09:00-21:00",
          "10:00-22:00",
          "10:00-22:00"
      );
      workingHoursId = workingHoursRepository.save(workingHours);

      // 3. Создание магазина
      Shop shop = new Shop();
      shop.setNaming("Тестовый Ресторан");
      shop.setDescription("Описание тестового ресторана");
      shop.setPublicEmail("restaurant@test.com");
      shop.setEmailForAuth("restaurant_auth@test.com");
      shop.setPhoneForAuth("+79991112233");
      shop.setPublicPhone("+79991112233");
      shop.setStatus(ShopStatus.PENDING);
      shop.setAddress(shopAddress);
      shop.setWorkingHours(workingHours);
      shop.setOwnerName("Владелец Тест");
      shop.setOwnerContactPhone("+79991112233");
      shop.setRating(4.5);
      shop.setType(ShopType.RESTAURANT);
      shop.setPassword("shop_password");

      shopId = shopRepository.save(shop);
      assertNotNull(shopId);

      // 4. Создание продуктов
      Product product1 = new Product(
          null,
          "Пицца Пепперони",
          "Острая пицца с пепперони",
          500.0,
          600.0,
          ProductCategory.MAIN_DISH,
          true,
          Duration.ofMinutes(20)
      );
      product1Id = productRepository.saveForShop(shopId, product1);
      assertNotNull(product1Id);

      Product product2 = new Product(
          null,
          "Салат Цезарь",
          "Классический салат Цезарь",
          300.0,
          350.0,
          ProductCategory.MAIN_DISH,
          true,
          Duration.ofMinutes(10)
      );
      product2Id = productRepository.saveForShop(shopId, product2);
      assertNotNull(product2Id);

      // 5. Проверка продуктов
      List<Product> products = productRepository.findByShopId(shopId);
      assertEquals(2, products.size());

    } finally {
      // Очистка
      if (product1Id != null) productRepository.delete(product1Id);
      if (product2Id != null) productRepository.delete(product2Id);
      if (shopId != null) shopRepository.delete(shopId);
      if (workingHoursId != null) workingHoursRepository.delete(workingHoursId);
      if (shopAddressId != null) addressRepository.delete(shopAddressId);
    }
  }

  @Test
  @DisplayName("Создание магазина разных типов")
  void testDifferentShopTypes() throws SQLException {
    Long shopId1 = null;
    Long shopId2 = null;

    try {
      // Магазин типа RESTAURANT
      Shop restaurant = new Shop();
      restaurant.setNaming("Ресторан Премиум");
      restaurant.setEmailForAuth("restaurant_premium@test.com");
      restaurant.setPhoneForAuth("+79991113344");
      restaurant.setStatus(ShopStatus.APPROVED);
      restaurant.setType(ShopType.RESTAURANT);
      restaurant.setPassword("pass1");
      shopId1 = shopRepository.save(restaurant);

      // Магазин типа FAST_FOOD
      Shop fastFood = new Shop();
      fastFood.setNaming("ФастФуд Экспресс");
      fastFood.setEmailForAuth("fastfood@test.com");
      fastFood.setPhoneForAuth("+79991114455");
      fastFood.setStatus(ShopStatus.APPROVED);
      fastFood.setType(ShopType.FAST_FOOD);
      fastFood.setPassword("pass2");
      shopId2 = shopRepository.save(fastFood);

      // Проверка сохранения
      assertNotNull(shopId1);
      assertNotNull(shopId2);
      assertNotEquals(shopId1, shopId2);

      Optional<Shop> retrievedRestaurant = shopRepository.findById(shopId1);
      Optional<Shop> retrievedFastFood = shopRepository.findById(shopId2);

      assertTrue(retrievedRestaurant.isPresent());
      assertTrue(retrievedFastFood.isPresent());
      assertEquals(ShopType.RESTAURANT, retrievedRestaurant.get().getType());
      assertEquals(ShopType.FAST_FOOD, retrievedFastFood.get().getType());

    } finally {
      if (shopId1 != null) shopRepository.delete(shopId1);
      if (shopId2 != null) shopRepository.delete(shopId2);
    }
  }
}