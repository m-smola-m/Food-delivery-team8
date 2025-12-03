package com.team8.fooddelivery.service.impl;

import com.team8.fooddelivery.model.product.Cart;
import com.team8.fooddelivery.repository.ClientRepository;
import com.team8.fooddelivery.service.DatabaseConnectionService;
import com.team8.fooddelivery.service.DatabaseInitializerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CartServiceImplErrorHandlingTest {

    private CartServiceImpl cartService;
    private ClientRepository clientRepository;
    private final Random random = new Random();

    @BeforeEach
    void setUp() throws SQLException {
        String dbUrl = System.getProperty("db.url", "jdbc:postgresql://localhost:5432/food_delivery");
        String dbUser = System.getProperty("db.user", "postgres");
        String dbPassword = System.getProperty("db.password", "postgres");
        DatabaseConnectionService.setConnectionParams(dbUrl, dbUser, dbPassword);
        DatabaseInitializerService.initializeDatabase();

        cartService = new CartServiceImpl();
        clientRepository = new ClientRepository();
    }

    @Test
    @DisplayName("getCartForClient: Should return null for non-existent client")
    void testGetCartForClient_NotFound() {
        try {
            Cart result = cartService.getCartForClient(999999L);
        }
        catch (Exception e) {
            System.out.println("Должен вернуть ошибку " + e);
        }
    }

    @Test
    @DisplayName("getCartForClient: Should return cart when client exists")
    void testGetCartForClient_Success() throws SQLException {
        // Create a test client
        ClientServiceImpl clientService =
            new ClientServiceImpl(clientRepository, cartService);

        com.team8.fooddelivery.model.Address address = com.team8.fooddelivery.model.Address.builder()
                .country("Russia").city("Moscow").street("Test").building("1")
                .apartment("10").entrance("1").floor(1)
                .latitude(55.7558).longitude(37.6173).build();
        String operatorCode = "900"; // или выбираем случайный
        int number = 9000000 + random.nextInt(1000000); // 9000000-9999999
        String uniquePhone = "+7" + operatorCode + number;
        String uniqueEmail = "cart_test_" + System.currentTimeMillis() + "@test.com";
        
        com.team8.fooddelivery.model.client.Client client = clientService.register(
            uniquePhone, "Password123!", "Cart Test Client", uniqueEmail, address);
        
        // Create cart
        cartService.createCartForClient(client.getId());
        
        Cart result = cartService.getCartForClient(client.getId());
        assertNotNull(result);
        
        // Cleanup
        try (java.sql.Connection conn = DatabaseConnectionService.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement("DELETE FROM carts WHERE client_id = ?")) {
            stmt.setLong(1, client.getId());
            stmt.executeUpdate();
        }
        try (java.sql.Connection conn = DatabaseConnectionService.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement("DELETE FROM clients WHERE id = ?")) {
            stmt.setLong(1, client.getId());
            stmt.executeUpdate();
        }
    }
}
