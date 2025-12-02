package com.team8.fooddelivery.service.impl;

import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.model.client.Client;
import com.team8.fooddelivery.model.client.ClientStatus;
import com.team8.fooddelivery.model.client.PaymentMethodForOrder;
import com.team8.fooddelivery.model.order.Order;
import com.team8.fooddelivery.model.order.OrderStatus;
import com.team8.fooddelivery.model.product.Cart;
import com.team8.fooddelivery.model.product.CartItem;
import com.team8.fooddelivery.repository.ClientRepository;
import com.team8.fooddelivery.service.DatabaseConnectionService;
import com.team8.fooddelivery.service.DatabaseInitializerService;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderServiceImplIntegrationTest {

    private OrderServiceImpl orderService;
    private CartServiceImpl cartService;
    private ClientServiceImpl clientService;
    private ClientRepository clientRepository;
    private static Long testClientId;
    private static String testPhone;
    private static String testEmail;

    @BeforeAll
    static void setupDatabase() {
        String dbUrl = System.getProperty("db.url", "jdbc:postgresql://localhost:5432/food_delivery");
        String dbUser = System.getProperty("db.user", "fooddelivery_user");
        String dbPassword = System.getProperty("db.password", "fooddelivery_pass");
        DatabaseConnectionService.setConnectionParams(dbUrl, dbUser, dbPassword);
        DatabaseInitializerService.initializeDatabase();
    }

    @AfterAll
    static void cleanup() {
        if (testClientId != null) {
            try (Connection conn = DatabaseConnectionService.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM orders WHERE customer_id = ?")) {
                stmt.setLong(1, testClientId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                // Ignore
            }
            try (Connection conn = DatabaseConnectionService.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM carts WHERE client_id = ?")) {
                stmt.setLong(1, testClientId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                // Ignore
            }
            try (Connection conn = DatabaseConnectionService.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM clients WHERE id = ?")) {
                stmt.setLong(1, testClientId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                // Ignore
            }
        }
    }

    @BeforeEach
    void setUp() {
        cartService = new CartServiceImpl();
        clientRepository = new ClientRepository();
        clientService = new ClientServiceImpl(clientRepository, cartService);
        orderService = new OrderServiceImpl(cartService);
        long suffix = System.currentTimeMillis() % 10000000000L;
        testPhone = "+7" + String.format("%010d", suffix).substring(0, 10);
        testEmail = "order_test_" + System.currentTimeMillis() + "@test.com";
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("Place order with CARD payment")
    void testPlaceOrderCard() throws SQLException {
        Address address = Address.builder()
                .country("Russia").city("Moscow").street("Test").building("1")
                .apartment("10").entrance("1").floor(1)
                .latitude(55.7558).longitude(37.6173).build();
        Client client = clientService.register(testPhone, "Password123!", "Test User", testEmail, address);
        testClientId = client.getId();

        // Add items to cart
        Long productId = createTestProduct();
        CartItem item = CartItem.builder()
                .productId(productId)
                .productName("Test Product")
                .quantity(2)
                .price(100.0)
                .build();
        cartService.addItem(testClientId, item);

        Address deliveryAddress = Address.builder()
                .country("Russia").city("Moscow").street("Delivery").building("2")
                .apartment("20").entrance("2").floor(2)
                .latitude(55.7558).longitude(37.6173).build();

        Order order = orderService.placeOrder(testClientId, deliveryAddress, PaymentMethodForOrder.CARD);
        assertNotNull(order);
        assertNotNull(order.getId());
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("Place order with CASH payment")
    void testPlaceOrderCash() throws SQLException {
        if (testClientId == null) {
            Address address = Address.builder()
                    .country("Russia").city("Moscow").street("Test").building("1")
                    .apartment("10").entrance("1").floor(1)
                    .latitude(55.7558).longitude(37.6173).build();
            long suffix = System.currentTimeMillis() % 10000000000L;
            String phone = "+7" + String.format("%010d", suffix).substring(0, 10);
            Client client = clientService.register(phone, "Password123!", "Test", testEmail + "cash", address);
            testClientId = client.getId();
        }

        Long productId = createTestProduct();
        CartItem item = CartItem.builder()
                .productId(productId)
                .productName("Test Product 2")
                .quantity(1)
                .price(50.0)
                .build();
        cartService.addItem(testClientId, item);

        Address deliveryAddress = Address.builder()
                .country("Russia").city("Moscow").street("Delivery").building("3")
                .apartment("30").entrance("3").floor(3)
                .latitude(55.7558).longitude(37.6173).build();

        Order order = orderService.placeOrder(testClientId, deliveryAddress, PaymentMethodForOrder.CASH);
        assertNotNull(order);
        assertNotNull(order.getId());
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("Place order with ONLINE payment method")
    void testPlaceOrderOnline() throws SQLException {
        if (testClientId == null) {
            Address address = Address.builder()
                    .country("Russia").city("Moscow").street("Test").building("1")
                    .apartment("10").entrance("1").floor(1)
                    .latitude(55.7558).longitude(37.6173).build();
            long suffix = System.currentTimeMillis() % 10000000000L;
            String phone = "+7" + String.format("%010d", suffix).substring(0, 10);
            Client client = clientService.register(phone, "Password123!", "Test", testEmail + "online", address);
            testClientId = client.getId();
        }

        cartService.clear(testClientId);
        Long productId = createTestProduct();
        CartItem item = CartItem.builder()
                .productId(productId)
                .productName("Test Product 3")
                .quantity(1)
                .price(75.0)
                .build();
        cartService.addItem(testClientId, item);

        Address deliveryAddress = Address.builder()
                .country("Russia").city("Moscow").street("Delivery").building("4")
                .apartment("40").entrance("4").floor(4)
                .latitude(55.7558).longitude(37.6173).build();

        Order order = orderService.placeOrder(testClientId, deliveryAddress, PaymentMethodForOrder.ONLINE);
        assertNotNull(order);
        assertNotNull(order.getId());
        // ONLINE обрабатывается как CARD, поэтому статус должен быть PAID
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    private Long createTestProduct() throws SQLException {
        // Create test product
        String sql = "INSERT INTO products (name, description, weight, price, category, is_available, cooking_time_minutes, shop_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING product_id";

        try (Connection conn = DatabaseConnectionService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Create test shop first
            Long shopId = createTestShop();

            String uniqueName = "Test Product " + System.currentTimeMillis() + "_" + (int) (Math.random() * 10000);
            stmt.setString(1, uniqueName);
            stmt.setString(2, "Test Description");
            stmt.setDouble(3, 250.0);
            stmt.setDouble(4, 100.0);
            stmt.setString(5, "MAIN_DISH");
            stmt.setBoolean(6, true);
            stmt.setLong(7, 600L);
            stmt.setLong(8, shopId);

            var rs = stmt.executeQuery();
            if (rs.next()) return rs.getLong(1);
        }
        throw new SQLException("Не удалось создать тестовый продукт");
    }

    private Long createTestShop() throws SQLException {
        String uniqueName = "Test Shop " + System.currentTimeMillis();
        String uniqueEmail = "test_shop_" + System.currentTimeMillis() + "@test.com";
        String uniquePhone = "+7999" + (System.currentTimeMillis() % 10000000);

        String sql = "INSERT INTO shops (naming, description, status, registration_date, email_for_auth, phone_for_auth, password, public_email, public_phone, owner_name, owner_contact_phone, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING shop_id";
        try (Connection conn = DatabaseConnectionService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, uniqueName);
            stmt.setString(2, "Test Description");
            stmt.setString(3, "APPROVED");
            stmt.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
            stmt.setString(5, uniqueEmail);
            stmt.setString(6, uniquePhone);
            stmt.setString(7, "test_password");
            stmt.setString(8, "public_" + uniqueEmail);
            stmt.setString(9, "+7444" + (System.currentTimeMillis() % 10000000));
            stmt.setString(10, "Owner " + uniqueName);
            stmt.setString(11, "+7555" + (System.currentTimeMillis() % 10000000));
            stmt.setString(12, "RESTAURANT");

            var rs = stmt.executeQuery();
            if (rs.next()) return rs.getLong(1);
        }
        throw new SQLException("Не удалось создать тестовый магазин");
    }
}
