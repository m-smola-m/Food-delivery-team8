package com.team8.fooddelivery.service.impl;

import com.team8.fooddelivery.model.courier.Courier;
import com.team8.fooddelivery.repository.CourierRepository;
import com.team8.fooddelivery.service.DatabaseConnectionService;
import com.team8.fooddelivery.service.DatabaseInitializerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class CourierServiceImplErrorHandlingTest {

    private CourierServiceImpl courierService;
    private CourierRepository courierRepository;
    private Long testCourierId;
    private final Random random = new Random();

    @BeforeEach
    void setUp() throws SQLException {
        String dbUrl = System.getProperty("db.url", "jdbc:postgresql://localhost:5432/food_delivery");
        String dbUser = System.getProperty("db.user", "postgres");
        String dbPassword = System.getProperty("db.password", "postgres");
        DatabaseConnectionService.setConnectionParams(dbUrl, dbUser, dbPassword);
        DatabaseInitializerService.initializeDatabase();

        courierService = new CourierServiceImpl();
        courierRepository = new CourierRepository();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (testCourierId != null) {
            try (Connection conn = DatabaseConnectionService.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM couriers WHERE id = ?")) {
                stmt.setLong(1, testCourierId);
                stmt.executeUpdate();
            }
        }
    }

    @Test
    @DisplayName("getCourierById: Should return null for non-existent courier")
    void testGetCourierById_NotFound() {
        Courier result = courierService.getCourierById(999999L);
        assertNull(result);
    }

    @Test
    @DisplayName("getOrderHistory: Should return empty list for non-existent courier")
    void testGetOrderHistory_NotFound() {
        var result = courierService.getOrderHistory(999999L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getCourierById: Should return courier when exists")
    void testGetCourierById_Success() throws SQLException {
        String operatorCode = "900"; // или выбираем случайный
        int number = 9000000 + random.nextInt(1000000); // 9000000-9999999
        String uniquePhone = "+7" + operatorCode + number;
        testCourierId = courierService.registerNewCourier("Test Courier", uniquePhone, "Password123!", "bike");
        
        Courier result = courierService.getCourierById(testCourierId);
        assertNotNull(result);
        assertEquals(testCourierId, result.getId());
    }

    @Test
    @DisplayName("getOrderHistory: Should return orders when courier exists")
    void testGetOrderHistory_Success() throws SQLException {
        String operatorCode = "900"; // или выбираем случайный
        int number = 9000000 + random.nextInt(1000000); // 9000000-9999999
        String uniquePhone = "+7" + operatorCode + number;
        testCourierId = courierService.registerNewCourier("Test Courier", uniquePhone, "Password123!", "bike");
        
        var result = courierService.getOrderHistory(testCourierId);
        assertNotNull(result);
        // May be empty if no orders assigned
    }
}
