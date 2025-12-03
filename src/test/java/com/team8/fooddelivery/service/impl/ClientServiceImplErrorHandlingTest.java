package com.team8.fooddelivery.service.impl;

import com.team8.fooddelivery.model.client.Client;
import com.team8.fooddelivery.repository.ClientRepository;
import com.team8.fooddelivery.service.DatabaseConnectionService;
import com.team8.fooddelivery.service.DatabaseInitializerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ClientServiceImplErrorHandlingTest {

    private ClientServiceImpl clientService;
    private CartServiceImpl cartService;
    private ClientRepository clientRepository;
    private Long testClientId;
    private final Random random = new Random();

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseInitializerService.initializeDatabase();
        String dbUrl = System.getProperty("db.url", "jdbc:postgresql://localhost:5432/food_delivery");
        String dbUser = System.getProperty("db.user", "postgres");
        String dbPassword = System.getProperty("db.password", "postgres");
        DatabaseConnectionService.setConnectionParams(dbUrl, dbUser, dbPassword);

        cartService = new CartServiceImpl();
        clientRepository = new ClientRepository();
        clientService = new ClientServiceImpl(clientRepository, cartService);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (testClientId != null) {
            try (Connection conn = DatabaseConnectionService.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM clients WHERE id = ?")) {
                stmt.setLong(1, testClientId);
                stmt.executeUpdate();
            }
        }
    }

    @Test
    @DisplayName("getByPhone: Should return null for non-existent phone")
    void testGetByPhone_NotFound() {
        // Use a phone number that definitely doesn't exist
        String nonExistentPhone = "+7999" + (System.currentTimeMillis() % 10000000 + 9999999);
        Client result = clientService.getByPhone(nonExistentPhone);
        assertNull(result);
    }

    @Test
    @DisplayName("getByEmail: Should return null for non-existent email")
    void testGetByEmail_NotFound() {
        Client result = clientService.getByEmail("nonexistent@example.com");
        assertNull(result);
    }

    @Test
    @DisplayName("getByPhone: Should return client when exists")
    void testGetByPhone_Success() throws SQLException {
        com.team8.fooddelivery.model.Address address = com.team8.fooddelivery.model.Address.builder()
                .country("Russia").city("Moscow").street("Test").building("1")
                .apartment("10").entrance("1").floor(1)
                .latitude(55.7558).longitude(37.6173).build();
        
        String uniquePhone = "+7999" + (System.currentTimeMillis() % 10000000 + 999999);
        System.out.println(uniquePhone);
        String uniqueEmail = "phone_test_" + System.currentTimeMillis() + "@test.com";
        
        Client client = clientService.register(uniquePhone, "Password123!", "Phone Test Client", uniqueEmail, address);
        testClientId = client.getId();
        
        Client result = clientService.getByPhone(uniquePhone);
        assertNotNull(result);
        assertEquals(uniquePhone, result.getPhone());
    }

    @Test
    @DisplayName("getByEmail: Should return client when exists")
    void testGetByEmail_Success() throws SQLException {
        com.team8.fooddelivery.model.Address address = com.team8.fooddelivery.model.Address.builder()
                .country("Russia").city("Moscow").street("Test").building("1")
                .apartment("10").entrance("1").floor(1)
                .latitude(55.7558).longitude(37.6173).build();

        String uniqueEmail = "email_test_" + System.currentTimeMillis() + "@test.com";
        String operatorCode = "900"; // или выбираем случайный
        int number = 9000000 + random.nextInt(1000000); // 9000000-9999999
        String uniquePhone = "+7" + operatorCode + number;
        Client client = clientService.register(uniquePhone, "Password123!", "Email Test Client", uniqueEmail, address);
        testClientId = client.getId();
        
        Client result = clientService.getByEmail(uniqueEmail);
        assertNotNull(result);
        assertEquals(uniqueEmail, result.getEmail());
    }

    @Test
    @DisplayName("listAll: Should return list of clients")
    void testListAll() {
        var result = clientService.listAll();
        assertNotNull(result);
        // May be empty if no clients exist
    }

    @Test
    @DisplayName("getOrderHistory: Should return empty list for non-existent client")
    void testGetOrderHistory_NotFound() {
        var result = clientService.getOrderHistory(999999L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getById: Should return null for non-existent client")
    void testGetById_NotFound() {
        Client result = clientService.getById(999999L);
        assertNull(result);
    }
}
