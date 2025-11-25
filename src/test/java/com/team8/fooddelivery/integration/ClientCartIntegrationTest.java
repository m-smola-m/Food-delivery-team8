package com.team8.fooddelivery.integration;

import com.team8.fooddelivery.model.*;
import com.team8.fooddelivery.repository.*;
import com.team8.fooddelivery.util.DatabaseConnection;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Интеграционные тесты клиента и корзины")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClientCartIntegrationTest {

    private AddressRepository addressRepository;
    private ClientRepository clientRepository;
    private CartRepository cartRepository;
    private ShopRepository shopRepository;
    private ProductRepository productRepository;

    @BeforeAll
    static void setupDatabaseConnection() {
        String dbUrl = System.getProperty("db.url", "jdbc:postgresql://localhost:5432/food_delivery");
        String dbUser = System.getProperty("db.user", "postgres");
        String dbPassword = System.getProperty("db.password", "postgres");
        DatabaseConnection.setConnectionParams(dbUrl, dbUser, dbPassword);

        if (!DatabaseConnection.testConnection()) {
            throw new RuntimeException(
                    "Не удалось подключиться к базе данных.\n" +
                            "Убедитесь, что:\n" +
                            "1. PostgreSQL запущен\n" +
                            "2. База данных 'food_delivery' создана\n" +
                            "3. Схема создана (выполнен schema.sql)\n" +
                            "4. Параметры подключения корректны"
            );
        }
    }

    @BeforeEach
    void setUp() {
        addressRepository = new AddressRepository();
        clientRepository = new ClientRepository();
        cartRepository = new CartRepository();
        shopRepository = new ShopRepository();
        productRepository = new ProductRepository();
    }

    @Test
    @DisplayName("Полный цикл: Регистрация клиента -> Создание корзины -> Добавление товаров")
    void testFullClientCartFlow() throws SQLException {
        Long addressId = null;
        Long clientId = null;
        Long cartId = null;
        Long shopId = null;
        Long product1Id = null;
        Long product2Id = null;
        Long item1Id = null;
        Long item2Id = null;

        try {
            // 1. Создание адреса
            Address address = Address.builder()
                    .country("Россия")
                    .city("Санкт-Петербург")
                    .street("Невский проспект")
                    .building("1")
                    .apartment("10")
                    .latitude(59.9343)
                    .longitude(30.3351)
                    .build();
            addressId = addressRepository.save(address);
            assertNotNull(addressId);

            // 2. Создание клиента
            String uniquePhone = "+799988877" + System.currentTimeMillis() % 10000;
            String uniqueEmail = "integration" + System.currentTimeMillis() + "@test.com";
            Client client = Client.builder()
                    .name("Интеграционный Тест")
                    .phone(uniquePhone)
                    .email(uniqueEmail)
                    .passwordHash("hashed_password_123")
                    .address(address)
                    .status(ClientStatus.ACTIVE)
                    .isActive(true)
                    .createdAt(Instant.now())
                    .orderHistory(List.of())
                    .build();
            clientId = clientRepository.save(client);
            assertNotNull(clientId);
            assertTrue(clientId > 0);

            // 3. Создание корзины
            Cart cart = Cart.builder()
                    .clientId(clientId)
                    .items(List.of())
                    .build();
            cartId = cartRepository.save(cart);
            assertNotNull(cartId);

            // 4. Создание магазина и продуктов
            Shop testShop = new Shop();
            String uniqueShopPhone = "+799911122" + System.currentTimeMillis() % 10000;
            String uniqueShopEmail = "shopcart" + System.currentTimeMillis() + "@test.com";
            testShop.setNaming("Тестовый Магазин для Корзины");
            testShop.setEmailForAuth(uniqueShopEmail);
            testShop.setPhoneForAuth(uniqueShopPhone);
            testShop.setStatus(ShopStatus.APPROVED);
            testShop.setPassword("password");
            shopId = shopRepository.save(testShop);

            Product product1 = new Product(
                    null, "Пицца Маргарита", "Описание", 500.0, 600.0,
                    ProductCategory.MAIN_DISH, true, Duration.ofMinutes(20)
            );
            product1Id = productRepository.saveForShop(shopId, product1);

            Product product2 = new Product(
                    null, "Кола", "Описание", 100.0, 150.0,
                    ProductCategory.DRINK, true, Duration.ofMinutes(5)
            );
            product2Id = productRepository.saveForShop(shopId, product2);

            // 5. Добавление товаров в корзину
            CartItem item1 = CartItem.builder()
                    .cartId(cartId)
                    .productId(product1Id)
                    .productName("Пицца Маргарита")
                    .quantity(1)
                    .price(500.0)
                    .build();
            item1Id = cartRepository.saveCartItem(item1);
            assertNotNull(item1Id);

            CartItem item2 = CartItem.builder()
                    .cartId(cartId)
                    .productId(product2Id)
                    .productName("Кола")
                    .quantity(2)
                    .price(100.0)
                    .build();
            item2Id = cartRepository.saveCartItem(item2);
            assertNotNull(item2Id);

            // 6. Проверка корзины
            Optional<Cart> retrievedCart = cartRepository.findByClientId(clientId);
            assertTrue(retrievedCart.isPresent());
            assertEquals(2, retrievedCart.get().getItems().size());

        } finally {
            // Очистка в обратном порядке зависимостей
            if (item1Id != null) cartRepository.deleteCartItem(item1Id);
            if (item2Id != null) cartRepository.deleteCartItem(item2Id);
            if (cartId != null) cartRepository.delete(cartId);
            if (product1Id != null) productRepository.delete(product1Id);
            if (product2Id != null) productRepository.delete(product2Id);
            if (shopId != null) shopRepository.delete(shopId);
            if (clientId != null) clientRepository.delete(clientId);
            if (addressId != null) addressRepository.delete(addressId);
        }
    }

    @Test
    @DisplayName("Создание клиента с адресом")
    void testClientWithAddress() throws SQLException {
        Long addressId = null;
        Long clientId = null;

        try {
            // Создание адреса
            Address address = Address.builder()
                    .country("Россия")
                    .city("Москва")
                    .street("Арбат")
                    .building("15")
                    .apartment("25")
                    .latitude(55.751244)
                    .longitude(37.618423)
                    .build();
            addressId = addressRepository.save(address);
            assertNotNull(addressId);

            // Создание клиента
            String uniquePhone = "+799933344" + System.currentTimeMillis() % 10000;
            String uniqueEmail = "clientaddr" + System.currentTimeMillis() + "@test.com";
            Client client = Client.builder()
                    .name("Клиент с Адресом")
                    .phone(uniquePhone)
                    .email(uniqueEmail)
                    .passwordHash("password_hash")
                    .address(address)
                    .status(ClientStatus.ACTIVE)
                    .isActive(true)
                    .createdAt(Instant.now())
                    .orderHistory(List.of())
                    .build();
            clientId = clientRepository.save(client);
            assertNotNull(clientId);

            // Проверка, что клиент сохранен с адресом
            Optional<Client> retrievedClient = clientRepository.findById(clientId);
            assertTrue(retrievedClient.isPresent());
            assertNotNull(retrievedClient.get().getAddress());
            assertEquals("Москва", retrievedClient.get().getAddress().getCity());

        } finally {
            if (clientId != null) clientRepository.delete(clientId);
            if (addressId != null) addressRepository.delete(addressId);
        }
    }
}
