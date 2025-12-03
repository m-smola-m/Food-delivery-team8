package com.team8.fooddelivery.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.SecretKey;
import java.util.Date;

public class PasswordAndTokenUtil {

    private static final String SECRET_KEY = "your-secret-key-min-32-chars-12345"; // TODO: Переместить в конфиг
    private static final long JWT_EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24 часа
    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * Хеширует пароль с использованием BCrypt
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Проверяет пароль против хешированного пароля
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    /**
     * Генерирует JWT токен для клиента
     */
    public static String generateClientToken(Long clientId, String email) {
        return generateToken("client", clientId, email);
    }

    /**
     * Генерирует JWT токен для магазина
     */
    public static String generateShopToken(Long shopId, String email) {
        return generateToken("shop", shopId, email);
    }

    /**
     * Генерирует JWT токен для курьера
     */
    public static String generateCourierToken(Long courierId, String phone) {
        return generateToken("courier", courierId, phone);
    }

    /**
     * Приватный метод для генерации токена
     */
    private static String generateToken(String userType, Long userId, String identifier) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("type", userType)
                .claim("identifier", identifier)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Извлекает userId из токена
     */
    public static Long getUserIdFromToken(String token) {
        try {
            String subject = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            return Long.parseLong(subject);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Проверяет валидность токена
     */
    public static boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Извлекает тип пользователя из токена
     */
    public static String getUserTypeFromToken(String token) {
        try {
            return (String) Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("type");
        } catch (Exception e) {
            return null;
        }
    }
}

