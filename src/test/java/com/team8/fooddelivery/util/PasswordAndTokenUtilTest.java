package com.team8.fooddelivery.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordAndTokenUtilTest {

    @Test
    @DisplayName("Хеширование и проверка пароля - успех")
    void testHashAndVerifyPassword_Success() {
        String password = "testPassword123";

        String hashed = PasswordAndTokenUtil.hashPassword(password);
        assertNotNull(hashed);
        assertNotEquals(password, hashed);

        boolean verified = PasswordAndTokenUtil.verifyPassword(password, hashed);
        assertTrue(verified);
    }

    @Test
    @DisplayName("Проверка неправильного пароля")
    void testVerifyPassword_Wrong() {
        String password = "testPassword123";
        String hashed = PasswordAndTokenUtil.hashPassword(password);

        boolean verified = PasswordAndTokenUtil.verifyPassword("wrongPassword", hashed);
        assertFalse(verified);
    }

    @Test
    @DisplayName("Генерация токена для клиента")
    void testGenerateClientToken() {
        Long clientId = 1L;
        String email = "client@example.com";

        String token = PasswordAndTokenUtil.generateClientToken(clientId, email);
        assertNotNull(token);

        Long extractedId = PasswordAndTokenUtil.getUserIdFromToken(token);
        assertEquals(clientId, extractedId);

        String userType = PasswordAndTokenUtil.getUserTypeFromToken(token);
        assertEquals("client", userType);

        boolean isValid = PasswordAndTokenUtil.isTokenValid(token);
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Генерация токена для магазина")
    void testGenerateShopToken() {
        Long shopId = 2L;
        String email = "shop@example.com";

        String token = PasswordAndTokenUtil.generateShopToken(shopId, email);
        assertNotNull(token);

        Long extractedId = PasswordAndTokenUtil.getUserIdFromToken(token);
        assertEquals(shopId, extractedId);

        String userType = PasswordAndTokenUtil.getUserTypeFromToken(token);
        assertEquals("shop", userType);

        boolean isValid = PasswordAndTokenUtil.isTokenValid(token);
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Генерация токена для курьера")
    void testGenerateCourierToken() {
        Long courierId = 3L;
        String phone = "+79001234567";

        String token = PasswordAndTokenUtil.generateCourierToken(courierId, phone);
        assertNotNull(token);

        Long extractedId = PasswordAndTokenUtil.getUserIdFromToken(token);
        assertEquals(courierId, extractedId);

        String userType = PasswordAndTokenUtil.getUserTypeFromToken(token);
        assertEquals("courier", userType);

        boolean isValid = PasswordAndTokenUtil.isTokenValid(token);
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Невалидный токен")
    void testInvalidToken() {
        String invalidToken = "invalid.jwt.token";

        Long userId = PasswordAndTokenUtil.getUserIdFromToken(invalidToken);
        assertNull(userId);

        String userType = PasswordAndTokenUtil.getUserTypeFromToken(invalidToken);
        assertNull(userType);

        boolean isValid = PasswordAndTokenUtil.isTokenValid(invalidToken);
        assertFalse(isValid);
    }
}
