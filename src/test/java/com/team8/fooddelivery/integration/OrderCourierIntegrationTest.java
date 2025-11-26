package com.team8.fooddelivery.integration;

import com.team8.fooddelivery.model.client.Client;
import com.team8.fooddelivery.model.client.ClientStatus;
import com.team8.fooddelivery.model.courier.Courier;
import com.team8.fooddelivery.model.courier.CourierStatus;
import com.team8.fooddelivery.model.order.Order;
import com.team8.fooddelivery.model.order.OrderStatus;
import com.team8.fooddelivery.model.shop.Shop;
import com.team8.fooddelivery.model.shop.ShopStatus;
import com.team8.fooddelivery.repository.*;
import com.team8.fooddelivery.util.DatabaseConnection;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Интеграционные тесты заказов и курьеров")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderCourierIntegrationTest {

  private CourierRepository courierRepository;
  private ClientRepository clientRepository;
  private ShopRepository shopRepository;
  private OrderRepository orderRepository;

  @BeforeAll
  static void setupDatabaseConnection() throws SQLException {
    String dbUrl = System.getProperty("db.url", "jdbc:postgresql://localhost:5432/food_delivery");
    String dbUser = System.getProperty("db.user", "postgres");
    String dbPassword = System.getProperty("db.password", "postgres");
    DatabaseConnection.setConnectionParams(dbUrl, dbUser, dbPassword);

    if (!DatabaseConnection.testConnection()) {
      throw new RuntimeException("Не удалось подключиться к базе данных");
    }
  }

  @BeforeEach
  void setUp() {
    courierRepository = new CourierRepository();
    clientRepository = new ClientRepository();
    shopRepository = new ShopRepository();
    orderRepository = new OrderRepository();
  }

  @Test
  @DisplayName("Создание курьера и заказа")
  void testCourierAndOrder() throws SQLException {
    Long courierId = null;
    Long clientId = null;
    Long shopId = null;
    Long orderId = null;

    try {
      // 1. Создание курьера
      Courier courier = new Courier();
      String uniqueCourierPhone = "+799944455" + System.currentTimeMillis() % 10000;
      courier.setName("Тестовый Курьер");
      courier.setPhoneNumber(uniqueCourierPhone);
      courier.setPassword("courier_password_hash");
      courier.setStatus(CourierStatus.OFF_SHIFT);
      courier.setTransportType("bike");
      courier.setCurrentBalance(0.0);
      courier.setBankCard(1234567890123456L);

      courierId = courierRepository.save(courier);
      assertNotNull(courierId);

      // 2. Создание клиента для заказа
      String uniqueClientPhone = "+799977788" + System.currentTimeMillis() % 10000;
      String uniqueClientEmail = "orderclient" + System.currentTimeMillis() + "@test.com";
      Client client = Client.builder()
          .name("Клиент для Заказа")
          .phone(uniqueClientPhone)
          .email(uniqueClientEmail)
          .passwordHash("hash")
          .status(ClientStatus.ACTIVE)
          .isActive(true)
          .orderHistory(List.of())
          .build();
      clientId = clientRepository.save(client);

      // 3. Создание магазина для заказа
      String uniqueOrderShopPhone = "+799955566" + System.currentTimeMillis() % 10000;
      String uniqueOrderShopEmail = "shop_order" + System.currentTimeMillis() + "@test.com";
      Shop shop = new Shop();
      shop.setNaming("Магазин для Заказа");
      shop.setEmailForAuth(uniqueOrderShopEmail);
      shop.setPhoneForAuth(uniqueOrderShopPhone);
      shop.setStatus(ShopStatus.APPROVED);
      shop.setPassword("shop_pass");
      shopId = shopRepository.save(shop);

      // 4. Создание заказа
      Order order = new Order();
      order.setStatus(OrderStatus.PENDING);
      order.setCustomerId(clientId);
      order.setRestaurantId(shopId);
      order.setDeliveryAddress("ул. Тестовая, д. 1");
      order.setTotalPrice(1000.0);
      order.setItems(List.of("Пицца", "Кола"));

      orderId = orderRepository.save(order);
      assertNotNull(orderId);

      // 5. Назначение курьера на заказ
      order.setId(orderId);
      order.setCourierId(courierId);
      order.setStatus(OrderStatus.DELIVERING);
      orderRepository.update(order);

      Optional<Order> retrievedOrder = orderRepository.findById(orderId);
      assertTrue(retrievedOrder.isPresent());
      assertEquals(courierId, retrievedOrder.get().getCourierId());
      assertEquals(OrderStatus.DELIVERING, retrievedOrder.get().getStatus());

    } finally {
      // Очистка
      if (orderId != null) orderRepository.delete(orderId);
      if (courierId != null) courierRepository.delete(courierId);
      if (clientId != null) clientRepository.delete(clientId);
      if (shopId != null) shopRepository.delete(shopId);
    }
  }

  @Test
  @DisplayName("Изменение статусов заказа")
  void testOrderStatusFlow() throws SQLException {
    Long clientId = null;
    Long shopId = null;
    Long orderId = null;

    try {
      // Создание клиента
      String uniqueClientPhone = "+799966677" + System.currentTimeMillis() % 10000;
      Client client = Client.builder()
          .name("Клиент Статус")
          .phone(uniqueClientPhone)
          .email("statusclient@test.com")
          .passwordHash("hash")
          .status(ClientStatus.ACTIVE)
          .isActive(true)
          .orderHistory(List.of())
          .build();
      clientId = clientRepository.save(client);

      // Создание магазина
      Shop shop = new Shop();
      shop.setNaming("Магазин Статус");
      shop.setEmailForAuth("status_shop@test.com");
      shop.setPhoneForAuth("+79991116677");
      shop.setStatus(ShopStatus.APPROVED);
      shop.setPassword("pass");
      shopId = shopRepository.save(shop);

      // Создание заказа
      Order order = new Order();
      order.setStatus(OrderStatus.PENDING);
      order.setCustomerId(clientId);
      order.setRestaurantId(shopId);
      order.setDeliveryAddress("ул. Статусная, д. 5");
      order.setTotalPrice(750.0);
      order.setItems(List.of("Бургер", "Фри"));

      orderId = orderRepository.save(order);

      // Проверка начального статуса
      Optional<Order> initialOrder = orderRepository.findById(orderId);
      assertTrue(initialOrder.isPresent());
      assertEquals(OrderStatus.PENDING, initialOrder.get().getStatus());

      // Изменение статуса на PREPARING
      order.setId(orderId);
      order.setStatus(OrderStatus.PREPARING);
      orderRepository.update(order);

      Optional<Order> preparingOrder = orderRepository.findById(orderId);
      assertTrue(preparingOrder.isPresent());
      assertEquals(OrderStatus.PREPARING, preparingOrder.get().getStatus());

      // Изменение статуса на DELIVERED
      order.setStatus(OrderStatus.COMPLETED);
      orderRepository.update(order);

      Optional<Order> deliveredOrder = orderRepository.findById(orderId);
      assertTrue(deliveredOrder.isPresent());
      assertEquals(OrderStatus.COMPLETED, deliveredOrder.get().getStatus());

    } finally {
      if (orderId != null) orderRepository.delete(orderId);
      if (clientId != null) clientRepository.delete(clientId);
      if (shopId != null) shopRepository.delete(shopId);
    }
  }

  @Test
  @DisplayName("Создание курьера с разными статусами")
  void testCourierStatuses() throws SQLException {
    Long courierId1 = null;
    Long courierId2 = null;

    try {
      // Курьер OFF_SHIFT
      Courier offShiftCourier = new Courier();
      offShiftCourier.setName("Курьер Отдых");
      offShiftCourier.setPhoneNumber("+79998880001");
      offShiftCourier.setPassword("pass1");
      offShiftCourier.setStatus(CourierStatus.OFF_SHIFT);
      offShiftCourier.setTransportType("car");
      courierId1 = courierRepository.save(offShiftCourier);

      // Курьер ON_SHIFT
      Courier onShiftCourier = new Courier();
      onShiftCourier.setName("Курьер Работа");
      onShiftCourier.setPhoneNumber("+79998880002");
      onShiftCourier.setPassword("pass2");
      onShiftCourier.setStatus(CourierStatus.ON_SHIFT);
      onShiftCourier.setTransportType("bike");
      courierId2 = courierRepository.save(onShiftCourier);

      // Проверка статусов
      Optional<Courier> retrievedOffShift = courierRepository.findById(courierId1);
      Optional<Courier> retrievedOnShift = courierRepository.findById(courierId2);

      assertTrue(retrievedOffShift.isPresent());
      assertTrue(retrievedOnShift.isPresent());
      assertEquals(CourierStatus.OFF_SHIFT, retrievedOffShift.get().getStatus());
      assertEquals(CourierStatus.ON_SHIFT, retrievedOnShift.get().getStatus());

    } finally {
      if (courierId1 != null) courierRepository.delete(courierId1);
      if (courierId2 != null) courierRepository.delete(courierId2);
    }
  }
}