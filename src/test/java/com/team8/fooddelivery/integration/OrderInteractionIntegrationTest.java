package com.team8.fooddelivery.integration;

import com.team8.fooddelivery.model.*;
import com.team8.fooddelivery.model.order.Order;
import com.team8.fooddelivery.model.client.Client;
import com.team8.fooddelivery.model.client.ClientStatus;
import com.team8.fooddelivery.model.courier.Courier;
import com.team8.fooddelivery.model.courier.CourierStatus;
import com.team8.fooddelivery.model.order.OrderStatus;
import com.team8.fooddelivery.model.product.CartItem;
import com.team8.fooddelivery.model.shop.Shop;
import com.team8.fooddelivery.model.shop.ShopStatus;
import com.team8.fooddelivery.repository.*;
import com.team8.fooddelivery.service.DatabaseConnectionService;
import com.team8.fooddelivery.service.DatabaseInitializerService;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Интеграционные сценарии заказов с участием клиентов, магазинов и курьеров")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderInteractionIntegrationTest {

  private static final Logger log = LoggerFactory.getLogger(OrderInteractionIntegrationTest.class);

  private CourierRepository courierRepository;
  private ClientRepository clientRepository;
  private ShopRepository shopRepository;
  private OrderRepository orderRepository;
  private AddressRepository addressRepository;

  @BeforeAll
  static void setupDatabaseConnection() {
    String dbUrl = System.getProperty("db.url", "jdbc:postgresql://localhost:5432/food_delivery");
    String dbUser = System.getProperty("db.user", "fooddelivery_user");
    String dbPassword = System.getProperty("db.password", "fooddelivery_pass");

    DatabaseConnectionService.setConnectionParams(dbUrl, dbUser, dbPassword);

    if (!DatabaseConnectionService.testConnection()) {
      throw new RuntimeException("Не удалось подключиться к базе данных");
    }
    DatabaseInitializerService.fullCleanDatabase();
    DatabaseInitializerService.initializeDatabase();
  }

  @BeforeEach
  void setUp() {
    courierRepository = new CourierRepository();
    clientRepository = new ClientRepository();
    shopRepository = new ShopRepository();
    orderRepository = new OrderRepository();
    addressRepository = new AddressRepository();
  }

  @AfterEach
  void cleanUp() throws SQLException {
    DatabaseInitializerService.cleanTestData();
  }

  @Test
  @Disabled("pending schema sync")
  @DisplayName("Клиент оформляет заказ в магазине с полным адресом и товарами")
  void customerCreatesOrderWithAddressAndItems() throws SQLException {
    Long clientId = saveActiveClient("client-addr-" + UUID.randomUUID());
    Long shopId = saveApprovedShop("shop-addr-" + UUID.randomUUID());

    Address deliveryAddress = sampleAddress("Питер", "Невский", "10", "77");
    List<CartItem> items = List.of(
        CartItem.builder().productId(101L).productName("Пицца Маргарита").quantity(1).price(750.0).build(),
        CartItem.builder().productId(102L).productName("Лимонад").quantity(2).price(120.0).build()
    );

    Order order = new Order();
    order.setStatus(OrderStatus.PENDING);
    order.setCustomerId(clientId);
    order.setRestaurantId(shopId);
    order.setDeliveryAddress(deliveryAddress);
    order.setTotalPrice(items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum());
    order.setItems(items);
    order.setCreatedAt(Instant.now());

    log.info("Создаем заказ клиента {} для магазина {}", clientId, shopId);
    Long orderId = orderRepository.save(order);

    Optional<Order> storedOrderOpt = orderRepository.findById(orderId);
    assertTrue(storedOrderOpt.isPresent(), "Заказ клиента не найден");
    Order storedOrder = storedOrderOpt.get();

    assertEquals(orderId, storedOrder.getId());
    assertEquals(order.getTotalPrice(), storedOrder.getTotalPrice());
    assertEquals(OrderStatus.PENDING, storedOrder.getStatus());

    // Проверяем адрес доставки
    assertNotNull(storedOrder.getDeliveryAddress(), "Адрес доставки не должен быть null");
    assertEquals(deliveryAddress.getCity(), storedOrder.getDeliveryAddress().getCity());
    assertEquals(deliveryAddress.getStreet(), storedOrder.getDeliveryAddress().getStreet());
    assertEquals(deliveryAddress.getBuilding(), storedOrder.getDeliveryAddress().getBuilding());
    assertEquals(deliveryAddress.getApartment(), storedOrder.getDeliveryAddress().getApartment());

    // Проверяем элементы заказа
    assertEquals(2, storedOrder.getItems().size());
    assertEquals("Пицца Маргарита", storedOrder.getItems().get(0).getProductName());
    assertEquals("Лимонад", storedOrder.getItems().get(1).getProductName());

    log.info("Заказ успешно создан с ID: {}, адрес: {}", orderId, storedOrder.getDeliveryAddress());
  }

  @Test
  @Disabled("pending schema sync")
  @DisplayName("Курьер берет заказ и довозит его до клиента")
  void courierTakesAndCompletesOrder() throws SQLException {
    Long courierId = saveCourierWithStatus(CourierStatus.ON_SHIFT, "bike");
    Long clientId = saveActiveClient("client-courier-" + UUID.randomUUID());
    Long shopId = saveApprovedShop("shop-courier-" + UUID.randomUUID());

    Order order = new Order();
    order.setStatus(OrderStatus.PENDING);
    order.setCustomerId(clientId);
    order.setRestaurantId(shopId);
    order.setDeliveryAddress(sampleAddress("Москва", "Тверская", "5", "21"));
    order.setTotalPrice(450.0);
    order.setItems(List.of(CartItem.builder()
        .productId(201L)
        .productName("Суп дня")
        .quantity(1)
        .price(450.0)
        .build()));

    log.info("Создаем заказ без курьера для клиента {}", clientId);
    Long orderId = orderRepository.save(order);

    // Получаем заказ для проверки начального состояния
    Optional<Order> initialOrder = orderRepository.findById(orderId);
    assertTrue(initialOrder.isPresent());
    assertNull(initialOrder.get().getCourierId(), "Курьер не должен быть назначен изначально");

    log.info("Назначаем курьера {} на заказ {}", courierId, orderId);
    Order orderToUpdate = initialOrder.get();
    orderToUpdate.setCourierId(courierId);
    orderToUpdate.setStatus(OrderStatus.DELIVERING);
    orderRepository.update(orderToUpdate);

    List<Order> courierOrders = orderRepository.findByCourierId(courierId);
    Optional<Order> deliveringOrder = courierOrders.stream()
        .filter(o -> orderId.equals(o.getId()))
        .findFirst();
    assertTrue(deliveringOrder.isPresent(), "Заказ не найден в списке заказов курьера");
    assertEquals(OrderStatus.DELIVERING, deliveringOrder.get().getStatus());
    assertEquals(courierId, deliveringOrder.get().getCourierId());

    log.info("Курьер {} завершает доставку заказа {}", courierId, orderId);
    Order orderToComplete = deliveringOrder.get();
    orderToComplete.setStatus(OrderStatus.COMPLETED);
    orderRepository.update(orderToComplete);

    Optional<Order> completedOrder = orderRepository.findById(orderId);
    assertTrue(completedOrder.isPresent(), "Завершенный заказ не найден");
    assertEquals(OrderStatus.COMPLETED, completedOrder.get().getStatus());
    assertEquals(courierId, completedOrder.get().getCourierId());

    log.info("Заказ {} успешно завершен курьером {}", orderId, courierId);
  }

  @Test
  @Disabled("pending schema sync")
  @DisplayName("Магазин видит заказы и может обновить цену и статус готовности")
  void shopUpdatesOrderPriceAndStatus() throws SQLException {
    Long clientId = saveActiveClient("client-shop-" + UUID.randomUUID());
    Long shopId = saveApprovedShop("shop-order-" + UUID.randomUUID());

    Order order = new Order();
    order.setStatus(OrderStatus.PENDING);
    order.setCustomerId(clientId);
    order.setRestaurantId(shopId);
    order.setDeliveryAddress(sampleAddress("Казань", "Баумана", "3", "15"));
    order.setTotalPrice(300.0);
    order.setItems(List.of(CartItem.builder()
        .productId(301L)
        .productName("Салат")
        .quantity(1)
        .price(300.0)
        .build()));

    log.info("Магазин {} принимает заказ от клиента {}", shopId, clientId);
    Long orderId = orderRepository.save(order);

    Optional<Order> pendingOrder = orderRepository.findById(orderId);
    assertTrue(pendingOrder.isPresent());
    assertEquals(OrderStatus.PENDING, pendingOrder.get().getStatus());
    assertEquals(300.0, pendingOrder.get().getTotalPrice());

    log.info("Магазин {} обновляет цену и статус заказа {}", shopId, orderId);
    Order orderToUpdate = pendingOrder.get();
    orderToUpdate.setTotalPrice(350.0);
    orderToUpdate.setStatus(OrderStatus.PREPARING);
    orderRepository.update(orderToUpdate);

    Optional<Order> updatedOrder = orderRepository.findById(orderId);
    assertTrue(updatedOrder.isPresent());
    assertEquals(350.0, updatedOrder.get().getTotalPrice());
    assertEquals(OrderStatus.PREPARING, updatedOrder.get().getStatus());

    log.info("Заказ {} обновлен: цена={}, статус={}",
        orderId, updatedOrder.get().getTotalPrice(), updatedOrder.get().getStatus());
  }

  @Test
  @Disabled("pending schema sync")
  @DisplayName("Поиск заказов по различным критериям")
  void findOrdersByVariousCriteria() throws SQLException {
    Long clientId = saveActiveClient("client-search-" + UUID.randomUUID());
    Long shopId = saveApprovedShop("shop-search-" + UUID.randomUUID());
    Long courierId = saveCourierWithStatus(CourierStatus.ON_SHIFT, "car");

    // Создаем несколько заказов
    Order order1 = createSampleOrder(clientId, shopId, OrderStatus.PENDING, 100.0);
    Order order2 = createSampleOrder(clientId, shopId, OrderStatus.PREPARING, 200.0);
    Order order3 = createSampleOrder(clientId, shopId, OrderStatus.DELIVERING, 300.0);

    order3.setCourierId(courierId);
    Long order3Id = orderRepository.save(order3);
    order3.setId(order3Id);

    // Обновляем order3 с курьером
    orderRepository.update(order3);

    // Тестируем поиск по клиенту
    List<Order> clientOrders = orderRepository.findByCustomerId(clientId);
    assertTrue(clientOrders.size() >= 3, "Должны найти все заказы клиента");

    // Тестируем поиск по курьеру
    List<Order> courierOrders = orderRepository.findByCourierId(courierId);
    assertEquals(1, courierOrders.size(), "Курьер должен иметь один заказ");
    assertEquals(order3Id, courierOrders.get(0).getId());

    // Тестируем поиск по статусу
    List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);
    assertTrue(pendingOrders.stream().anyMatch(o -> o.getStatus() == OrderStatus.PENDING));

    // Тестируем получение всех заказов
    List<Order> allOrders = orderRepository.findAll();
    assertTrue(allOrders.size() >= 3, "Должны найти все созданные заказы");
  }

  @Test
  @Disabled("pending schema sync")
  @DisplayName("Обновление адреса доставки в существующем заказе")
  void updateDeliveryAddressInExistingOrder() throws SQLException {
    Long clientId = saveActiveClient("client-addr-update-" + UUID.randomUUID());
    Long shopId = saveApprovedShop("shop-addr-update-" + UUID.randomUUID());

    Order order = new Order();
    order.setStatus(OrderStatus.PENDING);
    order.setCustomerId(clientId);
    order.setRestaurantId(shopId);
    order.setDeliveryAddress(sampleAddress("Москва", "Старая", "1", "1"));
    order.setTotalPrice(500.0);
    order.setItems(List.of(CartItem.builder()
        .productId(401L)
        .productName("Чай")
        .quantity(1)
        .price(500.0)
        .build()));

    Long orderId = orderRepository.save(order);

    // Получаем заказ и проверяем исходный адрес
    Optional<Order> originalOrder = orderRepository.findById(orderId);
    assertTrue(originalOrder.isPresent());
    assertEquals("Старая", originalOrder.get().getDeliveryAddress().getStreet());

    // Обновляем адрес
    Address newAddress = sampleAddress("Москва", "Новая", "2", "2");
    Order orderToUpdate = originalOrder.get();
    orderToUpdate.setDeliveryAddress(newAddress);
    orderRepository.update(orderToUpdate);

    // Проверяем обновленный адрес
    Optional<Order> updatedOrder = orderRepository.findById(orderId);
    assertTrue(updatedOrder.isPresent());
    assertEquals("Новая", updatedOrder.get().getDeliveryAddress().getStreet());
    assertEquals("2", updatedOrder.get().getDeliveryAddress().getBuilding());

    log.info("Адрес заказа {} успешно обновлен", orderId);
  }

  private Order createSampleOrder(Long clientId, Long shopId, OrderStatus status, Double price) throws SQLException {
    Order order = new Order();
    order.setStatus(status);
    order.setCustomerId(clientId);
    order.setRestaurantId(shopId);
    order.setDeliveryAddress(sampleAddress("Город", "Улица", "1", "1"));
    order.setTotalPrice(price);
    order.setItems(List.of(CartItem.builder()
        .productId(999L)
        .productName("Тестовый товар")
        .quantity(1)
        .price(price)
        .build()));
    orderRepository.save(order);
    return order;
  }

  private Long saveActiveClient(String key) throws SQLException {
    Client client = Client.builder()
        .name("Клиент " + key)
        .phone("+7999" + Math.abs(key.hashCode() % 1_000_000))
        .email(key + "@test.com")
        .passwordHash("hash")
        .status(ClientStatus.ACTIVE)
        .isActive(true)
        .orderHistory(List.of())
        .build();
    Long id = clientRepository.save(client);
    log.info("Создан клиент {}", id);
    return id;
  }

  private Long saveApprovedShop(String key) throws SQLException {
    Shop shop = new Shop();
    shop.setNaming("Магазин " + key);
    shop.setEmailForAuth(key + "@shop.test");
    shop.setPhoneForAuth("+7888" + Math.abs(key.hashCode() % 1_000_000));
    shop.setStatus(ShopStatus.APPROVED);
    shop.setPassword("shop_pass");
    Long id = shopRepository.save(shop);
    log.info("Создан магазин {}", id);
    return id;
  }

  private Long saveCourierWithStatus(CourierStatus status, String transport) throws SQLException {
    Courier courier = new Courier();
    courier.setName("Курьер " + status);
    courier.setPhoneNumber("+777" + System.nanoTime() % 1_000_000);
    courier.setPassword("courier_pass");
    courier.setStatus(status);
    courier.setTransportType(transport);
    courier.setCurrentBalance(0.0);
    courier.setBankCard(1111222233334444L);
    Long id = courierRepository.save(courier);
    log.info("Создан курьер {} со статусом {}", id, status);
    return id;
  }

  private Address sampleAddress(String city, String street, String building, String apartment) {
    return Address.builder()
        .country("Россия")
        .city(city)
        .street(street)
        .building(building)
        .apartment(apartment)
        .entrance("1")
        .floor(2)
        .addressNote("Проверка доставки")
        .build();
  }
}